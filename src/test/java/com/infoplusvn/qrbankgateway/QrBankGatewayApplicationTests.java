package com.infoplusvn.qrbankgateway;

import com.infoplusvn.qrbankgateway.constant.QRCodeFormat;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqNAPAS;

import com.infoplusvn.qrbankgateway.service.impl.QrIBTFServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@SpringBootTest
class QrBankGatewayApplicationTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private QrIBTFServiceImpl qrService;


    @Test
    public void genMappingReqInfoGW() throws UnsupportedEncodingException {


        LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(7));
        String dateTime = String.format("%1$tm%1$td%1$tH%1$tM%1$tS", now);
        String systemTraceNumber = String.format("%06d", 000000); // thay 123456 bằng số tuỳ ý
        String sendingGatewayBinCode = "970415";
        String uniqueId = String.format("Y%1$ty%1$td%1$tH%1$tM%1$tS%2$s", now, "nnnnnn"); // thay nnnnnn bằng chuỗi tuỳ ý
        String result = dateTime + systemTraceNumber + sendingGatewayBinCode + uniqueId;
        System.out.println(result);
    }



}

