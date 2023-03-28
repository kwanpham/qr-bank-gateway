package com.infoplusvn.qrbankgateway.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.infoplusvn.qrbankgateway.dto.request.DeCodeQRRequest;
import com.infoplusvn.qrbankgateway.dto.request.GenerateQRRequest;
import com.infoplusvn.qrbankgateway.dto.response.DeCodeQRResponse;
import com.infoplusvn.qrbankgateway.dto.response.GenerateQRResponse;
import com.infoplusvn.qrbankgateway.exception.ValidationHelper;
import com.infoplusvn.qrbankgateway.constant.ErrorDefination;
import com.infoplusvn.qrbankgateway.constant.QRCodeFormat;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;

@Service
public class QrService {

    final String QRIBFT_STATIC = "11";
    final String QRIBFT_DYNAMIC = "12";

    final int SIZE_IMAGE_QR = 300;
    final List<String> listServiceCode = Arrays.asList("QRPUSH", "QRCASH", "QRIBFTTC", "QRIBFTTA", "QRADVERTISE");


    /*public String genQRString(GenerateQRRequest qrRequest) {

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

    }*/
    public String genQRString(GenerateQRRequest qrRequest) {

        //QRString
        LinkedHashMap<String, String> linkedHashMapQRString = new LinkedHashMap<>();

        //phien ban du lieu (ID 00)
        linkedHashMapQRString.put(QRCodeFormat.PAYLOAD_FORMAT_INDICATOR.getId(), QRCodeFormat.PAYLOAD_FORMAT_INDICATOR.getValue());

        //phuong thuc khoi tao (ID 01)
        linkedHashMapQRString.put(QRCodeFormat.POINT_OF_INITIATION_METHOD.getId(), QRIBFT_STATIC);

        //Thong tin dinh danh nguoi thu huong (ID 38)
        LinkedHashMap<String, String> linkedHashMapAccountInfo = new LinkedHashMap<>();
        //Dinh danh toan cau
        linkedHashMapAccountInfo.put(QRCodeFormat.GUID.getId(), QRCodeFormat.GUID.getValue());

        //To chuc thanh toan (ID 01)
        LinkedHashMap<String, String> linkedHashMapMemberBanks = new LinkedHashMap<>();
        //Don vi thu huong (ID 00)
        linkedHashMapMemberBanks.put(QRCodeFormat.BNB_ID.getId(), qrRequest.getHeader().getBkCd().trim());
        //Thong tin nguoi huong thu (ID 01)
        linkedHashMapMemberBanks.put(QRCodeFormat.CONSUMER_ID.getId(), qrRequest.getData().getQrInfo().getCustomerId().trim());

        //Lay value cua to chuc thanh toan va put vao thong tin dinh danh nguoi thu huong (ID 38)
        linkedHashMapAccountInfo.put(QRCodeFormat.MEMBER_BANKS.getId(), showKeyLengthValue(linkedHashMapMemberBanks));
        //Loai dich vu
        linkedHashMapAccountInfo.put(QRCodeFormat.SERVICE_CODE.getId(), qrRequest.getData().getQrInfo().getServiceCode().toUpperCase().trim());

        //lay value cua thong tin dinh danh nguoi thu huong va put vao QRString
        linkedHashMapQRString.put(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId(), showKeyLengthValue(linkedHashMapAccountInfo));



        //Ma tien te
        linkedHashMapQRString.put(QRCodeFormat.TRANSACTION_CURRENCY.getId(), qrRequest.getData().getQrInfo().getTransCurrency().trim());

        //So tien giao dich
        if (!qrRequest.getData().getQrInfo().getTransAmount().trim().isEmpty()) {
            linkedHashMapQRString.put(QRCodeFormat.POINT_OF_INITIATION_METHOD.getId(), QRIBFT_DYNAMIC);
            linkedHashMapQRString.put(QRCodeFormat.TRANSACTION_AMOUNT.getId(), qrRequest.getData().getQrInfo().getTransAmount().trim());
        }

        //Ma quoc gia
        linkedHashMapQRString.put(QRCodeFormat.COUNTRY_CODE.getId(), qrRequest.getData().getQrInfo().getCountryCode().trim());

        //thong tin bo sung
        if (!qrRequest.getData().getQrInfo().getAdditionInfo().trim().isEmpty()){
            linkedHashMapQRString.put(QRCodeFormat.POINT_OF_INITIATION_METHOD.getId(), QRIBFT_DYNAMIC);
            LinkedHashMap<String,String> linkedHashMapAdditionInfo = new LinkedHashMap<>();
            linkedHashMapAdditionInfo.put(QRCodeFormat.PURPOSE_TRANSACTION.getId(), qrRequest.getData().getQrInfo().getAdditionInfo().trim());
            linkedHashMapQRString.put(QRCodeFormat.ADDITION_INFO.getId(), showKeyLengthValue(linkedHashMapAdditionInfo));
        }



        return showKeyLengthValue(linkedHashMapQRString) + QRCodeFormat.CRC.getId() + "04";

    }

    private String showKeyLengthValue(LinkedHashMap<String, String> linkedHashMap) {
        String result = "";
        Set<String> keySet = linkedHashMap.keySet();
        for (String key : keySet) {
            result += key + String.format("%02d", linkedHashMap.get(key).length()) + linkedHashMap.get(key);
        }
        return result;
    }

    public String genCRC(String qrString) throws UnsupportedEncodingException {

        return Integer.toHexString(crc16(qrString.getBytes("ASCII"))).toUpperCase();

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

    /*public GenerateQRRequest.QrInfo ParseQRString(String qr) {

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
    }*/

    /*public String genBase64FromQRImage(String qr) {

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


    }*/

    public String genBase64FromQRImage(String qr) throws WriterException, IOException {
        int size = SIZE_IMAGE_QR;
        String logoPath = "src/main/resources/image/logo1.jpg";
        int logoSize = size / 6;
        String format = "png";

        // create QR code with whitespace
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix qrMatrix = qrWriter.encode(qr, BarcodeFormat.QR_CODE, size, size, getQRCodeHints());
        BufferedImage qrImage = toBufferedImage(qrMatrix);

        // load logo image
        BufferedImage logoImage = ImageIO.read(new File(logoPath));

        // resize logo image
        BufferedImage resizedLogoImage = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedLogoImage.createGraphics();
        g2d.drawImage(logoImage, 0, 0, logoSize, logoSize, null);
        g2d.dispose();

        // add logo to QR code
        int logoX = (size - logoSize) / 2;
        int logoY = (size - logoSize) / 2;
        Graphics2D qrGraphics = qrImage.createGraphics();
        qrGraphics.drawImage(resizedLogoImage, logoX, logoY, logoSize, logoSize, null);
        qrGraphics.dispose();

        // encode image to base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, format, baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);        // print base64 image to console
        return base64Image;
    }

    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    private Map<EncodeHintType, Object> getQRCodeHints() {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        return hints;
    }

    public GenerateQRResponse genQRResponse(GenerateQRRequest qrRequest) throws IOException, WriterException {


        GenerateQRResponse generateQRResponse = new GenerateQRResponse();
        GenerateQRResponse.Data data = new GenerateQRResponse.Data();
        //generateQRResponse.setData(data);
        generateQRResponse.setHeader(qrRequest.getHeader());


        //generateQRResponse.setData(data);

        if (!ValidationHelper.isValid(qrRequest)) {
            //System.out.println(ValidationHelper.fieldNames.get());
            generateQRResponse.getHeader().setErrCode(ErrorDefination.ERR_004.getErrCode());
            generateQRResponse.getHeader().setErrDesc(ErrorDefination.ERR_004.getDesc() + ": " + ValidationHelper.fieldNames.get());

        } else if (!listServiceCode.contains(qrRequest.getData().getQrInfo().getServiceCode().toUpperCase().trim())) {
            generateQRResponse.getHeader().setErrCode(ErrorDefination.ERR_005.getErrCode());
            generateQRResponse.getHeader().setErrDesc(ErrorDefination.ERR_005.getDesc());
        } else {

            String qrString = genQRString(qrRequest) + genCRC(genQRString(qrRequest));

            generateQRResponse.setData(data);
            generateQRResponse.getHeader().setReqResGb("RES");

            generateQRResponse.getData().setResponseCode(ErrorDefination.ERR_OOO.getErrCode());
            generateQRResponse.getData().setResponseDesc(ErrorDefination.ERR_OOO.getDesc());
            generateQRResponse.getData().setQrImage(genBase64FromQRImage(qrString));
            generateQRResponse.getData().setQrString(qrString);
        }
        return generateQRResponse;
    }

    public DeCodeQRResponse parseQRString(DeCodeQRRequest deCodeQRRequest) throws UnsupportedEncodingException {

        DeCodeQRResponse deCodeQRResponse = new DeCodeQRResponse();
        DeCodeQRResponse.Data data = new DeCodeQRResponse.Data();
        DeCodeQRResponse.QrInfo qrInfo = new DeCodeQRResponse.QrInfo();


        deCodeQRResponse.setHeader(deCodeQRRequest.getHeader());

        if (!ValidationHelper.isValid(deCodeQRRequest)) {

            deCodeQRResponse.getHeader().setErrCode(ErrorDefination.ERR_004.getErrCode());
            deCodeQRResponse.getHeader().setErrDesc(ErrorDefination.ERR_004.getDesc() + ": " + ValidationHelper.fieldNames.get());

        } else if (!checkCRC(deCodeQRRequest.getData().getQrString().trim())) {
            deCodeQRResponse.getHeader().setErrCode(ErrorDefination.ERR_008.getErrCode());
            deCodeQRResponse.getHeader().setErrDesc(ErrorDefination.ERR_008.getDesc());
        } else {
            data.setQrInfo(qrInfo);
            deCodeQRResponse.setData(data);

            String qrString = deCodeQRRequest.getData().getQrString().trim();

            LinkedHashMap<String, String> linkedHashMapQRString = new LinkedHashMap<>();

            putHashMapAndCutQrString("", linkedHashMapQRString, qrString);

            String valueOfID38 = linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId());
            putHashMapAndCutQrString(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + ".", linkedHashMapQRString, valueOfID38);

            String valueOfID38_01 = linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.MEMBER_BANKS.getId());
            putHashMapAndCutQrString(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.MEMBER_BANKS.getId() + ".", linkedHashMapQRString, valueOfID38_01);

            deCodeQRResponse.getData().setResponseCode(ErrorDefination.ERR_OOO.getErrCode());
            deCodeQRResponse.getData().setResponseDesc(ErrorDefination.ERR_OOO.getDesc());
            deCodeQRResponse.getData().getQrInfo().setServiceCode(linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.SERVICE_CODE.getId()));
            deCodeQRResponse.getData().getQrInfo().setCustomerId(linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.MEMBER_BANKS.getId() + "." + QRCodeFormat.CONSUMER_ID.getId()));
            deCodeQRResponse.getData().getQrInfo().setTransCurrency(linkedHashMapQRString.get(QRCodeFormat.TRANSACTION_CURRENCY.getId()));
            deCodeQRResponse.getData().getQrInfo().setCountryCode(linkedHashMapQRString.get(QRCodeFormat.COUNTRY_CODE.getId()));
            deCodeQRResponse.getData().getQrInfo().setCrc(linkedHashMapQRString.get(QRCodeFormat.CRC.getId()));

            deCodeQRResponse.getHeader().setBkCd(linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.MEMBER_BANKS.getId() + "." + QRCodeFormat.BNB_ID.getId()));
            deCodeQRResponse.getHeader().setReqResGb("RES");

        }

        return deCodeQRResponse;
    }


    private void putHashMapAndCutQrString(String string, LinkedHashMap<String, String> linkedHashMap, String qrString) {
        while (!qrString.isEmpty()) {
            linkedHashMap.put(string + qrString.substring(0, 2), qrString.substring(4, 4 + Integer.parseInt(qrString.substring(2, 4))));
            qrString = qrString.replace(qrString.substring(0, 4) + qrString.substring(4, 4 + Integer.parseInt(qrString.substring(2, 4))), "");
        }
    }

    private boolean checkCRC(String qrString) throws UnsupportedEncodingException {
        qrString = qrString.trim();
        if (genCRC(qrString.substring(0, qrString.length() - 4)).equals(qrString.substring(qrString.length() - 4))) {
            return true;
        }
        return false;
    }
}
