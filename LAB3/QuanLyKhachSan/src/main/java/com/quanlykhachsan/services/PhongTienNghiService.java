package com.quanlykhachsan.services;

import com.quanlykhachsan.data.Db;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PhongTienNghiService {
    public DefaultTableModel layPhong(){return Db.query("""
        SELECT p.*,k.TenKhuVuc FROM Phong p JOIN KhuVuc k ON p.MaKhuVuc=k.MaKhuVuc ORDER BY p.SoPhong""");}
    public DefaultTableModel layTienNghi(){return Db.query("""
        SELECT t.*,l.TenLoaiTN FROM TienNghi t JOIN LoaiTienNghi l ON t.MaLoaiTN=l.MaLoaiTN ORDER BY t.MaTienNghi""");}
    public DefaultTableModel layLapDat(){return Db.query("""
        SELECT p.SoPhieuLapDat,p.MaTienNghi,l.TenLoaiTN,p.SoPhong,p.NgayLap,p.TinhTrang,p.MaNV,p.GhiChu
        FROM PhieuLapDat p JOIN TienNghi t ON p.MaTienNghi=t.MaTienNghi
        JOIN LoaiTienNghi l ON t.MaLoaiTN=l.MaLoaiTN ORDER BY p.NgayLap DESC""");}
    public Result themPhong(String so,String khu,int max,double gia){
        if(blank(so)||blank(khu)||max<=0||gia<0)return Result.fail("Thông tin phòng không hợp lệ.");
        try{Db.execute("INSERT INTO Phong(SoPhong,MaKhuVuc,SoNguoiToiDa,DonGiaNgay,TrangThai) VALUES(?,?,?,?,N'Trống')",so,khu,max,gia);return Result.ok("Đã thêm phòng.");}
        catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result themTienNghi(String ma,String loai,int stt,String tt){
        if(blank(ma)||blank(loai)||stt<=0)return Result.fail("Thông tin tiện nghi không hợp lệ.");
        try{Db.execute("INSERT INTO TienNghi(MaTienNghi,MaLoaiTN,SoThuTu,TinhTrangHienTai) VALUES(?,?,?,?)",ma,loai,stt,tt);return Result.ok("Đã thêm tiện nghi.");}
        catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result lapDat(String soPhieu,String maTN,String soPhong,java.util.Date ngay,String tt,String maNV,String ghiChu){
        if(blank(soPhieu)||blank(maTN)||blank(soPhong)||blank(tt)||blank(maNV))return Result.fail("Phiếu lắp đặt chưa đủ thông tin.");
        try{
            Db.tx(c->{
                try(PreparedStatement s=c.prepareStatement("INSERT INTO PhieuLapDat VALUES(?,?,?,?,?,?,?)")){
                    Db.bind(s,soPhieu,maTN,soPhong,new java.sql.Date(ngay.getTime()),tt,maNV,blank(ghiChu)?null:ghiChu);s.executeUpdate();
                }
                try(PreparedStatement s=c.prepareStatement("UPDATE TienNghi SET TinhTrangHienTai=? WHERE MaTienNghi=?")){
                    Db.bind(s,tt,maTN);s.executeUpdate();
                }
                return null;
            });
            return Result.ok("Đã lập phiếu lắp đặt.");
        }catch(Exception e){
            String m=e.getMessage();
            if(m!=null&&(m.contains("2627")||m.contains("2601")||m.contains("UNIQUE")))return Result.fail("Thiết bị này đã được lắp cho một phòng khác trong ngày đã chọn.");
            return Result.fail(m);
        }
    }
    private boolean blank(String s){return s==null||s.trim().isEmpty();}
}
