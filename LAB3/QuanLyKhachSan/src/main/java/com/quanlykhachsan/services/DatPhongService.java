package com.quanlykhachsan.services;

import com.quanlykhachsan.data.Db;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.*;

public class DatPhongService {
    public DefaultTableModel layKhach(){return Db.query("SELECT * FROM KhachHang ORDER BY HoTen");}
    public DefaultTableModel layPhong(){return Db.query("SELECT p.*,k.TenKhuVuc FROM Phong p JOIN KhuVuc k ON p.MaKhuVuc=k.MaKhuVuc ORDER BY p.SoPhong");}
    public DefaultTableModel layPhieuDat(){return Db.query("SELECT d.*,k.HoTen FROM PhieuDatPhong d JOIN KhachHang k ON d.MaKhach=k.MaKhach ORDER BY d.NgayLap DESC");}
    public DefaultTableModel layChiTiet(String so){return Db.query("SELECT c.*,p.SoNguoiToiDa,p.DonGiaNgay FROM ChiTietDatPhong c JOIN Phong p ON c.SoPhong=p.SoPhong WHERE c.SoPhieuDat=?",so);}
    public DefaultTableModel layNguoi(String so){return Db.query("SELECT * FROM NguoiLuuTru WHERE SoPhieuDat=? ORDER BY SoPhong,MaNguoiLT",so);}
    public Result themKhach(String ma,String ten,String cmnd,String qt,String sdt){
        if(blank(ma)||blank(ten)||blank(cmnd)||blank(qt))return Result.fail("Thông tin khách chưa đầy đủ.");
        try{Db.execute("INSERT INTO KhachHang VALUES(?,?,?,?,?)",ma,ten,cmnd,qt,blank(sdt)?null:sdt);return Result.ok("Đã lưu khách hàng.");}
        catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result taoDatPhong(String so,String maKhach,String maNV,java.util.Date lap,java.util.Date nhan,java.util.Date tra,double coc,String kenh,List<Item> ds){
        if(blank(so)||blank(maKhach)||blank(maNV)||ds==null||ds.isEmpty())return Result.fail("Phiếu đặt phòng chưa đủ thông tin.");
        if(tra.before(nhan))return Result.fail("Ngày trả dự kiến không được trước ngày nhận.");
        try{
            Db.tx(c->{
                for(Item x:ds){
                    int max;
                    try(PreparedStatement q=c.prepareStatement("SELECT SoNguoiToiDa FROM Phong WHERE SoPhong=?")){Db.bind(q,x.id);try(ResultSet r=q.executeQuery()){if(!r.next())throw new Exception("Không tìm thấy phòng "+x.id);max=r.getInt(1);}}
                    int n=(int)x.amount;
                    if(n<=0||n>max)throw new Exception("Số người của phòng "+x.id+" vượt sức chứa.");
                    try(PreparedStatement q=c.prepareStatement("""
                        SELECT COUNT(*) FROM ChiTietDatPhong c JOIN PhieuDatPhong d ON c.SoPhieuDat=d.SoPhieuDat
                        WHERE c.SoPhong=? AND d.TrangThai IN(N'Đã đặt',N'Đang ở')
                        AND ?<=d.NgayTraDuKien AND ? >= d.NgayNhan""")){
                        Db.bind(q,x.id,new java.sql.Date(nhan.getTime()),new java.sql.Date(tra.getTime()));
                        try(ResultSet r=q.executeQuery()){r.next();if(r.getInt(1)>0)throw new Exception("Phòng "+x.id+" bị trùng lịch đặt.");}
                    }
                }
                try(PreparedStatement h=c.prepareStatement("""
                    INSERT INTO PhieuDatPhong(SoPhieuDat,MaKhach,MaNVLeTan,NgayLap,NgayNhan,NgayTraDuKien,TienCoc,KenhDat,TrangThai)
                    VALUES(?,?,?,?,?,?,?,? ,N'Đã đặt')""")){
                    Db.bind(h,so,maKhach,maNV,new Timestamp(lap.getTime()),new java.sql.Date(nhan.getTime()),new java.sql.Date(tra.getTime()),coc,kenh);h.executeUpdate();
                }
                for(Item x:ds){
                    try(PreparedStatement s=c.prepareStatement("INSERT INTO ChiTietDatPhong VALUES(?,?,?)")){Db.bind(s,so,x.id,(int)x.amount);s.executeUpdate();}
                    try(PreparedStatement s=c.prepareStatement("UPDATE Phong SET TrangThai=N'Đã đặt' WHERE SoPhong=?")){Db.bind(s,x.id);s.executeUpdate();}
                }
                return null;
            });
            return Result.ok("Đã lập phiếu đặt phòng.");
        }catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result themNguoi(String so,String phong,String ten,String cmnd,String qt){
        if(blank(so)||blank(phong)||blank(ten)||blank(cmnd)||blank(qt))return Result.fail("Thông tin người lưu trú chưa đầy đủ.");
        try{
            int max=toInt(Db.scalar("SELECT SoNguoi FROM ChiTietDatPhong WHERE SoPhieuDat=? AND SoPhong=?",so,phong));
            int dem=toInt(Db.scalar("SELECT COUNT(*) FROM NguoiLuuTru WHERE SoPhieuDat=? AND SoPhong=?",so,phong));
            if(dem>=max)return Result.fail("Đã đủ số người đăng ký cho phòng này.");
            Db.execute("INSERT INTO NguoiLuuTru(SoPhieuDat,SoPhong,HoTen,SoCMND,QuocTich) VALUES(?,?,?,?,?)",so,phong,ten,cmnd,qt);
            return Result.ok("Đã thêm người lưu trú.");
        }catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result nhanPhong(String so,java.util.Date thucTe){
        try{
            Db.tx(c->{
                int n;
                try(PreparedStatement q=c.prepareStatement("UPDATE PhieuDatPhong SET TrangThai=N'Đang ở',NgayNhanThucTe=? WHERE SoPhieuDat=? AND TrangThai=N'Đã đặt'")){Db.bind(q,new Timestamp(thucTe.getTime()),so);n=q.executeUpdate();}
                if(n==0)throw new Exception("Phiếu không ở trạng thái có thể nhận phòng.");
                try(PreparedStatement u=c.prepareStatement("UPDATE Phong SET TrangThai=N'Đang ở' WHERE SoPhong IN(SELECT SoPhong FROM ChiTietDatPhong WHERE SoPhieuDat=?)")){Db.bind(u,so);u.executeUpdate();}
                return null;
            }); return Result.ok("Đã nhận phòng.");
        }catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result noShow(String so){
        try{
            Db.tx(c->{
                try(PreparedStatement q=c.prepareStatement("UPDATE PhieuDatPhong SET TrangThai=N'No-show' WHERE SoPhieuDat=? AND TrangThai=N'Đã đặt'")){Db.bind(q,so);q.executeUpdate();}
                try(PreparedStatement u=c.prepareStatement("UPDATE Phong SET TrangThai=N'Trống' WHERE SoPhong IN(SELECT SoPhong FROM ChiTietDatPhong WHERE SoPhieuDat=?)")){Db.bind(u,so);u.executeUpdate();}
                return null;
            }); return Result.ok("Đã đánh dấu không nhận phòng.");
        }catch(Exception e){return Result.fail(e.getMessage());}
    }
    private int toInt(Object x){return x==null?0:((Number)x).intValue();}
    private boolean blank(String s){return s==null||s.trim().isEmpty();}
}
