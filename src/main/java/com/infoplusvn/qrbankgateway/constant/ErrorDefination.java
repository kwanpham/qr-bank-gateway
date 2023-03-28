package com.infoplusvn.qrbankgateway.constant;

public enum ErrorDefination {

    ERR_OOO("000","Successful"),
    ERR_001("001","System error"),
    ERR_002("002","Lost connection"),
    ERR_003("003","Wrong username and password"),
    ERR_004("004","Wrong message format"),
    ERR_005("005","Invalid service code"),
    ERR_006("006","Parsing data met error"),
    ERR_007("007","Invalid bank code"),
    ERR_008("008","CRC invalid"),
    ERR_009("009","Can not generate CRC"),
    ERR_010("010","Validate data meet error"),
    ERR_011("011","Duplicated transaction"),
    ERR_068("068","System timeout");

    private String errCode;
    private String desc;

    ErrorDefination(String errCode,String desc) {
        this.errCode = errCode;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
