package com.quanlykhachsan.services;

import com.quanlykhachsan.data.Db;
import javax.swing.table.DefaultTableModel;
import java.util.Date;

public class ThongKeService {
    public DefaultTableModel tongHop(Date tu,Date den){return Db.query("""
        SELECT
        (SELECT COUNT(*) FROM PhieuDatPhong WHERE CAST(NgayLap AS date) BETWEEN ? AND ?) SoPhieuDat,
        (SELECT COUNT(*) FROM PhieuDatPhong WHERE TrangThai=N'Đang ở') DangO,
        (SELECT COUNT(*) FROM HoaDon WHERE CAST(NgayLap AS date) BETWEEN ? AND ?) SoHoaDon,
        (SELECT ISNULL(SUM(TongTien),0) FROM HoaDon WHERE CAST(NgayLap AS date) BETWEEN ? AND ?) DoanhThuHoaDon,
        (SELECT ISNULL(SUM(TongTien),0) FROM PhieuDenBu WHERE CAST(NgayLap AS date) BETWEEN ? AND ?) TongDenBu
        """,tu,den,tu,den,tu,den,tu,den);}
    public DefaultTableModel dichVu(Date tu,Date den){return Db.query("""
        SELECT d.MaDV,d.TenDV,SUM(c.SoLuong) TongSoLuong,SUM(c.ThanhTien) TongTien
        FROM PhieuSuDungDV p JOIN ChiTietPhieuSuDungDV c ON p.SoPhieuSDDV=c.SoPhieuSDDV
        JOIN DichVu d ON c.MaDV=d.MaDV WHERE p.NgaySuDung BETWEEN ? AND ?
        GROUP BY d.MaDV,d.TenDV ORDER BY TongTien DESC""",tu,den);}
}
