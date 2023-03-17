package com.infoplusvn.qrbankgateway.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.infoplusvn.qrbankgateway.dto.request.GenerateQRRequest;
import com.infoplusvn.qrbankgateway.dto.response.GenerateQRResponse;
import com.infoplusvn.qrbankgateway.exception.ValidationHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class QrService {

    public String genQRString(GenerateQRRequest qrRequest) {

            //phien ban du lieu & phuong thuc khoi tao
            String qr = "000201010211";

            //Định danh duy nhất toàn cầu - GUID (ID 00):
            //Có định dạng chữ số với độ dài tối đa 32 ký tự, cụ thể là AID của NAPAS: A000000727.
            String guid = "0010A000000727";

            //Tổ chức thanh toán(NHTV,TGTT) (ID 01):
            //Định danh ACQ ID(ID 00): các ngân hàng tại Việt Nam sử dụng mã BIN cấp bởi NHNN. VD: 970403
            String acqId = "0006" + qrRequest.getHeader().getBkCd();

            //Thong tin nguoi huong thu(ID 01)
            String customerId = "01" + String.format("%02d", qrRequest.getData().getQrInfo().getCustomerId().length()) + qrRequest.getData().getQrInfo().getCustomerId();

            //Ma dich vu (ID 02):
            String serviceCode = "02" + String.format("%02d", qrRequest.getData().getQrInfo().getServiceCode().length()) + qrRequest.getData().getQrInfo().getServiceCode().toUpperCase();

            //Ma tien te(ID 53): 704 là VND
            String transCurrency = "5303" + qrRequest.getData().getQrInfo().getTransCurrency();

            //Ma quoc gia(ID 58)
            String countryCode = "5802" + qrRequest.getData().getQrInfo().getCountryCode();

            //Thong tin to chuc thanh toan
            String TGTT = "01" + String.format("%02d", (acqId.length() + customerId.length())) + acqId + customerId;

            //Thông tin định danh người thụ hưởng(Consumer Account Information)(ID 38)
            String cusAccInfo = "38" + String.format("%02d", (guid.length() + TGTT.length() + serviceCode.length())) + guid + TGTT + serviceCode;

            qr += cusAccInfo + transCurrency + countryCode + "6304";


            return qr;

    }

    public String genCRC(String qrString) throws UnsupportedEncodingException {
        String crc = Integer.toHexString(crc16(qrString.getBytes("ASCII"))).toUpperCase();

        return crc;
    }

    public int crc16(byte[] value) {
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)

        byte[] testBytes = value;

        //byte[] bytes = args[0].getBytes();

        for (byte b : testBytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        crc &= 0xffff;
        return crc;
    }

    public GenerateQRRequest.QrInfo ParseQRString(String qr) {

        GenerateQRRequest.QrInfo qrInfo = new GenerateQRRequest.QrInfo();

        //CustomerID
        int lengthCustomerId = Integer.parseInt(qr.substring(qr.indexOf("00069704") + 12, qr.indexOf("00069704") + 14));

        String customerId = qr.substring(qr.indexOf("00069704") + 14, qr.indexOf("00069704") + 14 + lengthCustomerId);

        //ServiceCode
        int lengthServiceCode = Integer.parseInt(qr.substring(qr.indexOf(customerId) + lengthCustomerId + 2, qr.indexOf(customerId) + lengthCustomerId + 4));

        String serviceCode = qr.substring(qr.indexOf(customerId) + lengthCustomerId + 4, qr.indexOf(customerId) + lengthCustomerId + 4 + lengthServiceCode);

        //Transaction Currency
        String transCurrency = qr.substring(qr.indexOf("5303", qr.indexOf(serviceCode)) + 4, qr.indexOf("5303", qr.indexOf(serviceCode)) + 7);

        //CountryCode
        String countryCode = qr.substring(qr.indexOf("5802", qr.indexOf(serviceCode)) + 4, qr.indexOf("5802", qr.indexOf(serviceCode)) + 6);

        //set qrInfo
        qrInfo.setCountryCode(countryCode);

        qrInfo.setServiceCode(serviceCode);

        qrInfo.setCustomerId(customerId);

        qrInfo.setTransCurrency(transCurrency);

        return qrInfo;
    }

    public String genBase64FromQRImage(String qr) {

        StringBuilder result = new StringBuilder();

        if (!qr.isEmpty()) {

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            try {

                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix = writer.encode(qr, BarcodeFormat.QR_CODE, 300, 300);

                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                ImageIO.write(bufferedImage, "png", os);

                result.append("data:image/png;base64,");
                result.append(new String(Base64.getEncoder().encode(os.toByteArray())));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.toString();


    }

    public GenerateQRResponse genResponseQrIBFTStatic(GenerateQRRequest qrRequest) throws UnsupportedEncodingException {

        ArrayList<String> listServiceCode = new ArrayList<>();
        listServiceCode.add("QRPUSH");
        listServiceCode.add("QRCASH");
        listServiceCode.add("QRIBFTTC");
        listServiceCode.add("QRIBFTTA");
        listServiceCode.add("QRADVERTISE");

        GenerateQRResponse generateQRResponse = new GenerateQRResponse();
        GenerateQRResponse.Data data = new GenerateQRResponse.Data();
        generateQRResponse.setData(data);

        if (!ValidationHelper.isValid(qrRequest)) {
            //System.out.println(ValidationHelper.fieldNames.get());
            generateQRResponse.getData().setResponseCode("004");
            generateQRResponse.getData().setResponseDesc("Wrong message format: " + ValidationHelper.fieldNames.get());
        }
        else {

            if (!listServiceCode.contains(qrRequest.getData().getQrInfo().getServiceCode().toUpperCase())) {
                generateQRResponse.getData().setResponseCode("005");
                generateQRResponse.getData().setResponseDesc("Invalid service code");
            }
            else {
                generateQRResponse.setHeader(qrRequest.getHeader());
                generateQRResponse.getHeader().setReqResGb("RES");

                generateQRResponse.getData().setResponseCode("000");
                generateQRResponse.getData().setResponseDesc("Success");
                generateQRResponse.getData().setQrImage(genBase64FromQRImage(genQRString(qrRequest) + genCRC(genQRString(qrRequest))));
            }

        }

        return generateQRResponse;
    }


}
