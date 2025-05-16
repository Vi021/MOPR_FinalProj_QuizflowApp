# Quizflow - Ứng dụng trắc nghiệm di động

## Tổng quan
Quizflow là một ứng dụng trắc nghiệm di động được phát triển cho Android, cho phép người dùng tạo, chia sẻ và tham gia các bài trắc nghiệm. Ứng dụng cung cấp cả chế độ chơi đơn và chế độ chơi nhiều người, khiến việc học trở nên tương tác và hấp dẫn.

## Chức năng chính
### 1.1 Đăng nhập, đăng ký, quên mật khẩu
Hệ thống xác thực người dùng đầy đủ với OTP qua email. Người dùng có thể tạo tài khoản mới, đăng nhập vào tài khoản hiện có, hoặc khôi phục mật khẩu đã quên thông qua xác minh email.

### 1.2 Chỉnh sửa thông tin cá nhân
Người dùng có thể cập nhật thông tin hồ sơ như tên hiển thị, ảnh đại diện và thông tin cá nhân khác. Ứng dụng cho phép tùy chỉnh hồ sơ để tạo trải nghiệm cá nhân hóa.

### 1.3 Thay đổi mật khẩu
Tính năng bảo mật cho phép người dùng thay đổi mật khẩu trực tiếp trong ứng dụng, đảm bảo tài khoản luôn được bảo vệ. Quá trình thay đổi mật khẩu được thiết kế đơn giản và an toàn.

### 1.4 Xem danh sách bộ câu hỏi theo thể loại
Các bài trắc nghiệm được phân loại theo chủ đề/thể loại giúp người dùng dễ dàng tìm kiếm nội dung mình quan tâm. Người dùng có thể duyệt qua danh sách các bộ câu hỏi được tổ chức theo nhiều chủ đề khác nhau.

### 1.5 Tạo bộ câu hỏi, chỉnh sửa câu hỏi
Công cụ soạn thảo trực quan cho phép người dùng tạo mới và chỉnh sửa bài trắc nghiệm của riêng mình. Hỗ trợ nhiều loại câu hỏi, thiết lập điểm số và tùy chọn chia sẻ công khai/riêng tư.

### 1.6 Bộ sưu tập
Người dùng có thể lưu lại các bộ câu hỏi yêu thích hoặc muốn làm lại sau vào bộ sưu tập cá nhân. Tính năng này giúp quản lý và theo dõi các bài trắc nghiệm mà người dùng quan tâm.

### 1.7 Chức năng tìm kiếm
Tìm kiếm nhanh chóng các bài trắc nghiệm bằng từ khóa, tên người tạo hoặc chủ đề. Hệ thống tìm kiếm thông minh giúp người dùng dễ dàng tiếp cận nội dung mong muốn.

### 1.8 Xếp hạng
Bảng xếp hạng hiển thị thành tích của người dùng dựa trên điểm số và hoạt động. Có các xếp hạng theo ngày, tuần và tổng thời gian, tạo động lực cho người dùng tham gia tích cực.

### 1.9 Chơi đơn
Chế độ chơi đơn cho phép người dùng làm bài trắc nghiệm theo tốc độ của riêng mình. Sau khi hoàn thành, người dùng có thể xem kết quả, giải thích đáp án và thống kê hiệu suất.

### 1.10 Chơi nhiều người
Chế độ chơi nhiều người thời gian thực cho phép cạnh tranh trực tiếp với những người dùng khác. Người chơi tham gia vào cùng một phòng và thi đấu, xem kết quả và thứ hạng sau mỗi câu hỏi.

## Công nghệ sử dụng
- Java (Android SDK)
- Retrofit2 cho giao tiếp API
- STOMP cho kết nối WebSocket
- RxJava/RxAndroid cho lập trình phản ứng
- Glide để tải và lưu trữ hình ảnh
- Các thành phần Material Design
- CircleImageView cho hình ảnh hồ sơ tròn
- ChipNavBar cho điều hướng

## Yêu cầu
- Android 7.0 (API level 24) trở lên
- Kết nối internet cho tính năng chơi nhiều người và đồng bộ hóa nội dung

## Cài đặt
1. Clone repository
   ```
   git clone https://github.com/yourusername/MOPR_FinalProj_QuizflowApp.git
   ```
2. Mở dự án trong Android Studio
3. Kết nối thiết bị hoặc sử dụng máy ảo
4. Build và chạy ứng dụng

## Sử dụng
1. Khởi động ứng dụng và đăng nhập hoặc tạo tài khoản mới
2. Duyệt các bài trắc nghiệm có sẵn theo chủ đề hoặc sử dụng chức năng tìm kiếm
3. Làm bài trắc nghiệm ở chế độ chơi đơn hoặc mời bạn bè tham gia chế độ chơi nhiều người
4. Tạo bài trắc nghiệm của riêng bạn bằng trình soạn thảo
5. Xem điểm số và theo dõi tiến trình của bạn

## Cấu trúc dự án
- `activities/`: Chứa tất cả các activity (màn hình) của ứng dụng
- `adapters/`: Các adapter RecyclerView để hiển thị danh sách
- `fragments/`: Các fragment UI cho các thành phần có thể tái sử dụng
- `models/`: Các mô hình dữ liệu đại diện cho các thực thể như bài trắc nghiệm và người dùng
- `requests/`: Các mô hình yêu cầu API
- `respones/`: Các mô hình phản hồi API
- `utils/`: Các lớp tiện ích và hàm hỗ trợ
