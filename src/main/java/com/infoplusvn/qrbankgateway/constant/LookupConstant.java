package com.infoplusvn.qrbankgateway.constant;

public class LookupConstant {

    public static final String ACTIVITY_STEP_RECV_FROM_NAPAS = "RECV_FROM_NAPAS";
    public static final String ACTIVITY_STEP_SEND_TO_CORE= "SEND_TO_CORE";
    public static final String ACTIVITY_STEP_SEND_TO_NAPAS = "SEND_TO_NAPAS";
    public static final String ACTIVITY_STEP_RECV_FROM_CORE = "RECV_FROM_CORE";

    public static final String API_URL_SENT_TO_BEN_BANK = "http://localhost:8029/benbank/qr/v1/lookup/sentBenBank";
    public static final String API_URL_SENT_TO_NAPAS = "http://localhost:8029/benbank/qr/v1/lookup/sentNapas";
}
