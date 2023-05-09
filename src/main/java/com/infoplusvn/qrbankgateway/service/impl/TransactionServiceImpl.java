package com.infoplusvn.qrbankgateway.service.impl;

import com.infoplusvn.qrbankgateway.dto.common.payment.TransactionDTO;
import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestGW;
import com.infoplusvn.qrbankgateway.entity.TransactionActivityEntity;
import com.infoplusvn.qrbankgateway.entity.TransactionEntity;
import com.infoplusvn.qrbankgateway.repo.TransactionActivityRepo;
import com.infoplusvn.qrbankgateway.repo.TransactionRepo;
import com.infoplusvn.qrbankgateway.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private TransactionActivityRepo activityRepo;

    @Override
    public TransactionEntity createTransaction(PaymentRequestGW paymentRequestGW,String type, String transStep, String transStepStatus, String transStepDesc) {

        TransactionEntity transaction = new TransactionEntity();

        transaction.setBankCode(paymentRequestGW.getHeader().getBkCd());

        transaction.setBrandCode(paymentRequestGW.getHeader().getBrCd());

        transaction.setTransDate(paymentRequestGW.getHeader().getTrnDt());

        transaction.setRefferenceNo(paymentRequestGW.getHeader().getRefNo());

        transaction.setChannel(paymentRequestGW.getData().getPayment().getChannel());

        transaction.setDirection(paymentRequestGW.getHeader().getDirection());

        transaction.setTransStep(transStep);

        transaction.setTransStepStatus(transStepStatus);

        transaction.setTransStepDesc(transStepDesc);

        transaction.setServiceCode("QR_PUSH");

        transaction.setSenderBank(paymentRequestGW.getHeader().getBkCd());

        transaction.setReceiverBank(paymentRequestGW.getData().getParticipant().getReceivingInstitutionId());

        transaction.setTransAmount(paymentRequestGW.getData().getAmount());

        transaction.setTransCcy(paymentRequestGW.getData().getCurrency());

        transaction.setDebitAcct(paymentRequestGW.getData().getRecipientAccount());

        transaction.setCreditAcct(paymentRequestGW.getData().getRecipientAccount());

        transaction.setReceivedDt(LocalDateTime.now());

        transaction.setType(type);

        transaction.setBillNumber(paymentRequestGW.getData().getOrder().getBillNumber());

        transaction.setOrganizationName(paymentRequestGW.getData().getRecipient().getFullName());

        transaction.setCountry(paymentRequestGW.getData().getPayment().getLocation());

        return transactionRepo.save(transaction);
    }

    @Override
    public void updateSentDt(TransactionEntity transaction, LocalDateTime time) {
        transaction.setSentDt(time);
        transactionRepo.save(transaction);
    }

    @Override
    public void updateReceivedDt(TransactionEntity transaction, LocalDateTime time) {
        transaction.setReceivedDt(time);
        transactionRepo.save(transaction);
    }

    @Override
    public void updateTransStep(TransactionEntity transaction, String transStep, String transStepStatus, String transStepDesc) {

        transaction.setTransStep(transStep);

        transaction.setTransStepStatus(transStepStatus);

        transaction.setTransStepDesc(transStepDesc);

        transactionRepo.save(transaction);
    }

    @Override
    public void updateErrCodeDesc(TransactionEntity transaction, String errCode, String errDesc) {

        transaction.setErrorCode(errCode);

        transaction.setErrorDesc(errDesc);

        transactionRepo.save(transaction);
    }

    @Override
    public void createActivity(TransactionEntity transaction, String msgContent, String errCode, String errDesc, String activityStep, String activityStepStatus) {

        TransactionActivityEntity activity = new TransactionActivityEntity();

        activity.setTransaction(transaction);

        activity.setMsgContent(msgContent);

        activity.setErrorCode(errCode);

        activity.setErrorDesc(errDesc);

        activity.setCreatedDt(LocalDateTime.now());

        activity.setActivityStep(activityStep);

        activity.setActivityStepStatus(activityStepStatus);

        activityRepo.save(activity);

    }

//    @Override
//    public List<TransactionDTO> getAllTransactions() {
//        return transactionRepo.getAllTransaction();
//    }

    @Override
    public List<TransactionEntity> getAllTransactions() {
        return transactionRepo.findAll();
    }
}
