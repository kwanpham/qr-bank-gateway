package com.infoplusvn.qrbankgateway.service;

import com.infoplusvn.qrbankgateway.dto.common.Header;
import com.infoplusvn.qrbankgateway.dto.request.GenerateQRRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.util.*;

@SpringBootTest
class QrServiceTest {

    public final List<String> listServiceCode1 = Arrays.asList("QRPUSH", "QRCASH", "QRIBFTTC", "QRIBFTTA", "QRADVERTISE");

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

        request.getHeader().setBkCd("970415");
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

        System.out.println(qrService.genQRString(request) + qrService.genCRC(qrService.genQRString(request)));



    }

    @Test
    public void testGenCRC() throws UnsupportedEncodingException {
        System.out.println(Integer.toHexString(qrService.crc16("00020101021138630010A00000072701330006970436011997043686168422410170208QRIBFTTC53037045802VN6304".getBytes("ASCII"))).toUpperCase());
    }



    @Test
    public void testParseQr() {
        String qrString = "00020101021138560010A0000007270126000697041501121008712015370208QRIBFTTA53037045802VN630467E8";

        LinkedHashMap<String, String> linkedHashMapQRString = new LinkedHashMap<>();

        addHashMapAndCutQrString("",linkedHashMapQRString,qrString);

        String valueOfID38 = linkedHashMapQRString.get("38");
        addHashMapAndCutQrString("38.",linkedHashMapQRString,valueOfID38);

        String valueOfID38_01 = linkedHashMapQRString.get("38.01");
        addHashMapAndCutQrString("38.01.",linkedHashMapQRString,valueOfID38_01);


        System.out.println(linkedHashMapQRString);

    }
    public void addHashMapAndCutQrString(String string, LinkedHashMap<String, String> linkedHashMap, String qrString) {
        while (!qrString.isEmpty()) {
            linkedHashMap.put(string + qrString.substring(0,2), qrString.substring(4, 4 + Integer.parseInt(qrString.substring(2,4))));
            qrString = qrString.replace( qrString.substring(0,2) +  qrString.substring(2,4) + qrString.substring(4, 4 + Integer.parseInt(qrString.substring(2,4))),"");
        }
    }

    @Test
    public void testGenResponseBase64QR(){
        System.out.println(qrService.genBase64FromQRImage("00020101021138560010A0000007270126000697041501121008712015370208QRIBFTTA53037045802VN630467E8"));
    }

    @Test
    public void testLength(){
        System.out.println("00069704150112100871201537".length());
    }





}