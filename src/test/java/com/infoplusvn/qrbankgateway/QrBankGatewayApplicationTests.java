package com.infoplusvn.qrbankgateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootTest
class QrBankGatewayApplicationTests {

    @Test
    void contextLoads() {
        Random random = new Random();
        long randomNumber = (long) (random.nextLong() % 100000000000L);
        String formattedNumber = String.format("%011d", randomNumber);
        System.out.println("Số ngẫu nhiên 11 chữ số là: " + formattedNumber);

    }

}
