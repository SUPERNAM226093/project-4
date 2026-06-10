/*
 * Đây là file Servlet mẫu do VNPay cung cấp.
 * Mục đích: gửi request sang VNPay để truy vấn lại kết quả giao dịch.
 * Luồng này thường dùng khi hệ thống muốn kiểm tra trạng thái giao dịch
 * bằng mã đơn hàng và ngày giao dịch.
 */
package com.vnpay.common;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet truy vấn kết quả giao dịch VNPay.
 *
 * Khác với ajaxServlet dùng để tạo URL thanh toán,
 * file này dùng để hỏi lại VNPay xem một giao dịch đã thành công,
 * thất bại hay đang ở trạng thái nào.
 *
 * @author CTT VNPAY
 */
public class vnpayQuery extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Command: querydr
        // Đây là lệnh truy vấn kết quả giao dịch của VNPay.

        // Mã request ngẫu nhiên, dùng để định danh lần truy vấn này.
        String vnp_RequestId = Config.getRandomNumber(8);

        // Phiên bản API VNPay.
        String vnp_Version = "2.1.0";

        // Lệnh querydr nghĩa là truy vấn kết quả giao dịch.
        String vnp_Command = "querydr";

        // Mã merchant / website do VNPay cấp.
        String vnp_TmnCode = Config.vnp_TmnCode;

        // Mã đơn hàng cần truy vấn.
        // Frontend hoặc form gửi lên tham số order_id.
        String vnp_TxnRef = req.getParameter("order_id");

        // Nội dung truy vấn giao dịch.
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;

        // Mã giao dịch của VNPay nếu có.
        // Trong demo dòng này bị comment, tức là không dùng.
        // String vnp_TransactionNo = req.getParameter("transactionNo");

        // Ngày giao dịch cần truy vấn.
        // VNPay cần biết giao dịch phát sinh ngày nào để tra cứu.
        String vnp_TransDate = req.getParameter("trans_date");

        // Lấy thời gian hiện tại theo múi giờ GMT+7.
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        // Format thời gian theo chuẩn VNPay: yyyyMMddHHmmss.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        // Thời điểm tạo request truy vấn.
        String vnp_CreateDate = formatter.format(cld.getTime());

        // Lấy địa chỉ IP của người gửi request.
        String vnp_IpAddr = Config.getIpAddress(req);

        // Tạo object JSON chứa toàn bộ tham số gửi sang VNPay API.
        JsonObject vnp_Params = new JsonObject();

        // Thêm các tham số bắt buộc vào JSON.
        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

        // Nếu cần truy vấn theo mã giao dịch VNPay thì có thể thêm tham số này.
        // vnp_Params.put("vnp_TransactionNo", vnp_TransactionNo);

        // Ngày giao dịch gốc.
        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransDate);

        // Thời điểm tạo request truy vấn.
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);

        // IP của người gửi request.
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        // Chuỗi dữ liệu dùng để tạo chữ ký bảo mật.
        // Với API querydr, VNPay yêu cầu nối các trường bằng dấu "|".
        String hash_Data = String.join(
                "|",
                vnp_RequestId,
                vnp_Version,
                vnp_Command,
                vnp_TmnCode,
                vnp_TxnRef,
                vnp_TransDate,
                vnp_CreateDate,
                vnp_IpAddr,
                vnp_OrderInfo);

        // Tạo chữ ký HMAC-SHA512 cho request truy vấn.
        // Config.secretKey chính là vnp_HashSecret do VNPay cấp.
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hash_Data.toString());

        // Gắn chữ ký vào JSON request.
        // VNPay dùng chữ ký này để kiểm tra request có hợp lệ không.
        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        // URL API truy vấn giao dịch của VNPay.
        URL url = new URL(Config.vnp_ApiUrl);

        // Mở kết nối HTTP tới VNPay.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Gửi request dạng POST.
        con.setRequestMethod("POST");

        // Dữ liệu gửi đi là JSON.
        con.setRequestProperty("Content-Type", "application/json");

        // Cho phép ghi dữ liệu vào body của request.
        con.setDoOutput(true);

        // Ghi JSON request vào body.
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();

        // Lấy HTTP response code từ VNPay.
        int responseCode = con.getResponseCode();

        // In log để debug request gửi sang VNPay.
        System.out.println("nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);

        // Đọc response body VNPay trả về.
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String output;
        StringBuffer response = new StringBuffer();

        // Đọc từng dòng response và nối lại.
        while ((output = in.readLine()) != null) {
            response.append(output);
        }

        in.close();

        // In kết quả truy vấn giao dịch.
        // Response này cho biết giao dịch có tồn tại không,
        // trạng thái thanh toán ra sao, mã lỗi là gì...
        System.out.println(response.toString());
    }
}