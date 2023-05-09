package com.infoplusvn.qrbankgateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "QRCODE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QRCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qr_id")
    private Long qrId;

    @Column(name = "qr_name")
    private String qrName;

    //username
    @Column(name = "created_user")
    private String createdUser;

    private String channel;

    @Column(name = "qr_type")
    private String qrType;

    //header
    @Column(name = "bk_cd")
    private String bkCd;

    @Column(name = "br_cd")
    private String brCd;

    @Column(name = "trn_dt")
    private String trnDt;

    private String direction;

    @Column(name = "req_res_gb")
    private String reqResGb;

    @Column(name = "ref_no")
    private String refNo;

    @Column(name = "err_code")
    private String errCode;

    @Column(name = "err_desc")
    private String errDesc;

    //qrInfoIBFT
    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "trans_currency")
    private String transCurrency;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "trans_amount")
    private String transAmount;

    @Column(name = "addition_info")
    private String additionInfo;

    @Column(name = "merchant_code")
    private String merchantCode;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "merchant_city")
    private String merchantCity;

    //qrInfoAD
    private String text;

    // result
    @Column(name = "qr_image")
    private String qrImage;

    @Column(name = "qr_theme_image")
    private String qrThemeImage;

    private boolean enabled;

    @Column(name = "update_on")
    private LocalDateTime updateOn;

}
