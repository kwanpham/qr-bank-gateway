package com.infoplusvn.qrbankgateway.service;

import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestGW;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseGW;

public interface QRPaymentService {

    PaymentResponseGW genPaymentResGW(PaymentRequestGW paymentRequestGW);
}
