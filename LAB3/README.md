# Hệ thống quản lý khách sạn - Java

## Công nghệ
- Java 17
- Java Swing
- JDBC
- SQL Server
- Maven

## Kiến trúc
UI (Swing) -> Service -> Data/Db -> SQL Server

## Cấu hình database
Mở:
`src/main/resources/db.properties`

Mặc định:
- server: localhost
- port: 1433
- database: QuanLyKhachSan
- integratedSecurity=false
- user=sa
- password=123456

Nếu dùng Windows Authentication, có thể đổi JDBC URL theo máy của bạn.

## Chạy
1. Tạo database `QuanLyKhachSan` và chạy script SQL trong đề.
2. Sửa `db.properties`.
3. Mở project bằng IntelliJ IDEA / NetBeans / Eclipse có Maven.
4. Reload Maven.
5. Chạy `com.quanlykhachsan.Main`.

Hoặc:
`mvn clean compile exec:java`

## Các module
- FrmMain
- FrmDanhMuc
- FrmPhongTienNghi
- FrmDatPhong
- FrmDichVu
- FrmTraPhong
- FrmThongKe

Các nghiệp vụ chính bám theo đề: đặt phòng, kiểm tra sức chứa và trùng lịch, nhận phòng/no-show, lắp đặt tiện nghi, cộng dồn dịch vụ theo ngày, đền bù, hóa đơn, nhiều giao dịch thanh toán và trả phòng.
