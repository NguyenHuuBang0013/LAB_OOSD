# Hệ thống quản lý khách sạn - Java
## 1. Thông tin sinh viên
Họ tên: Nguyễn Hữu Bằng
MSSV: 1250080013
Tên bài Lab: Bài 3 – Hệ thống quản lý khách sạn
## 2. Môi trường / phiên bản
Ngôn ngữ: Java
JDK: Java 17
Giao diện: Java Swing
Kết nối CSDL: JDBC
Cơ sở dữ liệu: Microsoft SQL Server
JDBC Driver: Microsoft SQL Server JDBC 12.8.1.jre11
Công cụ build: Maven
Database mặc định: QuanLyKhachSan
SQL Server mặc định: localhost:1433
## 3. Công nghệ
Java 17
Java Swing
JDBC
SQL Server
Maven
## 4. Kiến trúc
Ứng dụng được tổ chức theo mô hình:
UI (Swing) → Service → Data/Db → SQL Server
Trong đó:
UI (Swing): Hiển thị giao diện và tiếp nhận thao tác từ người dùng.
Service: Xử lý nghiệp vụ của hệ thống.
Data/Db: Thực hiện kết nối và truy vấn cơ sở dữ liệu thông qua JDBC.
SQL Server: Lưu trữ dữ liệu của hệ thống quản lý khách sạn.
## 5. Cấu hình database
Mở file:
src/main/resources/db.properties
Cấu hình mặc định:
Server: localhost
Port: 1433
Database: QuanLyKhachSan
Integrated Security: false
User: sa
Password: Mật khẩu tài khoản SQL Server trên máy chạy chương trình.
Mẫu cấu hình:
db.url=jdbc:sqlserver://localhost:1433;databaseName=QuanLyKhachSan;encrypt=false;trustServerCertificate=true
db.user=sa
db.password=YOUR_PASSWORD
Thay YOUR_PASSWORD bằng mật khẩu thực tế của tài khoản SQL Server trên máy kiểm tra.
Nếu sử dụng Windows Authentication, có thể thay đổi JDBC URL và các thuộc tính kết nối phù hợp với cấu hình SQL Server trên máy.
## 6. Nội dung đã thực hiện
Đã xây dựng ứng dụng quản lý khách sạn bằng Java theo yêu cầu của bài Lab, gồm các chức năng chính:
Xây dựng màn hình chính và điều hướng các chức năng.
Quản lý Khu vực, Nhân viên, Loại tiện nghi, Dịch vụ, Quy định đền bù.
Quản lý Phòng, Tiện nghi và Lắp đặt/Luân chuyển.
Quản lý Khách hàng, Đặt phòng, Nhận phòng và Người lưu trú.
Kiểm tra sức chứa phòng và trùng lịch đặt phòng.
Xử lý nhận phòng/no-show theo nghiệp vụ của đề.
Ghi nhận sử dụng dịch vụ, trong đó số lượng dịch vụ cùng ngày được cộng dồn.
Thực hiện trả phòng, kiểm tra tiện nghi và tính tiền đền bù.
Lập hóa đơn, hỗ trợ nhiều giao dịch thanh toán và các phương thức thanh toán theo nghiệp vụ.
Chỉ cho phép hoàn tất trả phòng sau khi hóa đơn được thanh toán đầy đủ.
Thực hiện thống kê đặt phòng, khách đang ở, hóa đơn, doanh thu, đền bù và sử dụng dịch vụ.
Giao diện các form được xây dựng theo bố cục của đề bài.
## 7. Kết quả
Ứng dụng chạy được bằng class com.quanlykhachsan.Main.
Kết nối thành công với SQL Server thông qua JDBC khi cấu hình đúng tài khoản và mật khẩu.
Các form chính đã được xây dựng và liên kết với các tầng Service và Data/Db.
Có thể kiểm tra các nghiệp vụ theo quy trình:
Danh mục → Phòng/Tiện nghi → Đặt/Nhận phòng → Sử dụng dịch vụ → Trả phòng/Thanh toán → Thống kê
## 8. Các module
FrmMain
FrmDanhMuc
FrmPhongTienNghi
FrmDatPhong
FrmDichVu
FrmTraPhong
FrmThongKe
Các nghiệp vụ chính bám theo đề: đặt phòng, kiểm tra sức chứa và trùng lịch, nhận phòng/no-show, lắp đặt tiện nghi, cộng dồn dịch vụ theo ngày, đền bù, hóa đơn, nhiều giao dịch thanh toán và trả phòng.
## 9. Lỗi gặp phải và cách khắc phục
Lỗi 1: Login failed for user 'sa'
Nguyên nhân: Thông tin đăng nhập SQL Server trong db.properties không đúng với tài khoản SQL Server đang sử dụng.
Cách khắc phục: Mở:
src/main/resources/db.properties
và cập nhật lại db.user, db.password theo tài khoản SQL Server trên máy chạy chương trình.
Lỗi 2: Lỗi cú pháp Java Text Block trong TraPhongService.java
Nguyên nhân: Chuỗi """ được đặt sai vị trí trong câu lệnh SQL nhiều dòng.
Cách khắc phục: Đặt dấu """ mở và đóng đúng cú pháp Text Block của Java 17, sau đó biên dịch lại project.
## 10. Hướng dẫn để giảng viên chạy/kiểm tra lại
Bước 1 – Chuẩn bị môi trường
Cài đặt:
JDK 17
Maven
Microsoft SQL Server
Một IDE hỗ trợ Java và Maven, ví dụ IntelliJ IDEA, NetBeans hoặc Eclipse.
Bước 2 – Chuẩn bị database
Tạo database tên QuanLyKhachSan trên SQL Server.
Chạy script SQL tạo bảng và dữ liệu mẫu theo đề bài.
Đảm bảo SQL Server đang chạy ở cổng 1433, hoặc chỉnh lại JDBC URL trong db.properties nếu dùng cổng khác.
Bước 3 – Cấu hình kết nối
Mở:
src/main/resources/db.properties
và cấu hình:
db.url=jdbc:sqlserver://localhost:1433;databaseName=QuanLyKhachSan;encrypt=false;trustServerCertificate=true
db.user=sa
db.password=YOUR_PASSWORD
Bước 4 – Mở project
Giải nén project.
Mở thư mục project bằng IntelliJ IDEA, NetBeans hoặc Eclipse.
Đảm bảo IDE nhận diện đây là project Maven.
Reload/Reimport Maven để tải các thư viện cần thiết.
Bước 5 – Chạy chương trình
Chạy class:
com.quanlykhachsan.Main
Hoặc mở terminal tại thư mục project và chạy:
mvn clean compile
mvn exec:java
Bước 6 – Kiểm tra chức năng
Có thể kiểm tra theo thứ tự:
Danh mục → kiểm tra Khu vực, Nhân viên, Loại tiện nghi, Dịch vụ, Quy định đền bù.
Phòng - Tiện nghi → kiểm tra phòng, tiện nghi và lắp đặt/luân chuyển.
Đặt / Nhận phòng → tạo khách hàng, đặt phòng, nhận phòng và người lưu trú.
Sử dụng dịch vụ → ghi nhận dịch vụ cho khách đang lưu trú và kiểm tra cộng dồn số lượng cùng ngày.
Trả phòng - Thanh toán → kiểm tra tiện nghi, đền bù, hóa đơn và thanh toán.
Thống kê → chọn khoảng ngày và kiểm tra các số liệu tổng hợp.
## 11. Cấu trúc project
QuanLyKhachSan/
├── pom.xml
├── README.md
├── .gitignore
└── src/main/
    ├── resources/
    │   └── db.properties
    └── java/com/quanlykhachsan/
        ├── Main.java
        ├── data/Db.java
        ├── services/
        │   ├── Result.java
        │   ├── Item.java
        │   ├── DanhMucService.java
        │   ├── PhongTienNghiService.java
        │   ├── DatPhongService.java
        │   ├── DichVuService.java
        │   ├── TraPhongService.java
        │   └── ThongKeService.java
        └── forms/
            ├── UI.java
            ├── FrmMain.java
            ├── FrmDanhMuc.java
            ├── FrmPhongTienNghi.java
            ├── FrmDatPhong.java
            ├── FrmDichVu.java
            ├── FrmTraPhong.java
            └── FrmThongKe.java