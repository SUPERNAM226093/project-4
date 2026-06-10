package com.myproject.clinic.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service xử lý tích hợp thanh toán VNPay trong hệ thống MedPro.
 *
 * Nhiệm vụ chính:
 * - Tạo URL thanh toán VNPay cho đơn tư vấn online.
 * - Tạo chữ ký vnp_SecureHash bằng HMAC-SHA512.
 * - Xác thực callback VNPay trả về.
 */
@Slf4j
@Service
public class VnPayService {

    // Mã website / merchant do VNPay cấp.
    @Value("${vnp.tmn.code}")
    private String tmnCode;

    @Value("${vnp.hash.secret}")
    private String hashSecret;

    @Value("${vnp.pay.url}")
    private String payUrl;

    @Value("${vnp.return.url}")
    private String returnUrl;

    private String getTmnCode() {
        return tmnCode != null ? tmnCode.trim() : "";
    }

    // Lấy khóa bí mật VNPay và trim.
    // Khóa này được dùng khi tạo vnp_SecureHash và khi verify callback.
    private String getHashSecret() {
        return hashSecret != null ? hashSecret.trim() : "";
    }

    // Lấy URL thanh toán VNPay.
    private String getPayUrl() {
        return payUrl != null ? payUrl.trim() : "";
    }

    // Lấy URL callback mà VNPay sẽ redirect về.
    private String getReturnUrl() {
        return returnUrl != null ? returnUrl.trim() : "";
    }

    /**
     * Tạo URL thanh toán VNPay.
     *
     * @param amount
     * @param txnRef
     * @param ipAddr địa chỉ IP của người dùng
     * @return URL
     */
    /**
     * Tạo URL thanh toán VNPay cho một đơn tư vấn.
     * Hàm dựng bộ tham số vnp_*, sắp xếp theo alphabet, ký bằng HMAC-SHA512 rồi gắn vnp_SecureHash vào URL.
     */
    public String createPaymentUrl(long amount, String txnRef, String ipAddr) {

        // Phiên bản API VNPay.
        String vnp_Version = "2.1.0";

        // Lệnh "pay" nghĩa là tạo giao dịch thanh toán.
        String vnp_Command = "pay";

        // Nội dung thanh toán hiển thị trên VNPay.
        String vnp_OrderInfo = "Thanh toan don hang:" + txnRef;

        // Loại đơn hàng, để "other" theo demo VNPay.
        String vnp_OrderType = "other";

        // Mã tham chiếu giao dịch.
        // Ở MedPro, giá trị này là consultationId.
        String vnp_TxnRef = txnRef;

        // Tạo Map chứa toàn bộ tham số gửi sang VNPay.
        Map<String, String> vnp_Params = new HashMap<>();

        // Các tham số bắt buộc theo chuẩn VNPay.
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", getTmnCode());

        // VNPay yêu cầu số tiền gửi sang phải nhân 100.
        // Ví dụ 200000 VND sẽ gửi là 20000000.
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));

        // Đơn vị tiền tệ.
        vnp_Params.put("vnp_CurrCode", "VND");

        // Mã đơn hàng / mã giao dịch.
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // Nội dung thanh toán.
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);

        // Loại đơn hàng.
        vnp_Params.put("vnp_OrderType", vnp_OrderType);

        // Ngôn ngữ hiển thị trên cổng VNPay.
        vnp_Params.put("vnp_Locale", "vn");

        // URL callback để VNPay redirect về sau thanh toán.
        vnp_Params.put("vnp_ReturnUrl", getReturnUrl());

        // IP của người thanh toán.
        vnp_Params.put("vnp_IpAddr", ipAddr);

        // Lấy thời gian hiện tại theo múi giờ GMT+7.
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        // Format thời gian theo chuẩn VNPay: yyyyMMddHHmmss.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        // Thời điểm tạo giao dịch.
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Thời điểm hết hạn giao dịch trên VNPay.
        // Ở đây URL thanh toán hết hạn sau 15 phút.
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // VNPay yêu cầu các tham số phải được sắp xếp theo alphabet
        // trước khi tạo chuỗi hash.
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        // hashData là chuỗi dùng để tạo chữ ký bảo mật.
        StringBuilder hashData = new StringBuilder();

        // query là chuỗi tham số gắn vào URL thanh toán.
        StringBuilder query = new StringBuilder();

        // Duyệt các tham số đã sắp xếp để tạo hashData và query.
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);

            // Chỉ xử lý tham số có giá trị.
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    // Encode tên và giá trị tham số theo US_ASCII giống demo VNPay.
                    String encodedName = URLEncoder.encode(
                            fieldName,
                            StandardCharsets.US_ASCII.toString());

                    String encodedValue = URLEncoder.encode(
                            fieldValue,
                            StandardCharsets.US_ASCII.toString());

                    // Ghép vào chuỗi hashData dạng:
                    // key1=value1&key2=value2&...
                    if (hashData.length() > 0) {
                        hashData.append('&');
                    }
                    hashData.append(encodedName).append('=').append(encodedValue);

                    // Ghép vào chuỗi query để tạo URL thanh toán.
                    if (query.length() > 0) {
                        query.append('&');
                    }
                    query.append(encodedName).append('=').append(encodedValue);

                } catch (Exception e) {
                    // Ghi log nếu lỗi encode tham số.
                    log.error("Error encoding parameter: {}", e.getMessage());
                }
            }
        }

        // Lấy chuỗi query sau khi build xong.
        String queryUrl = query.toString();

        // Log chuỗi hashData để debug khi chữ ký không khớp.
        log.info("[VNPay] Raw Hash Data string (encoded): {}", hashData);

        // Tạo chữ ký bảo mật vnp_SecureHash bằng HMAC-SHA512.
        // getHashSecret() chính là vnp_HashSecret do VNPay cấp.
        String vnp_SecureHash = hmacSHA512(getHashSecret(), hashData.toString());

        // Gắn chữ ký vào cuối query.
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        // Ghép URL thanh toán hoàn chỉnh.
        String finalUrl = getPayUrl() + "?" + queryUrl;

        // Log URL để kiểm tra trong môi trường test.
        log.info("[VNPay] Generated Payment URL: {}", finalUrl);

        // Trả URL này cho frontend.
        // Frontend sẽ dùng window.location.href để chuyển bệnh nhân sang VNPay.
        return finalUrl;
    }

    /**
     * Xác thực callback VNPay trả về.
     *
     * @param fields toàn bộ tham số callback VNPay gửi về
     * @return true nếu chữ ký hợp lệ, false nếu callback không hợp lệ
     */
    /**
     * Xác thực callback VNPay bằng cách tự tính lại vnp_SecureHash từ các tham số trả về.
     * Chỉ khi chữ ký tự tính trùng chữ ký VNPay gửi về thì dữ liệu callback mới được xem là hợp lệ.
     */
    public boolean verifyCallback(Map<String, String> fields) {

        // Lấy chữ ký VNPay gửi về trong callback.
        String vnp_SecureHash = fields.get("vnp_SecureHash");

        // Nếu không có chữ ký thì callback không hợp lệ.
        if (vnp_SecureHash == null) {
            return false;
        }

        // Tạo map mới để chứa các tham số dùng cho việc tính lại hash.
        Map<String, String> params = new HashMap<>();

        // Duyệt toàn bộ tham số callback.
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (val != null
                    && val.length() > 0
                    && !key.equals("vnp_SecureHash")
                    && !key.equals("vnp_SecureHashType")) {
                params.put(key, val);
            }
        }

        // Sắp xếp tên tham số theo alphabet giống lúc tạo payment URL.
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        // Build lại chuỗi hashData từ callback.
        StringBuilder hashData = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);

            try {
                // Encode tên và giá trị tham số theo cùng quy tắc lúc tạo URL.
                String encodedName = URLEncoder.encode(
                        fieldName,
                        StandardCharsets.US_ASCII.toString());

                String encodedValue = URLEncoder.encode(
                        fieldValue,
                        StandardCharsets.US_ASCII.toString());

                // Ghép chuỗi hashData dạng key=value&key=value.
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(encodedName).append('=').append(encodedValue);

            } catch (Exception e) {
                // Ghi log nếu lỗi encode callback parameter.
                log.error("Error encoding callback parameter: {}", e.getMessage());
            }
        }

        // Log chuỗi hashData callback để debug.
        log.info("[VNPay Callback] Raw Hash Data string (encoded): {}", hashData);

        // Tính lại chữ ký bằng hashSecret của hệ thống.
        String calculatedHash = hmacSHA512(getHashSecret(), hashData.toString());

        // So sánh chữ ký tự tính với chữ ký VNPay gửi về.
        // equalsIgnoreCase dùng để tránh khác biệt chữ hoa/chữ thường trong chuỗi hex.
        boolean isValid = calculatedHash.equalsIgnoreCase(vnp_SecureHash);

        // Log trạng thái xác thực callback.
        log.info(
                "[VNPay Callback] Verification status: {}. Received hash: {}, Calculated hash: {}",
                isValid,
                vnp_SecureHash,
                calculatedHash);

        // Nếu true thì callback hợp lệ, service xử lý thanh toán mới được cập nhật
        // PAID.
        return isValid;
    }

    /**
     * Hàm tạo chữ ký HMAC-SHA512.
     *
     * @param key  khóa bí mật VNPay, tức vnp_HashSecret
     * @param data chuỗi dữ liệu cần ký
     * @return chuỗi hash dạng hexadecimal
     */
    /**
     * Tạo chữ ký HMAC-SHA512 từ hashSecret và chuỗi dữ liệu giao dịch.
     * Kết quả byte được đổi sang chuỗi hex để dùng làm vnp_SecureHash.
     */
    private String hmacSHA512(final String key, final String data) {
        try {
            // Nếu key hoặc data null thì không thể tạo chữ ký.
            if (key == null || data == null) {
                throw new NullPointerException();
            }

            // Khởi tạo Mac với thuật toán HmacSHA512.
            final Mac hmac512 = Mac.getInstance("HmacSHA512");

            // Chuyển khóa bí mật sang byte.
            // Dòng này giữ giống Config.java trong demo VNPay.
            byte[] hmacKeyBytes = key.getBytes();

            // Tạo SecretKeySpec từ khóa bí mật.
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");

            // Khởi tạo Mac với khóa bí mật.
            hmac512.init(secretKey);

            // Chuyển dữ liệu cần ký sang byte UTF-8.
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

            // Ký dữ liệu bằng HMAC-SHA512.
            byte[] result = hmac512.doFinal(dataBytes);

            // Chuyển mảng byte kết quả sang chuỗi hex.
            // Chuỗi này chính là vnp_SecureHash.
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }

            // Trả về chữ ký hash.
            return sb.toString();

        } catch (Exception ex) {
            // Nếu lỗi khi tính HMAC thì ghi log và trả chuỗi rỗng.
            log.error("Error calculating HMAC SHA512", ex);
            return "";
        }
    }
}
