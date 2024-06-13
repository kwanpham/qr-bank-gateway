package com.infoplusvn.qrbankgateway.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.dto.common.qribft.QrCodeDTORoleUser;
import com.infoplusvn.qrbankgateway.dto.request.qribft.ChangeQRNameRequest;
import com.infoplusvn.qrbankgateway.dto.request.qribft.DeCodeQRRequest;
import com.infoplusvn.qrbankgateway.dto.request.qribft.GenerateAdQR;
import com.infoplusvn.qrbankgateway.dto.request.qribft.GenerateQRRequest;
import com.infoplusvn.qrbankgateway.dto.response.qribft.DeCodeQRResponse;
import com.infoplusvn.qrbankgateway.dto.response.qribft.GenerateQRResponse;
import com.infoplusvn.qrbankgateway.entity.BankEntity;
import com.infoplusvn.qrbankgateway.entity.QRCodeEntity;
import com.infoplusvn.qrbankgateway.exception.ValidationHelper;
import com.infoplusvn.qrbankgateway.constant.ErrorDefination;
import com.infoplusvn.qrbankgateway.constant.QRCodeFormat;
import com.infoplusvn.qrbankgateway.repo.BankRepo;
import com.infoplusvn.qrbankgateway.repo.QrCodeRepo;
import com.infoplusvn.qrbankgateway.service.QrIBFTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Slf4j
@Service
public class QrIBTFServiceImpl implements QrIBFTService {


    @Autowired
    BankRepo bankRepo;

    @Autowired
    QrCodeRepo qrCodeRepo;

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

    private int crc16(byte[] value) {
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

    //resize bang cach giam kich thuoc hinh anh di bao nhieu lan
    private BufferedImage resizeImage(BufferedImage image, int resize) {
        BufferedImage resizedImage = new BufferedImage(image.getWidth() / resize, image.getHeight() / resize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image, 0, 0, image.getWidth() / resize, image.getHeight() / resize, null);
        g2d.dispose();
        return resizedImage;
    }

    //resize bang cach gan 1 kich thuoc co dinh
    private BufferedImage resizeImage(BufferedImage image, int sizeWidth, int sizeHeight) {
        BufferedImage resizedImage = new BufferedImage(sizeWidth, sizeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image, 0, 0, sizeWidth, sizeHeight, null);
        g2d.dispose();
        return resizedImage;
    }

    private void addContentToImage(String content, BufferedImage baseImage, int y, Graphics2D baseImageGraphics, FontMetrics fm) {
        int x = (baseImage.getWidth() - fm.stringWidth(content)) / 2;
        baseImageGraphics.drawString(content, x, y);
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

    public void putHashMapAndCutQrString(String string, LinkedHashMap<String, String> linkedHashMap, String qrString) {
        while (!qrString.isEmpty()) {
            linkedHashMap.put(string + qrString.substring(0, 2), qrString.substring(4, 4 + Integer.parseInt(qrString.substring(2, 4))));
            qrString = qrString.replace(qrString.substring(0, 4) + qrString.substring(4, 4 + Integer.parseInt(qrString.substring(2, 4))), "");
        }
    }

    public boolean checkCRC(String qrString) throws UnsupportedEncodingException {
        qrString = qrString.trim();
        if (genCRC(qrString.substring(0, qrString.length() - 4)).equals(qrString.substring(qrString.length() - 4))) {
            return true;
        }
        return false;
    }

    private String genBase64FromImage(BufferedImage baseImage) throws IOException {
        String format = "png";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(baseImage, format, baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();


        String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);        // print base64 image to console
        return base64Image;
    }

    private String replaceComma(String str) {
        return str.replaceAll(",", "");
    }


    private String genQRString(GenerateQRRequest qrRequest) {

        //QRString
        LinkedHashMap<String, String> linkedHashMapQRString = new LinkedHashMap<>();

        //phien ban du lieu (ID 00)
        linkedHashMapQRString.put(QRCodeFormat.PAYLOAD_FORMAT_INDICATOR.getId(), QRCodeFormat.PAYLOAD_FORMAT_INDICATOR.getValue());

        //phuong thuc khoi tao (ID 01)
        linkedHashMapQRString.put(QRCodeFormat.POINT_OF_INITIATION_METHOD.getId(), CommonConstant.METHOD_STATIC);

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
        if (qrRequest.getData().getQrInfo().getTransCurrency() == null || qrRequest.getData().getQrInfo().getTransCurrency().trim().isEmpty()) {
            linkedHashMapQRString.put(QRCodeFormat.TRANSACTION_CURRENCY.getId(), QRCodeFormat.TRANSACTION_CURRENCY.getValue());
        } else {
            linkedHashMapQRString.put(QRCodeFormat.TRANSACTION_CURRENCY.getId(), qrRequest.getData().getQrInfo().getTransCurrency().trim());
        }


        //So tien giao dich
        if (!replaceComma(qrRequest.getData().getQrInfo().getTransAmount().trim()).isEmpty() && Long.parseLong(replaceComma(qrRequest.getData().getQrInfo().getTransAmount().trim())) >= 1000) {
            linkedHashMapQRString.put(QRCodeFormat.POINT_OF_INITIATION_METHOD.getId(), CommonConstant.METHOD_DYNAMIC);
            linkedHashMapQRString.put(QRCodeFormat.TRANSACTION_AMOUNT.getId(), replaceComma(qrRequest.getData().getQrInfo().getTransAmount().trim()));
        }

        //Ma quoc gia
        if (qrRequest.getData().getQrInfo().getCountryCode() == null || qrRequest.getData().getQrInfo().getCountryCode().trim().isEmpty()) {
            linkedHashMapQRString.put(QRCodeFormat.COUNTRY_CODE.getId(), QRCodeFormat.COUNTRY_CODE.getValue());
        } else {
            linkedHashMapQRString.put(QRCodeFormat.COUNTRY_CODE.getId(), qrRequest.getData().getQrInfo().getCountryCode().trim());
        }

        //thong tin bo sung
        if (!qrRequest.getData().getQrInfo().getAdditionInfo().trim().isEmpty()) {
            //linkedHashMapQRString.put(QRCodeFormat.POINT_OF_INITIATION_METHOD.getId(), CommonConstant.QRIBFT_DYNAMIC);
            LinkedHashMap<String, String> linkedHashMapAdditionInfo = new LinkedHashMap<>();
            linkedHashMapAdditionInfo.put(QRCodeFormat.PURPOSE_TRANSACTION.getId(), qrRequest.getData().getQrInfo().getAdditionInfo().trim());
            linkedHashMapQRString.put(QRCodeFormat.ADDITION_INFO.getId(), showKeyLengthValue(linkedHashMapAdditionInfo));
        }


        return showKeyLengthValue(linkedHashMapQRString) + QRCodeFormat.CRC.getId() + CommonConstant.LENGTH_CRC;

    }

    private BufferedImage genQRImage(String qr) throws WriterException, IOException {
        int size = CommonConstant.SIZE_IMAGE_QR;
        String logoPath = "src/main/resources/image/logoVietQR.png";
        int logoSize = size / 7;

        // create QR code with whitespace
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix qrMatrix = qrWriter.encode(qr, BarcodeFormat.QR_CODE, size, size, getQRCodeHints());
        BufferedImage qrImage = toBufferedImage(qrMatrix);

        // load logo image
        BufferedImage logoImage = ImageIO.read(new File(logoPath));

        // resize logo image
        BufferedImage resizedLogoImage = resizeImage(logoImage, logoSize, logoSize);

        // add logo to QR code
        int logoX = (size - logoSize) / 2;
        int logoY = (size - logoSize) / 2;
        Graphics2D qrGraphics = qrImage.createGraphics();
        qrGraphics.drawImage(resizedLogoImage, logoX, logoY, logoSize, logoSize, null);
        qrGraphics.dispose();

        return qrImage;
    }

    private boolean checkBankCode(String code) {
        BankEntity bank = bankRepo.findByBin(code);
        if (bank != null) {
            return true;
        }
        return false;
    }

    private BufferedImage genQRImageTheme(String qr, GenerateQRRequest qrRequest) throws WriterException, IOException {

        BankEntity bank = bankRepo.findByBin(qrRequest.getHeader().getBkCd());

        BufferedImage baseImage = ImageIO.read(new File(CommonConstant.IMAGE_URL));
        BufferedImage qrImage = genQRImage(qr);

        URL urlLogoBank = new URL(bank.getLogo());
        BufferedImage logoBank = ImageIO.read(urlLogoBank);

        Graphics2D baseImageGraphics = baseImage.createGraphics();
        baseImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        // resize logoBank giam 4 lan
        BufferedImage resizedLogoBank = resizeImage(logoBank, 4);

        //them logo ngan hang vao baseImage
        int logoBankX = (baseImage.getWidth() - resizedLogoBank.getWidth()) / 2 + 110;
        int logoBankY = (baseImage.getHeight() - resizedLogoBank.getHeight()) / 2 + 145;
        baseImageGraphics.drawImage(resizedLogoBank, logoBankX, logoBankY, null);

        //them anh qr vao baseImage
        int baseX = (baseImage.getWidth() - qrImage.getWidth()) / 2;
        int baseY = (baseImage.getHeight() - qrImage.getHeight()) / 2 - 80;
        baseImageGraphics.drawImage(qrImage, baseX, baseY, null);


        // Đặt font chữ, màu sắc và vị trí của chữ
        Font font = new Font(CommonConstant.FONT_STYLE, Font.PLAIN, 18);
        baseImageGraphics.setFont(font);
        baseImageGraphics.setColor(Color.BLACK);


        // Tính toán vị trí x, y để đặt chữ vào giữa ảnh
        FontMetrics fm = baseImageGraphics.getFontMetrics(font);
        // toa do y dùng chung
        int fmY = (baseImage.getHeight() - fm.getHeight()) / 2 + fm.getAscent() + 200;

        // them so tien
        if (!replaceComma(qrRequest.getData().getQrInfo().getTransAmount().trim()).isEmpty()) {
            try {
                long amount = Long.parseLong(replaceComma(qrRequest.getData().getQrInfo().getTransAmount().trim()));
                if (amount > 0) {
                    // Tạo đối tượng DecimalFormat và đặt Locale là Vietnam để sử dụng định dạng tiền tệ của Việt Nam
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
                    formatter.applyPattern("#,##0.##");

                    // Định dạng số thành chuỗi có đơn vị tiền tệ và dấu phẩy ngăn cách phần ngàn
                    String formattedAmount = formatter.format(amount) + " VND";

                    addContentToImage(CommonConstant.TITLE_SO_TIEN + formattedAmount, baseImage, fmY, baseImageGraphics, fm);
                }
            } catch (NumberFormatException e) {
                // Xử lý ngoại lệ khi không thể chuyển đổi chuỗi thành số long
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //them noi dung chuyen khoan
        if (!qrRequest.getData().getQrInfo().getAdditionInfo().trim().isEmpty()) {
            String noiDungCK = CommonConstant.TITLE_NOI_DUNG_CK + qrRequest.getData().getQrInfo().getAdditionInfo().trim();
            addContentToImage(noiDungCK, baseImage, fmY + 30, baseImageGraphics, fm);
        }

        //them ten chu tk vao anh
        String tenChuTK = CommonConstant.TITLE_TEN_CHU_TK + qrRequest.getData().getQrInfo().getCustomerName().trim().toUpperCase();
        addContentToImage(tenChuTK, baseImage, fmY + 60, baseImageGraphics, fm);

        //them stk vao anh
        String soTK = CommonConstant.TITLE_SO_TK + qrRequest.getData().getQrInfo().getCustomerId().trim();
        addContentToImage(soTK, baseImage, fmY + 90, baseImageGraphics, fm);


        //them ten ngan hang vao anh
        String bankName = bank.getBankName();
        addContentToImage(bankName, baseImage, fmY + 120, baseImageGraphics, fm);

        baseImageGraphics.dispose();
        return baseImage;
    }

    private String getQRNameFromQRText(String text) {
        return text.substring(text.indexOf('<') + 1, text.indexOf('>'));
    }


    @Override
    public GenerateQRResponse genQRResponse(GenerateQRRequest qrRequest) throws IOException, WriterException {

        qrRequest.getHeader().setBrCd(CommonConstant.BRAND_CODE);
        qrRequest.getHeader().setTrnDt(CommonConstant.TRANSACTION_DATE);
        qrRequest.getHeader().setDirection(CommonConstant.DIRECTION);
        qrRequest.getHeader().setReqResGb(CommonConstant.REQ_GB);
        qrRequest.getHeader().setRefNo(CommonConstant.REFERENCE_NUMBER);

        GenerateQRResponse generateQRResponse = new GenerateQRResponse();
        GenerateQRResponse.Data data = new GenerateQRResponse.Data();
        //generateQRResponse.setData(data);
        generateQRResponse.setHeader(qrRequest.getHeader());
        generateQRResponse.getHeader().setReqResGb(CommonConstant.RES_GB);


        //generateQRResponse.setData(data);

        if (!ValidationHelper.isValid(qrRequest)) {
            //System.out.println(ValidationHelper.fieldNames.get());
            generateQRResponse.getHeader().setErrCode(ErrorDefination.ERR_004.getErrCode());
            generateQRResponse.getHeader().setErrDesc(ErrorDefination.ERR_004.getDesc() + ": " + ValidationHelper.fieldNames.get());

        } else if (!CommonConstant.listServiceCode.contains(qrRequest.getData().getQrInfo().getServiceCode().toUpperCase().trim())) {
            generateQRResponse.getHeader().setErrCode(ErrorDefination.ERR_005.getErrCode());
            generateQRResponse.getHeader().setErrDesc(ErrorDefination.ERR_005.getDesc());
        } else if (!checkBankCode(qrRequest.getHeader().getBkCd())) {
            generateQRResponse.getHeader().setErrCode(ErrorDefination.ERR_007.getErrCode());
            generateQRResponse.getHeader().setErrDesc(ErrorDefination.ERR_007.getDesc());
        } else {

            String qrString = genQRString(qrRequest) + genCRC(genQRString(qrRequest));

            if (qrString == null || qrString.trim().isEmpty()) {
                generateQRResponse.getHeader().setErrCode(ErrorDefination.ERR_001.getErrCode());
                generateQRResponse.getHeader().setErrDesc(ErrorDefination.ERR_001.getDesc());
            } else {
                String qrThemeImage = genBase64FromImage(genQRImageTheme(qrString, qrRequest));

                generateQRResponse.setData(data);
                generateQRResponse.getData().setResponseCode(ErrorDefination.ERR_OOO.getErrCode());
                generateQRResponse.getData().setResponseDesc(ErrorDefination.ERR_OOO.getDesc());
                generateQRResponse.getData().setQrImage(qrThemeImage);
                generateQRResponse.getData().setQrString(qrString);

                if (!qrRequest.getData().getCreatedUser().trim().isEmpty()) {
                    createQREntity(qrRequest, null, "QRIBFT", genBase64FromImage(genQRImage(qrString)), qrThemeImage);
                }
            }

        }
        return generateQRResponse;
    }

    @Override
    public DeCodeQRResponse parseQRString(DeCodeQRRequest deCodeQRRequest) throws UnsupportedEncodingException {

        deCodeQRRequest.getHeader().setBrCd(CommonConstant.BRAND_CODE);
        deCodeQRRequest.getHeader().setTrnDt(CommonConstant.TRANSACTION_DATE);
        deCodeQRRequest.getHeader().setDirection(CommonConstant.DIRECTION);
        deCodeQRRequest.getHeader().setReqResGb(CommonConstant.REQ_GB);
        deCodeQRRequest.getHeader().setRefNo(CommonConstant.REFERENCE_NUMBER);


        DeCodeQRResponse deCodeQRResponse = new DeCodeQRResponse();
        DeCodeQRResponse.Data data = new DeCodeQRResponse.Data();
        DeCodeQRResponse.QrInfo qrInfo = new DeCodeQRResponse.QrInfo();


        deCodeQRResponse.setHeader(deCodeQRRequest.getHeader());
        deCodeQRResponse.getHeader().setReqResGb(CommonConstant.RES_GB);

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

            String valueOfID62 = linkedHashMapQRString.get(QRCodeFormat.ADDITION_INFO.getId());
            if (valueOfID62 != null) {
                putHashMapAndCutQrString(QRCodeFormat.ADDITION_INFO.getId() + ".", linkedHashMapQRString, valueOfID62);
                deCodeQRResponse.getData().getQrInfo().setAdditionInfo(linkedHashMapQRString.get(QRCodeFormat.ADDITION_INFO.getId() + "." + QRCodeFormat.PURPOSE_TRANSACTION.getId()));
            }


            deCodeQRResponse.getData().setResponseCode(ErrorDefination.ERR_OOO.getErrCode());
            deCodeQRResponse.getData().setResponseDesc(ErrorDefination.ERR_OOO.getDesc());
            deCodeQRResponse.getData().getQrInfo().setServiceCode(linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.SERVICE_CODE.getId()));
            deCodeQRResponse.getData().getQrInfo().setCustomerId(linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.MEMBER_BANKS.getId() + "." + QRCodeFormat.CONSUMER_ID.getId()));
            deCodeQRResponse.getData().getQrInfo().setTransCurrency(linkedHashMapQRString.get(QRCodeFormat.TRANSACTION_CURRENCY.getId()));
            deCodeQRResponse.getData().getQrInfo().setTransAmount(linkedHashMapQRString.get(QRCodeFormat.TRANSACTION_AMOUNT.getId()));
            deCodeQRResponse.getData().getQrInfo().setCountryCode(linkedHashMapQRString.get(QRCodeFormat.COUNTRY_CODE.getId()));


            deCodeQRResponse.getData().getQrInfo().setCrc(linkedHashMapQRString.get(QRCodeFormat.CRC.getId()));

            deCodeQRResponse.getHeader().setBkCd(linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.MEMBER_BANKS.getId() + "." + QRCodeFormat.BNB_ID.getId()));


        }

        return deCodeQRResponse;
    }

    @Override
    public GenerateQRResponse genAdQR(GenerateAdQR qrRequest) throws IOException, WriterException {

        qrRequest.getHeader().setBrCd(CommonConstant.BRAND_CODE);
        qrRequest.getHeader().setTrnDt(CommonConstant.TRANSACTION_DATE);
        qrRequest.getHeader().setDirection(CommonConstant.DIRECTION);
        qrRequest.getHeader().setReqResGb(CommonConstant.REQ_GB);
        qrRequest.getHeader().setRefNo(CommonConstant.REFERENCE_NUMBER);

        GenerateQRResponse generateQRResponse = new GenerateQRResponse();
        GenerateQRResponse.Data data = new GenerateQRResponse.Data();

        generateQRResponse.setHeader(qrRequest.getHeader());
        generateQRResponse.getHeader().setReqResGb(CommonConstant.RES_GB);


        if (!ValidationHelper.isValid(qrRequest)) {
            //System.out.println(ValidationHelper.fieldNames.get());
            generateQRResponse.getHeader().setErrCode(ErrorDefination.ERR_004.getErrCode());
            generateQRResponse.getHeader().setErrDesc(ErrorDefination.ERR_004.getDesc() + ": " + ValidationHelper.fieldNames.get());

        } else {
            generateQRResponse.setData(data);
            generateQRResponse.getData().setResponseCode(ErrorDefination.ERR_OOO.getErrCode());
            generateQRResponse.getData().setResponseDesc(ErrorDefination.ERR_OOO.getDesc());

            String qrString = qrRequest.getData().getQrInfo().getText().trim();
            String qrType = qrRequest.getData().getQrInfo().getAdType().trim();
            String qrImage = genBase64FromImage(genQRImage(qrString));


            generateQRResponse.getData().setQrImage(qrImage);
            if (!qrRequest.getData().getCreatedUser().trim().isEmpty()) {
                createQREntity(null, qrRequest, qrType, qrImage, null);
            }


        }
        return generateQRResponse;
    }


    private void createQREntity(GenerateQRRequest qrRequest, GenerateAdQR adQR, String qrType, String qrImage, String qrThemeImage) {
        QRCodeEntity qrCodeEntity = new QRCodeEntity();

        //header
        if (qrRequest != null) {
            qrCodeEntity.setBkCd(qrRequest.getHeader().getBkCd());
            qrCodeEntity.setBrCd(qrRequest.getHeader().getBrCd());
            qrCodeEntity.setTrnDt(qrRequest.getHeader().getTrnDt());
            qrCodeEntity.setRefNo(qrRequest.getHeader().getRefNo());

            //data
            qrCodeEntity.setCreatedUser(qrRequest.getData().getCreatedUser());
            qrCodeEntity.setChannel(qrRequest.getData().getChannel());


            //qrInfoIBFT
            qrCodeEntity.setServiceCode(qrRequest.getData().getQrInfo().getServiceCode());
            qrCodeEntity.setCustomerId(qrRequest.getData().getQrInfo().getCustomerId());
            qrCodeEntity.setCustomerName(qrRequest.getData().getQrInfo().getCustomerName());
            qrCodeEntity.setTransCurrency(qrRequest.getData().getQrInfo().getTransCurrency());
            qrCodeEntity.setCountryCode(qrRequest.getData().getQrInfo().getCountryCode());
            qrCodeEntity.setTransAmount(qrRequest.getData().getQrInfo().getTransAmount());
            qrCodeEntity.setAdditionInfo(qrRequest.getData().getQrInfo().getAdditionInfo());
            qrCodeEntity.setMerchantCode(qrRequest.getData().getQrInfo().getMerchantCode());
            qrCodeEntity.setMerchantName(qrRequest.getData().getQrInfo().getMerchantName());
            qrCodeEntity.setMerchantCity(qrRequest.getData().getQrInfo().getMerchantCity());

            qrCodeEntity.setQrName(qrRequest.getData().getQrInfo().getCustomerName());
            qrCodeEntity.setQrThemeImage(qrThemeImage);
        } else {
            qrCodeEntity.setBkCd(adQR.getHeader().getBkCd());
            qrCodeEntity.setBrCd(adQR.getHeader().getBrCd());
            qrCodeEntity.setTrnDt(adQR.getHeader().getTrnDt());
            qrCodeEntity.setRefNo(adQR.getHeader().getRefNo());

            //data
            qrCodeEntity.setCreatedUser(adQR.getData().getCreatedUser());
            qrCodeEntity.setChannel(adQR.getData().getChannel());

            //qrInfoAd
            qrCodeEntity.setText(adQR.getData().getQrInfo().getText());

            if (adQR.getData().getQrInfo().getAdType().equals("URL")) {
                qrCodeEntity.setQrName(adQR.getData().getQrInfo().getText());
            } else {
                qrCodeEntity.setQrName(getQRNameFromQRText(adQR.getData().getQrInfo().getText()));
            }
        }

        qrCodeEntity.setDirection(CommonConstant.DIRECTION);
        qrCodeEntity.setReqResGb(CommonConstant.RES_GB);
        qrCodeEntity.setQrType(qrType);

        //result
        qrCodeEntity.setQrImage(qrImage);
        qrCodeEntity.setEnabled(true);
        qrCodeEntity.setUpdateOn(LocalDateTime.now());

        qrCodeRepo.save(qrCodeEntity);


    }

    public List<QRCodeEntity> getAllQRCodes() {
        return qrCodeRepo.findAll();
    }

    public List<QrCodeDTORoleUser> finByCreatedUserRoleUser(String createdUser) {
        return qrCodeRepo.findByCreatedUserRoleUser(createdUser);
    }
    public List<QrCodeDTORoleUser> findByCreatedUserAndEnabledFalseRoleUser(String createdUser) {
        return qrCodeRepo.findByCreatedUserAndEnabledFalseRoleUser(createdUser);
    }

    public QRCodeEntity findByQrId(Long qrId) {
        return qrCodeRepo.findByQrId(qrId);
    }

    public void disableQRCode(Long qrId) {

        QRCodeEntity qrCodeEntity = qrCodeRepo.findByQrId(qrId);

        qrCodeEntity.setEnabled(false);
        qrCodeEntity.setUpdateOn(LocalDateTime.now());

        qrCodeRepo.save(qrCodeEntity);

    }

    public void enableQRCode(Long qrId) {

        QRCodeEntity qrCodeEntity = qrCodeRepo.findByQrId(qrId);

        qrCodeEntity.setEnabled(true);
        qrCodeEntity.setUpdateOn(LocalDateTime.now());

        qrCodeRepo.save(qrCodeEntity);

    }

    public String getQrThemeImageById(Long id) {
        return qrCodeRepo.getQrThemeImageById(id);
    }

    public QrCodeDTORoleUser getQrDTOById(Long id) {
        return qrCodeRepo.getQrDTOById(id);
    }

    public void changeQRName(ChangeQRNameRequest changeQRNameRequest) {
        QRCodeEntity qrCodeEntity = qrCodeRepo.findByQrId(changeQRNameRequest.getQrId());

        qrCodeEntity.setQrName(changeQRNameRequest.getQrName().trim());


        qrCodeRepo.save(qrCodeEntity);
    }

    public void deleteQR(Long id){
        qrCodeRepo.deleteById(id);
    }


}
