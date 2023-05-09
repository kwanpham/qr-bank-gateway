package com.infoplusvn.qrbankgateway.constant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommonConstant {
    public static final String QRIBFT_STATIC = "11";
    public static final String QRIBFT_DYNAMIC = "12";
    public static final String LENGTH_CRC = "04";
    public static final String FONT_STYLE = "Arial";
    public static final String TITLE_SO_TIEN = "Số tiền: ";
    public static final String TITLE_NOI_DUNG_CK = "Nội dung CK: ";
    public static final String TITLE_TEN_CHU_TK = "Tên chủ TK: ";
    public static final String TITLE_SO_TK = "Số TK: ";
    public static final String IMAGE_URL = "src/main/resources/image/baseImage.png";

    public static final int SIZE_IMAGE_QR = 400;
    public static final List<String> listServiceCode = Arrays.asList("QRPUSH", "QRCASH", "QRIBFTTC", "QRIBFTTA", "QRADVERTISE");

    public static final String BRAND_CODE = "HN";
    public static final String TRANSACTION_DATE = genTrnDt();
    public static final String DIRECTION_OUTBOUND = "O";;
    public static final String DIRECTION_INBOUND = "I";
    public static final String CARD_ACCEPTOR_CITY = "HANOI";
    public static final String REQ_GB = "REQ";
    public static final String RES_GB = "RES";
    public static final String REFERENCE_NUMBER = genRefNo(genTrnDt());

    public static final String ROLE_USER = "USER";

    private static String genTrnDt(){
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);

        return formattedDate;
    }

    private static String genRefNo(String date){
        Random random = new Random();

        long randomNumber = random.nextLong() % 100000000000L;
        String formattedNumber = String.format("%011d", randomNumber);

        return date + formattedNumber;
    }
}
