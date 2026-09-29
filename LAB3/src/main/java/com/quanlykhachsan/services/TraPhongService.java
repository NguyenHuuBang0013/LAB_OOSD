package com.quanlykhachsan.services;

import com.quanlykhachsan.data.Db;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;

public class TraPhongService {
    public DefaultTableModel layPhieuDangO(){return Db.query("""
        SELECT d.SoPhieuDat,k.HoTen,d.NgayNhanThucTe,d.NgayTraDuKien
        FROM PhieuDatPhong d JOIN KhachHang k ON d.MaKhach=k.MaKhach
        WHERE d.TrangThai=N'Đang ở' ORDER BY d.SoPhieuDat""");}
    public DefaultTableModel layPhongTheoPhieu(String so){return Db.query("""
        SELECT c.SoPhong,p.DonGiaNgay FROM ChiTietDatPhong c JOIN Phong p ON c.SoPhong=p.SoPhong
        WHERE c.SoPhieuDat=?""",so);}
    public DefaultTableModel layTienNghiPhong(String phong){return Db.query("""
        SELECT TOP 100 p.MaTienNghi,l.TenLoaiTN,t.TinhTrangHienTai
        FROM PhieuLapDat p JOIN TienNghi t ON p.MaTienNghi=t.MaTienNghi
        JOIN LoaiTienNghi l ON t.MaLoaiTN=l.MaLoaiTN WHERE p.SoPhong=?
        ORDER BY p.NgayLap DESC""",phong);}
    public DefaultTableModel layHoaDon(){return Db.query("""
        SELECT h.*,k.HoTen FROM HoaDon h JOIN PhieuDatPhong d ON h.SoPhieuDat=d.SoPhieuDat
        JOIN KhachHang k ON d.MaKhach=k.MaKhach ORDER BY h.NgayLap DESC""");}
    public Result lapDenBu(String soDB,String soDat,String phong,java.util.Date ngay,String maNV,List<Item> ds){
        if(blank(soDB)||blank(soDat)||blank(phong)||blank(maNV)||ds==null||ds.isEmpty())return Result.fail("Phiếu đền bù chưa đủ thông tin.");
        try{
            Db.tx(c->{
                double tong=0; for(Item x:ds){if(x.amount<0)throw new Exception("Mức đền bù không hợp lệ.");tong+=x.amount;}
                try(PreparedStatement h=c.prepareStatement("INSERT INTO PhieuDenBu VALUES(?,?,?,?,?,?)")){
                    Db.bind(h,soDB,soDat,phong,new Timestamp(ngay.getTime()),maNV,tong);h.executeUpdate();
                }
                for(Item x:ds){
                    try(PreparedStatement q=c.prepareStatement("INSERT INTO ChiTietPhieuDenBu VALUES(?,?,?,?)")){
                        Db.bind(q,soDB,x.id,x.text,x.amount);q.executeUpdate();
                    }
                }
                return null;
            }); return Result.ok("Đã lập phiếu đền bù.");
        }catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result lapHoaDon(String soHD,String soDat,java.util.Date ngay,String maNV,int soNgay){
        if(blank(soHD)||blank(soDat)||blank(maNV)||soNgay<=0)return Result.fail("Thông tin hóa đơn chưa hợp lệ.");
        try{
            double phong=toDouble(Db.scalar("SELECT ISNULL(SUM(p.DonGiaNgay),0) FROM ChiTietDatPhong c JOIN Phong p ON c.SoPhong=p.SoPhong WHERE c.SoPhieuDat=?",soDat))*soNgay;
            double dv=toDouble(Db.scalar("SELECT ISNULL(SUM(c.ThanhTien),0) FROM PhieuSuDungDV h JOIN ChiTietPhieuSuDungDV c ON h.SoPhieuSDDV=c.SoPhieuSDDV WHERE h.SoPhieuDat=?",soDat));
            Db.execute("""
                    INSERT INTO HoaDon
                    (SoHoaDon, SoPhieuDat, NgayLap, MaNV, SoNgayTinhTien,
                     TienPhong, TienDichVu, TrangThai)
                    VALUES (?, ?, ?, ?, ?, ?, ?, N'Chưa thanh toán')
                    """, soHD, soDat, new Timestamp(ngay.getTime()), maNV, soNgay, phong, dv);
            return Result.ok("Đã lập hóa đơn tiền phòng và dịch vụ.");
        }catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result thanhToan(String maTT,String soHD,java.util.Date ngay,String hinhThuc,double tien){
        if(blank(maTT)||blank(soHD)||blank(hinhThuc)||tien<=0)return Result.fail("Thông tin thanh toán không hợp lệ.");
        try{
            return Db.tx(c->{
                double tong;
                try(PreparedStatement q=c.prepareStatement("SELECT TongTien FROM HoaDon WHERE SoHoaDon=?")){Db.bind(q,soHD);try(ResultSet r=q.executeQuery()){if(!r.next())throw new Exception("Không tìm thấy hóa đơn.");tong=r.getDouble(1);}}
                double da;
                try(PreparedStatement q=c.prepareStatement("SELECT ISNULL(SUM(SoTien),0) FROM ThanhToan WHERE SoHoaDon=?")){Db.bind(q,soHD);try(ResultSet r=q.executeQuery()){r.next();da=r.getDouble(1);}}
                if(da+tien>tong)throw new Exception("Số tiền thanh toán vượt số còn phải trả.");
                try(PreparedStatement q=c.prepareStatement("INSERT INTO ThanhToan VALUES(?,?,?,?,?)")){Db.bind(q,maTT,soHD,new Timestamp(ngay.getTime()),hinhThuc,tien);q.executeUpdate();}
                if(Math.abs(da+tien-tong)<0.001){
                    try(PreparedStatement q=c.prepareStatement("UPDATE HoaDon SET TrangThai=N'Đã thanh toán' WHERE SoHoaDon=?")){Db.bind(q,soHD);q.executeUpdate();}
                }
                return Result.ok("Đã ghi nhận thanh toán bằng "+hinhThuc+".");
            });
        }catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result traPhong(String soDat,java.util.Date ngayTra){
        try{
            Db.tx(c->{
                String soHD,tt;
                try(PreparedStatement q=c.prepareStatement("SELECT SoHoaDon,TrangThai FROM HoaDon WHERE SoPhieuDat=?")){Db.bind(q,soDat);try(ResultSet r=q.executeQuery()){if(!r.next())throw new Exception("Chưa lập hóa đơn cho phiếu đặt phòng.");soHD=r.getString(1);tt=r.getString(2);}}
                if(!"Đã thanh toán".equals(tt))throw new Exception("Hóa đơn chưa thanh toán đủ.");
                try(PreparedStatement q=c.prepareStatement("UPDATE PhieuDatPhong SET TrangThai=N'Đã trả',NgayTraThucTe=? WHERE SoPhieuDat=?")){Db.bind(q,new Timestamp(ngayTra.getTime()),soDat);q.executeUpdate();}
                try(PreparedStatement q=c.prepareStatement("UPDATE Phong SET TrangThai=N'Trống' WHERE SoPhong IN(SELECT SoPhong FROM ChiTietDatPhong WHERE SoPhieuDat=?)")){Db.bind(q,soDat);q.executeUpdate();}
                return null;
            });return Result.ok("Đã hoàn tất trả phòng.");
        }catch(Exception e){return Result.fail(e.getMessage());}
    }
    private double toDouble(Object x){return x==null?0:((Number)x).doubleValue();}
    private boolean blank(String s){return s==null||s.trim().isEmpty();}
}
