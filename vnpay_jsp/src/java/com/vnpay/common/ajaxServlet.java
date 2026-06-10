/*
 * Đây là file Servlet mẫu do VNPay cung cấp.
 * Mục đích: nhận request thanh toán từ frontend, tạo URL thanh toán VNPay,
 * sau đó trả URL này về cho frontend dưới dạng JSON.
 */
package com.vnpay.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet xử lý tạo URL thanh toán VNPay.
 *
 * Trong demo VNPay, frontend gọi AJAX tới servlet này.
 * Servlet nhận số tiền, ngân hàng, ngôn ngữ... rồi tạo link thanh toán.
 *
 * @author CTT VNPAY
 */
public class ajaxServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Phiên bản API VNPay đang sử dụng.
        String vnp_Version = "2.1.0";

        // Lệnh "pay" nghĩa là tạo giao dịch thanh toán.
        String vnp_Command = "pay";

        // Loại đơn hàng. Ở demo để mặc định là "other".
        String orderType = "other";

        // Lấy số tiền từ request frontend gửi lên.
        // VNPay yêu cầu amount nhân 100.
        // Ví dụ người dùng thanh toán 200000 VND thì gửi sang VNPay là 20000000.
        long amount = Integer.parseInt(req.getParameter("amount")) * 100;

        // Mã ngân hàng người dùng chọn, ví dụ NCB.
        // Nếu không chọn thì VNPay sẽ cho người dùng tự chọn trên cổng thanh toán.
        String bankCode = req.getParameter("bankCode");

        // Tạo mã giao dịch ngẫu nhiên, dùng làm mã tham chiếu đơn hàng.
        // Trong hệ thống thật, chỗ này thường dùng orderId hoặc consultationId.
        String vnp_TxnRef = Config.getRandomNumber(8);

        // Lấy địa chỉ IP của người dùng gửi request.
        String vnp_IpAddr = Config.getIpAddress(req);

        // Mã website/merchant do VNPay cấp.
        String vnp_TmnCode = Config.vnp_TmnCode;

        // Tạo Map chứa toàn bộ tham số sẽ gửi sang VNPay.
        Map<String, String> vnp_Params = new HashMap<>();

        // Thêm các tham số bắt buộc của VNPay.
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        // Nếu người dùng chọn ngân hàng cụ thể thì gửi kèm vnp_BankCode.
        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        // Mã tham chiếu giao dịch.
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // Nội dung thanh toán hiển thị trên VNPay.
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);

        // Loại đơn hàng.
        vnp_Params.put("vnp_OrderType", orderType);

        // Lấy ngôn ngữ từ request.
        // Nếu frontend không gửi thì mặc định dùng tiếng Việt.
        String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }

        // URL mà VNPay sẽ redirect về sau khi thanh toán xong.
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);

        // IP của người thanh toán.
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Tạo thời gian giao dịch theo format VNPay yêu cầu: yyyyMMddHHmmss.
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        // Thời điểm tạo giao dịch.
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Thời điểm hết hạn giao dịch.
        // Ở demo này, giao dịch hết hạn sau 15 phút.
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Lấy danh sách tên tham số để sắp xếp alphabet.
        // VNPay yêu cầu các tham số phải được sắp xếp trước khi ký hash.
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);

        // hashData: chuỗi dữ liệu dùng để tạo chữ ký HMAC-SHA512.
        StringBuilder hashData = new StringBuilder();

        // query: chuỗi query thật sự gắn vào URL thanh toán.
        StringBuilder query = new StringBuilder();

        Iterator itr = fieldNames.iterator();

        // Duyệt qua từng tham số đã sắp xếp.
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);

            // Chỉ xử lý các tham số có giá trị.
            if ((fieldValue != null) && (fieldValue.length() > 0)) {

                // Build hash data.
                // Đây là chuỗi dùng để ký bảo mật.
                // Format: key1=value1&key2=value2&...
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(
                        URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                // Build query.
                // Đây là chuỗi tham số sẽ gắn lên URL thanh toán VNPay.
                query.append(
                        URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(
                        URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                // Nếu chưa phải tham số cuối thì thêm dấu &.
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        // Chuyển query builder thành chuỗi.
        String queryUrl = query.toString();

        // Tạo chữ ký bảo mật vnp_SecureHash.
        // secretKey là vnp_HashSecret do VNPay cấp.
        // hashData là chuỗi tham số đã sắp xếp.
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());

        // Gắn chữ ký vào cuối query URL.
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        // Tạo URL thanh toán hoàn chỉnh.
        // Đây là link mà frontend sẽ chuyển người dùng sang VNPay.
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

        // Tạo object JSON để trả về frontend.
        JsonObject job = new JsonObject();

        // code = 00 nghĩa là tạo URL thành công.
        job.addProperty("code", "00");

        // message mô tả kết quả.
        job.addProperty("message", "success");

        // data chứa URL thanh toán VNPay.
        job.addProperty("data", paymentUrl);

        // Dùng Gson để chuyển object JSON thành chuỗi JSON.
        Gson gson = new Gson();

        // Trả JSON về cho frontend.
        // Frontend nhận paymentUrl rồi redirect người dùng sang VNPay.
        resp.getWriter().write(gson.toJson(job));
    }
}