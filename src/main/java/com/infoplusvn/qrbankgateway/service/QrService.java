package com.infoplusvn.qrbankgateway.service;

import com.infoplusvn.qrbankgateway.dto.request.GenerateQRRequest;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class QrService {

    public String generateQrIBFTStatic(GenerateQRRequest qrRequest) throws UnsupportedEncodingException {

        //phien ban du lieu & phuong thuc khoi tao
        String qr ="000201010211";

        //Định danh duy nhất toàn cầu - GUID (ID 00):
        //Có định dạng chữ số với độ dài tối đa 32 ký tự, cụ thể là AID của NAPAS: A000000727.
        String guid = "0010A000000727";

        //Tổ chức thanh toán(NHTV,TGTT) (ID 01):
        //Định danh ACQ ID(ID 00): các ngân hàng tại Việt Nam sử dụng mã BIN cấp bởi NHNN. VD: 970403
        String acqId = "0006970415";
        //Thong tin nguoi huong thu(ID 01)
        String customerId = "01" + String.format("%02d", qrRequest.getData().getQrInfo().getCustomerId().length()) + qrRequest.getData().getQrInfo().getCustomerId();

        //Ma dich vu (ID 02):
        String serviceCode = "02" + String.format("%02d", qrRequest.getData().getQrInfo().getServiceCode().length()) + qrRequest.getData().getQrInfo().getServiceCode();

        //Ma tien te(ID 53): 704 là VND
        String transCurrency = "5303" + qrRequest.getData().getQrInfo().getTransCurrency();

        //Ma quoc gia(ID 58)
        String countryCode = "5802" + qrRequest.getData().getQrInfo().getCountryCode();

        //Thong tin to chuc thanh toan
        String TGTT = "01" + String.format("%02d", (acqId.length() + customerId.length())) + acqId + customerId;

        //Thông tin định danh người thụ hưởng(Consumer Account Information)(ID 38)
        String cusAccInfo = "38" + String.format("%02d", (guid.length() + TGTT.length() + serviceCode.length())) + guid + TGTT + serviceCode;

        qr += cusAccInfo + transCurrency + countryCode + "6304";

        String crc = Integer.toHexString(crc16(qr.getBytes("ASCII"))).toUpperCase();

        return qr + crc;
    }

    public int crc16(byte[] value)  {
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)

        byte[] testBytes = value;

        //byte[] bytes = args[0].getBytes();

        for (byte b : testBytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        crc &= 0xffff;
        return crc;
    }
}
