package com.infoplusvn.qrbankgateway.service;

import com.infoplusvn.qrbankgateway.dto.common.payment.TransactionDTO;
import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestGW;
import com.infoplusvn.qrbankgateway.entity.TransactionActivityEntity;
import com.infoplusvn.qrbankgateway.entity.TransactionEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    TransactionEntity createTransaction(PaymentRequestGW paymentRequestGW,String type, String transStep, String transStepStatus, String transStepDesc);

    void updateSentDt(TransactionEntity transaction, LocalDateTime time);

    void updateReceivedDt(TransactionEntity transaction, LocalDateTime time);

    void updateTransStep(TransactionEntity transaction, String transStep, String transStepStatus, String transStepDesc);

    void updateErrCodeDesc(TransactionEntity transaction, String errCode, String errDesc);

    void createActivity(TransactionEntity transaction, String msgContent, String errCode, String errDesc, String activityStep, String activityStepStatus);

    List<TransactionDTO> getAllTransactions();

    //List<TransactionEntity> getAllTransactions();

    List<TransactionActivityEntity> getAllActivity();

    List<TransactionActivityEntity> getActivityByTransactionId(Long transactionId);

}
