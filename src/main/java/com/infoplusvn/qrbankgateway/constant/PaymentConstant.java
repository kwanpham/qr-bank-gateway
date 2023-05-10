package com.infoplusvn.qrbankgateway.constant;

public class PaymentConstant {
    public static final String STEP_SENT = "S";
    public static final String STEP_RECV = "R";
    public static final String STEP_STATUS_SUCCESS_CODE = "00";
    public static final String STEP_STATUS_SUCCESS_DESC = "Success";
    public static final String STEP_STATUS_ERROR_DESC = "Sent Error";
    public static final String RECV_ERR = "Receive Error";
    public static final String STEP_STATUS_ERROR_CODE = "XX";
    public static final String ACTIVITY_STEP_RECV_FROM_CORE = "RECV_FROM_CORE";
    public static final String ACTIVITY_STEP_SEND_TO_CORE = "SEND_TO_CORE";
    public static final String ACTIVITY_STEP_SEND_TO_NAPAS = "SEND_TO_NAPAS";
    public static final String ACTIVITY_STEP_RECV_FROM_NAPAS = "RECV_FROM_NAPAS";
    public static final String API_URL_SENT_TO_CORE = "http://localhost:8029/issuerbank/qr/v1/payment/sentCore";
    public static final String API_URL_SENT_TO_NAPAS = "http://localhost:8029/issuerbank/qr/v1/payment/sentNapas";

}
