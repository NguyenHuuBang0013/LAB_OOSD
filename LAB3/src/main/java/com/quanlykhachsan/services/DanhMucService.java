package com.quanlykhachsan.services;

import com.quanlykhachsan.data.Db;
import javax.swing.table.DefaultTableModel;

public class DanhMucService {
    public DefaultTableModel layKhuVuc() {
        return Db.query("SELECT * FROM KhuVuc ORDER BY MaKhuVuc");
    }
    public DefaultTableModel layNhanVien() {
        return Db.query("SELECT * FROM NhanVien ORDER BY MaNV");
    }
    public DefaultTableModel layLoaiTienNghi() {
        return Db.query("SELECT * FROM LoaiTienNghi ORDER BY MaLoaiTN");
    }
    public DefaultTableModel layDichVu() {
        return Db.query("SELECT * FROM DichVu ORDER BY MaDV");
    }
    public DefaultTableModel layQuyDinh() {
        return Db.query("""
            SELECT q.MaQuyDinh,q.MaLoaiTN,l.TenLoaiTN,q.MucDoThietHai,q.MucDenBu
            FROM QuyDinhDenBu q JOIN LoaiTienNghi l ON q.MaLoaiTN=l.MaLoaiTN
            ORDER BY q.MaQuyDinh""");
    }
    public Result themKhu(String ma,String ten) {
        if (blank(ma)||blank(ten)) return Result.fail("Mã khu vực và tên khu vực không được để trống.");
        try { Db.execute("INSERT INTO KhuVuc(MaKhuVuc,TenKhuVuc) VALUES(?,?)",ma,ten); return Result.ok("Đã thêm khu vực."); }
        catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result themNhanVien(String ma,String ten,String vaiTro,String sdt) {
        if(blank(ma)||blank(ten)||blank(vaiTro)) return Result.fail("Thông tin nhân viên chưa đầy đủ.");
        try { Db.execute("INSERT INTO NhanVien(MaNV,HoTen,VaiTro,SoDienThoai) VALUES(?,?,?,?)",ma,ten,vaiTro,blank(sdt)?null:sdt); return Result.ok("Đã thêm nhân viên.");}
        catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result themLoai(String ma,String ten) {
        if(blank(ma)||blank(ten)) return Result.fail("Thông tin loại tiện nghi chưa đủ.");
        try {Db.execute("INSERT INTO LoaiTienNghi VALUES(?,?)",ma,ten);return Result.ok("Đã thêm loại tiện nghi.");}
        catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result themDichVu(String ma,String ten,String dvt,double gia) {
        if(blank(ma)||blank(ten)||blank(dvt)||gia<0) return Result.fail("Thông tin dịch vụ không hợp lệ.");
        try {Db.execute("INSERT INTO DichVu VALUES(?,?,?,?)",ma,ten,dvt,gia);return Result.ok("Đã thêm dịch vụ.");}
        catch(Exception e){return Result.fail(e.getMessage());}
    }
    public Result themQuyDinh(String ma,String loai,String muc,double tien) {
        if(blank(ma)||blank(loai)||blank(muc)||tien<0) return Result.fail("Quy định đền bù không hợp lệ.");
        try {Db.execute("INSERT INTO QuyDinhDenBu VALUES(?,?,?,?)",ma,loai,muc,tien);return Result.ok("Đã thêm quy định đền bù.");}
        catch(Exception e){return Result.fail(e.getMessage());}
    }
    private boolean blank(String s){return s==null||s.trim().isEmpty();}
}
