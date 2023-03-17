package com.infoplusvn.qrbankgateway.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.infoplusvn.qrbankgateway.dto.common.Header;
import com.infoplusvn.qrbankgateway.dto.request.GenerateQRRequest;
import com.infoplusvn.qrbankgateway.dto.response.GenerateQRResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class QrServiceTest {

    @Autowired
    QrService qrService;

    @Test
    public void testGenQr() throws UnsupportedEncodingException {
        GenerateQRRequest request = new GenerateQRRequest();

        Header header = new Header();

        GenerateQRRequest.Data data = new GenerateQRRequest.Data();

        GenerateQRRequest.QrInfo qrInfo = new GenerateQRRequest.QrInfo();

        request.setHeader(header);

        request.setData(data);

        data.setQrInfo(qrInfo);

        request.getHeader().setBkCd("KEBHANABANK");
        request.getHeader().setBrCd("HN");
        request.getHeader().setTrnDt("20230101");
        request.getHeader().setDirection("O");
        request.getHeader().setReqResGb("REQ");
        request.getHeader().setRefNo("2023010109000000001");
        request.getHeader().setErrDesc(null);
        request.getHeader().setErrCode(null);



        request.getData().getQrInfo().setServiceCode("QRIBFTTA");

        request.getData().getQrInfo().setCustomerId("100871201537");

        request.getData().getQrInfo().setTransCurrency("704");

        request.getData().getQrInfo().setCountryCode("VN");

        request.getData().setCreatedUser("huyleq");

        request.getData().setChannel("M");


        //gen ma qr

        System.out.println(qrService.genResponseQrIBFTStatic(request));

    }

    @Test
    public void testGenCRC() throws UnsupportedEncodingException {
        System.out.println(Integer.toHexString(qrService.crc16("00020101021138600010A00000072701300006970403011621129950446040250208QRIBFTTA53037045802VN6304".getBytes("ASCII"))).toUpperCase());
    }



    @Test
    public void testParseQr() {

        //parse ma qr
        System.out.println(qrService.ParseQRString("00020101021138560010A0000007270126000697041501121008712015370206QRPUSH53037045802VN630467E8"));

    }

    @Test
    public void testResponseBase64QR(){
        System.out.println(qrService.genBase64FromQRImage("00020101021138560010A0000007270126000697041501121008712015370208QRIBFTTA53037045802VN630467E8"));

    }



}