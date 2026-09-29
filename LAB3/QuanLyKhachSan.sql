USE master;
GO

-- 1. XÓA VÀ TẠO LẠI DATABASE
IF DB_ID(N'QuanLyKhachSan') IS NOT NULL
BEGIN
    ALTER DATABASE QuanLyKhachSan SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyKhachSan;
END
GO

CREATE DATABASE QuanLyKhachSan;
GO

USE QuanLyKhachSan;
GO

-- 2. TẠO BẢNG DỮ LIỆU
CREATE TABLE NhanVien(
    MaNV varchar(20) NOT NULL PRIMARY KEY,
    HoTen nvarchar(120) NOT NULL,
    VaiTro nvarchar(50) NOT NULL,
    SoDienThoai varchar(20) NULL
);

CREATE TABLE KhuVuc(
    MaKhuVuc varchar(20) NOT NULL PRIMARY KEY,
    TenKhuVuc nvarchar(100) NOT NULL UNIQUE
);

CREATE TABLE Phong(
    SoPhong varchar(20) NOT NULL PRIMARY KEY,
    MaKhuVuc varchar(20) NOT NULL,
    SoNguoiToiDa int NOT NULL CHECK(SoNguoiToiDa > 0),
    DonGiaNgay decimal(18,2) NOT NULL CHECK(DonGiaNgay >= 0),
    TrangThai nvarchar(30) NOT NULL DEFAULT N'Trống',
    CONSTRAINT CK_Phong_TrangThai CHECK(TrangThai IN (N'Trống', N'Đã đặt', N'Đang ở', N'Bảo trì')),
    CONSTRAINT FK_Phong_KhuVuc FOREIGN KEY(MaKhuVuc) REFERENCES KhuVuc(MaKhuVuc)
);

CREATE TABLE LoaiTienNghi(
    MaLoaiTN varchar(20) NOT NULL PRIMARY KEY,
    TenLoaiTN nvarchar(100) NOT NULL UNIQUE
);

CREATE TABLE TienNghi(
    MaTienNghi varchar(30) NOT NULL PRIMARY KEY,
    MaLoaiTN varchar(20) NOT NULL,
    SoThuTu int NOT NULL,
    TinhTrangHienTai nvarchar(100) NULL,
    CONSTRAINT UQ_TienNghi_Loai_STT UNIQUE(MaLoaiTN, SoThuTu),
    CONSTRAINT FK_TienNghi_Loai FOREIGN KEY(MaLoaiTN) REFERENCES LoaiTienNghi(MaLoaiTN)
);

CREATE TABLE PhieuLapDat(
    SoPhieuLapDat varchar(30) NOT NULL PRIMARY KEY,
    MaTienNghi varchar(30) NOT NULL,
    SoPhong varchar(20) NOT NULL,
    NgayLap date NOT NULL,
    TinhTrang nvarchar(100) NOT NULL,
    MaNV varchar(20) NOT NULL,
    GhiChu nvarchar(250) NULL,
    CONSTRAINT UQ_PhieuLapDat_ThietBi_Ngay UNIQUE(MaTienNghi, NgayLap),
    CONSTRAINT FK_PhieuLapDat_TienNghi FOREIGN KEY(MaTienNghi) REFERENCES TienNghi(MaTienNghi),
    CONSTRAINT FK_PhieuLapDat_Phong FOREIGN KEY(SoPhong) REFERENCES Phong(SoPhong),
    CONSTRAINT FK_PhieuLapDat_NV FOREIGN KEY(MaNV) REFERENCES NhanVien(MaNV)
);

CREATE TABLE KhachHang(
    MaKhach varchar(20) NOT NULL PRIMARY KEY,
    HoTen nvarchar(120) NOT NULL,
    SoCMND varchar(30) NOT NULL UNIQUE,
    QuocTich nvarchar(80) NOT NULL,
    SoDienThoai varchar(20) NULL
);

CREATE TABLE PhieuDatPhong(
    SoPhieuDat varchar(30) NOT NULL PRIMARY KEY,
    MaKhach varchar(20) NOT NULL,
    MaNVLeTan varchar(20) NOT NULL,
    NgayLap datetime NOT NULL,
    NgayNhan date NOT NULL,
    NgayTraDuKien date NOT NULL,
    TienCoc decimal(18,2) NOT NULL DEFAULT 0 CHECK(TienCoc >= 0),
    KenhDat nvarchar(20) NOT NULL,
    TrangThai nvarchar(30) NOT NULL DEFAULT N'Đã đặt',
    NgayNhanThucTe datetime NULL,
    NgayTraThucTe datetime NULL,
    CONSTRAINT CK_PhieuDat_Ngay CHECK(NgayTraDuKien >= NgayNhan),
    CONSTRAINT CK_PhieuDat_Kenh CHECK(KenhDat IN (N'Điện thoại', N'Website', N'Trực tiếp')),
    CONSTRAINT CK_PhieuDat_TrangThai CHECK(TrangThai IN (N'Đã đặt', N'Đang ở', N'Đã trả', N'No-show', N'Hủy')),
    CONSTRAINT FK_PhieuDat_Khach FOREIGN KEY(MaKhach) REFERENCES KhachHang(MaKhach),
    CONSTRAINT FK_PhieuDat_NV FOREIGN KEY(MaNVLeTan) REFERENCES NhanVien(MaNV)
);

CREATE TABLE ChiTietDatPhong(
    SoPhieuDat varchar(30) NOT NULL,
    SoPhong varchar(20) NOT NULL,
    SoNguoi int NOT NULL CHECK(SoNguoi > 0),
    PRIMARY KEY (SoPhieuDat, SoPhong),
    CONSTRAINT FK_CTDat_Phieu FOREIGN KEY(SoPhieuDat) REFERENCES PhieuDatPhong(SoPhieuDat),
    CONSTRAINT FK_CTDat_Phong FOREIGN KEY(SoPhong) REFERENCES Phong(SoPhong)
);

CREATE TABLE NguoiLuuTru(
    MaNguoiLT int IDENTITY(1,1) NOT NULL PRIMARY KEY,
    SoPhieuDat varchar(30) NOT NULL,
    SoPhong varchar(20) NOT NULL,
    HoTen nvarchar(120) NOT NULL,
    SoCMND varchar(30) NOT NULL,
    QuocTich nvarchar(80) NOT NULL,
    CONSTRAINT FK_NguoiLT_CTDat FOREIGN KEY(SoPhieuDat, SoPhong) REFERENCES ChiTietDatPhong(SoPhieuDat, SoPhong)
);

CREATE TABLE DichVu(
    MaDV varchar(20) NOT NULL PRIMARY KEY,
    TenDV nvarchar(120) NOT NULL,
    DonViTinh nvarchar(40) NOT NULL,
    DonGia decimal(18,2) NOT NULL CHECK(DonGia >= 0)
);

CREATE TABLE PhieuSuDungDV(
    SoPhieuSDDV varchar(30) NOT NULL PRIMARY KEY,
    SoPhieuDat varchar(30) NOT NULL,
    SoPhong varchar(20) NOT NULL,
    NgaySuDung date NOT NULL,
    MaNV varchar(20) NOT NULL,
    CONSTRAINT UQ_PhieuSDDV_PhongNgay UNIQUE(SoPhieuDat, SoPhong, NgaySuDung),
    CONSTRAINT FK_PhieuSDDV_CTDat FOREIGN KEY(SoPhieuDat, SoPhong) REFERENCES ChiTietDatPhong(SoPhieuDat, SoPhong),
    CONSTRAINT FK_PhieuSDDV_NV FOREIGN KEY(MaNV) REFERENCES NhanVien(MaNV)
);

CREATE TABLE ChiTietPhieuSuDungDV(
    SoPhieuSDDV varchar(30) NOT NULL,
    MaDV varchar(20) NOT NULL,
    SoLuong int NOT NULL CHECK(SoLuong > 0),
    DonGia decimal(18,2) NOT NULL CHECK(DonGia >= 0),
    ThanhTien AS (CONVERT(decimal(18,2), SoLuong * DonGia)) PERSISTED,
    PRIMARY KEY (SoPhieuSDDV, MaDV),
    CONSTRAINT FK_CTSDDV_Phieu FOREIGN KEY(SoPhieuSDDV) REFERENCES PhieuSuDungDV(SoPhieuSDDV),
    CONSTRAINT FK_CTSDDV_DV FOREIGN KEY(MaDV) REFERENCES DichVu(MaDV)
);

CREATE TABLE QuyDinhDenBu(
    MaQuyDinh varchar(30) NOT NULL PRIMARY KEY,
    MaLoaiTN varchar(20) NOT NULL,
    MucDoThietHai nvarchar(80) NOT NULL,
    MucDenBu decimal(18,2) NOT NULL CHECK(MucDenBu >= 0),
    CONSTRAINT UQ_QDDB_Loai_MucDo UNIQUE(MaLoaiTN, MucDoThietHai),
    CONSTRAINT FK_QDDB_Loai FOREIGN KEY(MaLoaiTN) REFERENCES LoaiTienNghi(MaLoaiTN)
);

CREATE TABLE PhieuDenBu(
    SoPhieuDenBu varchar(30) NOT NULL PRIMARY KEY,
    SoPhieuDat varchar(30) NOT NULL,
    SoPhong varchar(20) NOT NULL,
    NgayLap datetime NOT NULL,
    MaNV varchar(20) NOT NULL,
    TongTien decimal(18,2) NOT NULL DEFAULT 0 CHECK(TongTien >= 0),
    CONSTRAINT FK_PhieuDB_CTDat FOREIGN KEY(SoPhieuDat, SoPhong) REFERENCES ChiTietDatPhong(SoPhieuDat, SoPhong),
    CONSTRAINT FK_PhieuDB_NV FOREIGN KEY(MaNV) REFERENCES NhanVien(MaNV)
);

CREATE TABLE ChiTietPhieuDenBu(
    SoPhieuDenBu varchar(30) NOT NULL,
    MaTienNghi varchar(30) NOT NULL,
    MucDoThietHai nvarchar(80) NOT NULL,
    SoTien decimal(18,2) NOT NULL CHECK(SoTien >= 0),
    PRIMARY KEY (SoPhieuDenBu, MaTienNghi),
    CONSTRAINT FK_CTDB_Phieu FOREIGN KEY(SoPhieuDenBu) REFERENCES PhieuDenBu(SoPhieuDenBu),
    CONSTRAINT FK_CTDB_TienNghi FOREIGN KEY(MaTienNghi) REFERENCES TienNghi(MaTienNghi)
);

CREATE TABLE HoaDon(
    SoHoaDon varchar(30) NOT NULL PRIMARY KEY,
    SoPhieuDat varchar(30) NOT NULL UNIQUE,
    NgayLap datetime NOT NULL,
    MaNV varchar(20) NOT NULL,
    SoNgayTinhTien int NOT NULL CHECK(SoNgayTinhTien > 0),
    TienPhong decimal(18,2) NOT NULL CHECK(TienPhong >= 0),
    TienDichVu decimal(18,2) NOT NULL CHECK(TienDichVu >= 0),
    TongTien AS (CONVERT(decimal(18,2), TienPhong + TienDichVu)) PERSISTED,
    TrangThai nvarchar(30) NOT NULL DEFAULT N'Chưa thanh toán',
    CONSTRAINT CK_HoaDon_TrangThai CHECK(TrangThai IN (N'Chưa thanh toán', N'Đã thanh toán')),
    CONSTRAINT FK_HoaDon_PhieuDat FOREIGN KEY(SoPhieuDat) REFERENCES PhieuDatPhong(SoPhieuDat),
    CONSTRAINT FK_HoaDon_NV FOREIGN KEY(MaNV) REFERENCES NhanVien(MaNV)
);

CREATE TABLE ThanhToan(
    MaThanhToan varchar(30) NOT NULL PRIMARY KEY,
    SoHoaDon varchar(30) NOT NULL,
    NgayThanhToan datetime NOT NULL,
    HinhThuc nvarchar(30) NOT NULL,
    SoTien decimal(18,2) NOT NULL CHECK(SoTien > 0),
    CONSTRAINT CK_ThanhToan_HinhThuc CHECK(HinhThuc IN (N'Tiền mặt', N'Chuyển khoản', N'Thẻ', N'Ví điện tử')),
    CONSTRAINT FK_ThanhToan_HoaDon FOREIGN KEY(SoHoaDon) REFERENCES HoaDon(SoHoaDon)
);
GO

-- 3. INSERT DỮ LIỆU ĐẦY ĐỦ (MỖI BẢNG 4-5 BẢN GHI)
INSERT INTO NhanVien VALUES 
('NV01', N'Nguyễn Thu Hà', N'Lễ tân', '0901000001'), 
('NV02', N'Trần Minh An', N'Phục vụ phòng', '0901000002'), 
('NV03', N'Lê Hoàng Nam', N'Thanh toán', '0901000003'), 
('NV04', N'Phạm Thùy Linh', N'Lễ tân', '0901000004'),
('NV05', N'Nguyễn Văn Toàn', N'Phục vụ phòng', '0901000005');

INSERT INTO KhuVuc VALUES 
('A', N'Khu A (Tầng cao)'), 
('B', N'Khu B (Sân vườn)'), 
('C', N'Khu C (VIP)'),
('D', N'Khu D (Bờ biển)');

INSERT INTO Phong VALUES 
('A101', 'A', 2, 600000, N'Trống'), 
('A102', 'A', 3, 800000, N'Đang ở'), 
('A103', 'A', 2, 650000, N'Đã đặt'), 
('B201', 'B', 4, 1200000, N'Trống'), 
('B202', 'B', 2, 900000, N'Đang ở'),
('C301', 'C', 2, 2500000, N'Trống');

INSERT INTO LoaiTienNghi VALUES 
('TV', N'Ti vi Smart'), 
('TL', N'Tủ lạnh Mini'), 
('DT', N'Điện thoại bàn'),
('DH', N'Điều hòa inverter');

INSERT INTO TienNghi VALUES 
('TV01', 'TV', 1, N'Tốt'), 
('TV02', 'TV', 2, N'Tốt'), 
('TL01', 'TL', 1, N'Tốt'), 
('DT01', 'DT', 1, N'Tốt'),
('DH01', 'DH', 1, N'Tốt');

INSERT INTO PhieuLapDat VALUES 
('LD001', 'TV01', 'A101', '2026-09-01', N'Mới lắp', 'NV02', N'Lắp TV phòng A101'),
('LD002', 'DH01', 'A101', '2026-09-02', N'Mới lắp', 'NV02', N'Lắp điều hòa A101'),
('LD003', 'TL01', 'A102', '2026-09-03', N'Tốt', 'NV02', N'Lắp tủ lạnh A102');

INSERT INTO KhachHang VALUES 
('KH01', N'Nguyễn Văn An', '031095000001', N'Việt Nam', '0988111222'), 
('KH02', N'Trần Thị Bình', '031098000002', N'Việt Nam', '0988222333'),
('KH03', N'John Smith', 'P987654321', N'Mỹ', '0988333444'),
('KH04', N'Lê Minh Cường', '031092000004', N'Việt Nam', '0988444555');

INSERT INTO PhieuDatPhong VALUES 
('DP001', 'KH01', 'NV01', '2026-09-10 08:00:00', '2026-09-12', '2026-09-14', 500000, N'Website', N'Đang ở', '2026-09-12 14:00:00', NULL), 
('DP002', 'KH02', 'NV01', '2026-09-11 09:30:00', '2026-09-13', '2026-09-15', 800000, N'Trực tiếp', N'Đang ở', '2026-09-13 13:30:00', NULL),
('DP003', 'KH03', 'NV04', '2026-09-15 10:00:00', '2026-09-20', '2026-09-25', 1000000, N'Điện thoại', N'Đã đặt', NULL, NULL),
('DP004', 'KH04', 'NV01', '2026-09-01 07:00:00', '2026-09-01', '2026-09-03', 500000, N'Trực tiếp', N'Đã trả', '2026-09-01 12:00:00', '2026-09-03 11:00:00');

INSERT INTO ChiTietDatPhong VALUES 
('DP001', 'A101', 2), 
('DP002', 'A102', 3),
('DP003', 'A103', 2),
('DP004', 'B201', 2);

INSERT INTO NguoiLuuTru (SoPhieuDat, SoPhong, HoTen, SoCMND, QuocTich) VALUES
('DP001', 'A101', N'Nguyễn Văn An', '031095000001', N'Việt Nam'),
('DP002', 'A102', N'Trần Thị Bình', '031098000002', N'Việt Nam');

INSERT INTO DichVu VALUES 
('DV01', N'Ăn sáng buffet', N'Suất', 120000), 
('DV02', N'Tắm hơi / Massage', N'Lượt', 250000), 
('DV03', N'Karaoke gia đình', N'Giờ', 300000),
('DV04', N'Giặt ủi quần áo', N'Kg', 50000);

INSERT INTO PhieuSuDungDV VALUES 
('SD001', 'DP001', 'A101', '2026-09-12', 'NV01'),
('SD002', 'DP002', 'A102', '2026-09-13', 'NV01');

INSERT INTO ChiTietPhieuSuDungDV VALUES 
('SD001', 'DV01', 2, 120000),
('SD002', 'DV03', 2, 300000);

INSERT INTO QuyDinhDenBu VALUES 
('QD01', 'TV', N'Hư hỏng nhẹ', 500000), 
('QD02', 'TL', N'Hư hỏng nhẹ', 400000),
('QD03', 'DH', N'Mất điều khiển từ xa', 350000);

INSERT INTO HoaDon VALUES 
('HD001', 'DP001', '2026-09-14 09:00:00', 'NV03', 2, 1200000, 240000, N'Chưa thanh toán'),
('HD002', 'DP004', '2026-09-03 11:00:00', 'NV03', 2, 2400000, 300000, N'Đã thanh toán');

INSERT INTO ThanhToan VALUES 
('TT001', 'HD002', '2026-09-03 11:05:00', N'Tiền mặt', 2700000);
GO