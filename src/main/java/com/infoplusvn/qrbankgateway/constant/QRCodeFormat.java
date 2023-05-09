package com.infoplusvn.qrbankgateway.constant;


public enum QRCodeFormat {
    PAYLOAD_FORMAT_INDICATOR("00","01"), //Phiên bản dữ liệu
    POINT_OF_INITIATION_METHOD("01",""), //Phương thức khởi tạo: 11 là phương thức tĩnh, 12 là phương thức động
    CONSUMER_ACCOUNT_INFO("38",""), //Thông tin định danh người thụ hưởng

    //trong CONSUMER_ACCOUNT_INFO(ID 38) có 3 thuộc  tính là GUID(ID 00), MEMBER_BANKS(ID 01) và SERVICE_CODE(ID 02)
    GUID("00","A000000727"), //Định danh toàn cầu
    MEMBER_BANKS("01",""), //Tổ chức thanh toán

    //trong MEMBER_BANKS(ID 38.01) có 2 thuộc tính là BNB_ID(ID 00) và CONSUMER_ID (ID 01)
    BNB_ID("00",""), //Đơn vị thụ hưởng
    CONSUMER_ID("01",""), //Thông tin người thụ hưởng

    SERVICE_CODE("02",""), //Loại dịch vụ
    TRANSACTION_CURRENCY("53","704"), //Mã tiền tệ
    TRANSACTION_AMOUNT("54",""), //Số tiền giao dịch
    COUNTRY_CODE("58","VN"), //Mã quốc gia
    CARD_ACCEPTOR_NAME("59", ""), // Tên ĐVCNTT
    CARD_ACCEPTOR_CITY("60","HANOI"),

    //Trong Thông tin bổ sung có Mục đích giao dịch(ID 08)
    ADDITION_INFO("62",""), //Thông tin bổ sung
    PURPOSE_TRANSACTION("08",""), //Mục đích giao dịch

    CRC("63","");
    private String id;
    private String value;

    QRCodeFormat(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
