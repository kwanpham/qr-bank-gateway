package com.infoplusvn.qrbankgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infoplusvn.qrbankgateway.dto.common.payment.TransactionDTO;
import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestGW;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseGW;
import com.infoplusvn.qrbankgateway.entity.TransactionEntity;

import java.util.List;

public interface QRPaymentService {

    PaymentResponseGW genPaymentResGW(PaymentRequestGW paymentRequestGW) throws JsonProcessingException;


}
