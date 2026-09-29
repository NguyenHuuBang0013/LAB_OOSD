package com.quanlykhachsan.services;

import com.quanlykhachsan.data.Db;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DichVuService {
    public DefaultTableModel layPhieuDangO(){return Db.query("""
        SELECT d.SoPhieuDat,k.HoTen,c.SoPhong FROM PhieuDatPhong d JOIN KhachHang k ON d.MaKhach=k.MaKhach
        JOIN ChiTietDatPhong c ON d.SoPhieuDat=c.SoPhieuDat WHERE d.TrangThai=N'Đang ở'
        ORDER BY d.SoPhieuDat,c.SoPhong""");}
    public DefaultTableModel layDichVu(){return Db.query("SELECT * FROM DichVu ORDER BY MaDV");}
    public DefaultTableModel layLichSu(String so){return Db.query("""
        SELECT p.SoPhieuSDDV,p.SoPhong,p.NgaySuDung,d.TenDV,c.SoLuong,c.DonGia,c.ThanhTien
        FROM PhieuSuDungDV p JOIN ChiTietPhieuSuDungDV c ON p.SoPhieuSDDV=c.SoPhieuSDDV
        JOIN DichVu d ON c.MaDV=d.MaDV WHERE p.SoPhieuDat=? ORDER BY p.NgaySuDung,p.SoPhong,d.TenDV""",so);}
    public Result ghiNhan(String soDat,String phong,java.util.Date ngay,String maNV,String maDV,int sl){
        if(blank(soDat)||blank(phong)||blank(maNV)||blank(maDV)||sl<=0)return Result.fail("Thông tin sử dụng dịch vụ không hợp lệ.");
        try{
            Db.tx(c->{
                String trangThai;
                try(PreparedStatement q=c.prepareStatement("SELECT TrangThai FROM PhieuDatPhong WHERE SoPhieuDat=?")){Db.bind(q,soDat);try(ResultSet r=q.executeQuery()){if(!r.next())throw new Exception("Không tìm thấy phiếu.");trangThai=r.getString(1);}}
                if(!"Đang ở".equals(trangThai))throw new Exception("Chỉ ghi nhận dịch vụ cho phiếu đang lưu trú.");
                double gia;
                try(PreparedStatement q=c.prepareStatement("SELECT DonGia FROM DichVu WHERE MaDV=?")){Db.bind(q,maDV);try(ResultSet r=q.executeQuery()){if(!r.next())throw new Exception("Không tìm thấy dịch vụ.");gia=r.getDouble(1);}}
                String so=null;
                try(PreparedStatement q=c.prepareStatement("SELECT SoPhieuSDDV FROM PhieuSuDungDV WHERE SoPhieuDat=? AND SoPhong=? AND NgaySuDung=?")){
                    Db.bind(q,soDat,phong,new java.sql.Date(ngay.getTime()));try(ResultSet r=q.executeQuery()){if(r.next())so=r.getString(1);}
                }
                if(so==null){
                    so="SD"+System.currentTimeMillis();
                    try(PreparedStatement q=c.prepareStatement("INSERT INTO PhieuSuDungDV VALUES(?,?,?,?,?)")){Db.bind(q,so,soDat,phong,new java.sql.Date(ngay.getTime()),maNV);q.executeUpdate();}
                }
                int count;
                try(PreparedStatement q=c.prepareStatement("SELECT COUNT(*) FROM ChiTietPhieuSuDungDV WHERE SoPhieuSDDV=? AND MaDV=?")){Db.bind(q,so,maDV);try(ResultSet r=q.executeQuery()){r.next();count=r.getInt(1);}}
                if(count>0){
                    try(PreparedStatement q=c.prepareStatement("UPDATE ChiTietPhieuSuDungDV SET SoLuong=SoLuong+?,DonGia=? WHERE SoPhieuSDDV=? AND MaDV=?")){Db.bind(q,sl,gia,so,maDV);q.executeUpdate();}
                }else{
                    try(PreparedStatement q=c.prepareStatement("INSERT INTO ChiTietPhieuSuDungDV VALUES(?,?,?,?)")){Db.bind(q,so,maDV,sl,gia);q.executeUpdate();}
                }
                return null;
            });
            return Result.ok("Đã ghi nhận dịch vụ. Nếu cùng dịch vụ được dùng nhiều lần trong ngày, số lượng đã được cộng dồn.");
        }catch(Exception e){return Result.fail(e.getMessage());}
    }
    private boolean blank(String s){return s==null||s.trim().isEmpty();}
}
