package com.myproject.clinic.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * LỚP: GlobalExceptionHandler
 * MÔ TẢ: Bộ xử lý ngoại lệ tập trung cho toàn bộ ứng dụng (Global Exception Handler).
 * Sử dụng annotation @RestControllerAdvice để tự động bắt các ngoại lệ được ném ra từ tất cả các Controller
 * và chuyển đổi chúng thành phản hồi JSON chuẩn (RESTful Error Response) trả về cho phía client (Web/Mobile App).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý ngoại lệ ResourceNotFoundException (Không tìm thấy tài nguyên dữ liệu).
     * Phản hồi HTTP trả về: 404 NOT FOUND kèm message báo lỗi chi tiết.
     * 
     * @param ex Ngoại lệ bắt được
     * @return Phản hồi JSON chứa thông tin lỗi 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Xử lý ngoại lệ BadCredentialsException (Thông tin đăng nhập không hợp lệ từ Spring Security).
     * Phản hồi HTTP trả về: 401 UNAUTHORIZED.
     * 
     * @param ex Ngoại lệ bắt được khi sai tài khoản/mật khẩu
     * @return Phản hồi JSON thông báo sai tài khoản mật khẩu
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không chính xác");
    }

    /**
     * Xử lý ngoại lệ IllegalArgumentException (Tham số truyền vào không hợp lệ hoặc không đúng logic nghiệp vụ).
     * Phản hồi HTTP trả về: 400 BAD REQUEST.
     * 
     * @param ex Ngoại lệ bắt được
     * @return Phản hồi JSON chứa thông điệp lỗi tham số
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Xử lý ngoại lệ ResponseStatusException (Ngoại lệ ném ra kèm theo Http Status cụ thể của Spring).
     * Phản hồi HTTP trả về: Trả về chính mã trạng thái (HttpStatus) và lý do (reason) của ngoại lệ đó.
     * 
     * @param ex Ngoại lệ ném ra thủ công trong Controller/Service
     * @return Phản hồi JSON chứa mã lỗi và lý do tương ứng
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        return buildResponse((HttpStatus) ex.getStatusCode(), ex.getReason());
    }

    /**
     * Xử lý ngoại lệ MethodArgumentNotValidException (Lỗi validate dữ liệu đầu vào - Bean Validation).
     * Xảy ra khi client gửi dữ liệu không thỏa mãn các ràng buộc định nghĩa bằng các annotation như
     * @NotNull, @Size, @Email, @Min, @Max... trong DTO.
     * Phản hồi HTTP trả về: 400 BAD REQUEST kèm chi tiết danh sách tất cả các trường bị lỗi dữ liệu đầu vào.
     * 
     * @param ex Ngoại lệ validate bắt được
     * @return Phản hồi JSON gồm cấu trúc lỗi phân mảnh theo từng trường dữ liệu lỗi
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Lặp qua danh sách các trường lỗi và gom thông báo lỗi lại dưới dạng Key (tên trường) - Value (mô tả lỗi)
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Xử lý ngoại lệ DataIntegrityViolationException (Vi phạm tính toàn vẹn dữ liệu trong database).
     * Xảy ra khi cố tình insert trùng khoá chính/khoá duy nhất (Unique Constraint) hoặc vi phạm ràng buộc khoá ngoại (Foreign Key).
     * Phản hồi HTTP trả về: 409 CONFLICT.
     * 
     * @param ex Ngoại lệ cơ sở dữ liệu bắt được
     * @return Phản hồi JSON chứa mô tả lỗi ràng buộc dữ liệu
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Không thể thực hiện thao tác này vì vi phạm ràng buộc dữ liệu hoặc dữ liệu liên quan.");
    }

    /**
     * Xử lý ngoại lệ MethodArgumentTypeMismatchException (Sai kiểu dữ liệu của tham số truyền vào từ URL hoặc Query string).
     * Ví dụ: URL yêu cầu ID là số (/api/users/5) nhưng client truyền vào dạng chuỗi chữ (/api/users/abc).
     * Phản hồi HTTP trả về: 400 BAD REQUEST kèm thông tin kiểu dữ liệu đúng mong đợi.
     * 
     * @param ex Ngoại lệ sai kiểu dữ liệu bắt được
     * @return Phản hồi JSON thông báo tham số truyền vào sai kiểu dữ liệu mong đợi
     */
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Sai kiểu dữ liệu: " + ex.getName() + " mong đợi kiểu " + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Long"));
    }

    /**
     * Xử lý ngoại lệ Exception.class (Lỗi máy chủ nội bộ - Internal Server Error).
     * Đây là bộ lọc cuối cùng dùng để bắt tất cả các lỗi runtime chưa được phân loại hoặc không mong muốn khác
     * nhằm ngăn ngừa việc lộ thông tin nhạy cảm của hệ thống ra bên ngoài.
     * Phản hồi HTTP trả về: 500 INTERNAL SERVER ERROR.
     * 
     * @param ex Ngoại lệ chung bắt được
     * @return Phản hồi JSON thông báo lỗi máy chủ hệ thống
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi hệ thống: " + ex.getMessage());
    }

    /**
     * HÀM TIỆN ÍCH: buildResponse
     * MÔ TẢ: Định dạng cấu trúc JSON chuẩn cho các phản hồi báo lỗi từ API Backend.
     * Định dạng bao gồm:
     * - "timestamp": Thời gian lỗi xảy ra
     * - "status": Mã trạng thái HTTP (HTTP Status code)
     * - "message": Thông điệp báo lỗi chi tiết
     * 
     * @param status Mã trạng thái HTTP muốn trả về
     * @param message Nội dung báo lỗi tương ứng
     * @return ResponseEntity chứa Map dữ liệu phản hồi lỗi dạng JSON
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
