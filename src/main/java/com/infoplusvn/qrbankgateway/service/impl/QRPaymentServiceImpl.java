package com.infoplusvn.qrbankgateway.service.impl;

import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.constant.ErrorDefination;
import com.infoplusvn.qrbankgateway.constant.PaymentConstant;
import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import com.infoplusvn.qrbankgateway.dto.common.payment.TransactionDTO;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqInfoGW;
import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestGW;
import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResInfoGW;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseGW;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseNAPAS;
import com.infoplusvn.qrbankgateway.entity.TransactionActivityEntity;
import com.infoplusvn.qrbankgateway.entity.TransactionEntity;
import com.infoplusvn.qrbankgateway.exception.ValidationHelper;
import com.infoplusvn.qrbankgateway.repo.TransactionActivityRepo;
import com.infoplusvn.qrbankgateway.repo.TransactionRepo;
import com.infoplusvn.qrbankgateway.service.QRPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class QRPaymentServiceImpl implements QRPaymentService {

    @Autowired
    private TransactionServiceImpl transactionService;


    private PaymentRequestNAPAS genMappingReqNAPAS(PaymentRequestGW request) {

        PaymentRequestNAPAS paymentRequestNAPAS = new PaymentRequestNAPAS();
        PaymentRequestNAPAS.Payload payload = new PaymentRequestNAPAS.Payload();
        PaymentRequestNAPAS.Payment payment = new PaymentRequestNAPAS.Payment();
        PaymentRequestNAPAS.Sender sender = new PaymentRequestNAPAS.Sender();
        PaymentRequestNAPAS.Participant participant = new PaymentRequestNAPAS.Participant();
        PaymentRequestNAPAS.Recipient recipient = new PaymentRequestNAPAS.Recipient();
        PaymentRequestNAPAS.Address senderAddress = new PaymentRequestNAPAS.Address();
        PaymentRequestNAPAS.Address recipientAddress = new PaymentRequestNAPAS.Address();
        PaymentRequestNAPAS.OrderInfo orderInfo = new PaymentRequestNAPAS.OrderInfo();

        //payment
        payment.setFunding_reference(request.getData().getFundingReference());
        payment.setType("QR_PUSH");
        payment.setGeneration_method(CommonConstant.METHOD_DYNAMIC);
        payment.setChannel(request.getData().getPayment().getChannel());
        payment.setDevice_id(request.getData().getPayment().getDeviceId());
        payment.setLocation(request.getData().getPayment().getLocation());
        payment.setTransaction_local_date_time(request.getData().getPayment().getLocationDateTime());
        payment.setInterbank_amount(request.getData().getPayment().getInterbankAmount());
        payment.setInterbank_currency(request.getData().getPayment().getInterbankCurrency());
        payment.setExchange_rate(request.getData().getPayment().getExchangeRate());
        payment.setPayment_reference(request.getHeader().getRefNo());
        payment.setTrace(request.getData().getPayment().getTrace());

        //senderAddress
        senderAddress.setLine1(request.getData().getSender().getAddress().getLine1());
        senderAddress.setLine2(request.getData().getSender().getAddress().getLine2());
        senderAddress.setCountry(request.getData().getSender().getCountry());
        senderAddress.setPhone(request.getData().getSender().getPhone());

        //sender
        sender.setFull_name(request.getData().getSender().getFullName());
        sender.setAddress(senderAddress);

        //participant
        participant.setReceiving_institution_id(request.getData().getParticipant().getReceivingInstitutionId());

        //recipientAddress
        recipientAddress.setLine1(request.getData().getRecipient().getAddress().getLine1());
        recipientAddress.setLine2(request.getData().getRecipient().getAddress().getLine2());

        //recipient
        recipient.setFull_name(request.getData().getRecipient().getFullName());
        recipient.setAddress(recipientAddress);

        //orderInfo
        orderInfo.setBill_number(request.getData().getOrder().getBillNumber());

        //payload
        payload.setPayment(payment);
        payload.setAmount(request.getData().getAmount());
        payload.setCurrency(request.getData().getCurrency());
        payload.setSender_account(request.getData().getSenderAccount());
        payload.setSender(sender);
        payload.setParticipant(participant);
        payload.setRecipient_account(request.getData().getRecipientAccount());
        payload.setRecipient(recipient);
        payload.setAdditional_message(request.getData().getAdditionMessage());
        payload.setOrder_info(orderInfo);


        paymentRequestNAPAS.setPayload(payload);

        return paymentRequestNAPAS;
    }

    private PaymentResponseGW genMappingResGW(PaymentResponseNAPAS responseNAPAS) {

        PaymentResponseGW paymentResponseGW = new PaymentResponseGW();

        PaymentResponseGW.Data data = new PaymentResponseGW.Data();
        PaymentResponseGW.Payment payment = new PaymentResponseGW.Payment();

        //payment
        payment.setTrace(responseNAPAS.getPayload().getPayment().getTrace());
        payment.setExchangeRate(responseNAPAS.getPayload().getPayment().getExchange_rate());
        payment.setAuthorizationCode(responseNAPAS.getPayload().getPayment().getAuthorization_code());
        payment.setReference(responseNAPAS.getPayload().getPayment().getReference());

        //data
        data.setResponseCode(PaymentConstant.STEP_STATUS_SUCCESS_CODE);
        data.setResponseDesc(PaymentConstant.STEP_STATUS_SUCCESS_DESC);
        data.setPayment(payment);
        data.setAmount(responseNAPAS.getPayload().getAmount());
        data.setCurrency(responseNAPAS.getPayload().getCurrency());


        paymentResponseGW.setData(data);

        return paymentResponseGW;

    }

    private void sentToIssuerBank(PaymentResponseGW paymentResponseGW, HeaderInfoGW header, TransactionEntity transaction) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String apiUrl = PaymentConstant.API_URL_SENT_TO_CORE;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo một đối tượng HttpEntity để đại diện cho toàn bộ yêu cầu POST
            HttpEntity<PaymentResponseGW> requestDTO = new HttpEntity<>(paymentResponseGW, headers);

            // Gọi API sử dụng phương thức POST và truyền vào body là đối tượng requestEntity
            //InfoGW gửi bản tin chuẩn GW sang Issuer Bank
            restTemplate.postForLocation(apiUrl, requestDTO);

            //nếu gửi sang issuerBank thành công
            transactionService.updateTransStep(transaction, PaymentConstant.STEP_SENT, PaymentConstant.STEP_STATUS_SUCCESS_CODE, PaymentConstant.STEP_STATUS_SUCCESS_DESC);
            if (paymentResponseGW.getData() == null) {
                transactionService.updateErrCodeDesc(transaction, paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc());
                transactionService.createActivity(transaction, String.valueOf(paymentResponseGW.getData()), paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc(), PaymentConstant.ACTIVITY_STEP_SEND_TO_CORE, PaymentConstant.STEP_STATUS_SUCCESS_CODE);
            } else {
                transactionService.updateErrCodeDesc(transaction, paymentResponseGW.getData().getResponseCode(), paymentResponseGW.getData().getResponseDesc());
                transactionService.createActivity(transaction, String.valueOf(paymentResponseGW.getData()), paymentResponseGW.getData().getResponseCode(), paymentResponseGW.getData().getResponseDesc(), PaymentConstant.ACTIVITY_STEP_SEND_TO_CORE, PaymentConstant.STEP_STATUS_SUCCESS_CODE);
            }
            transactionService.updateSentDt(transaction, LocalDateTime.now());



        } catch (Exception ex) {
            //nếu gửi sang issuerBank không thành công
            header.setErrCode(ErrorDefination.ERR_068.getErrCode());
            header.setErrDesc(ErrorDefination.ERR_068.getDesc());
            paymentResponseGW.setHeader(header);

            transactionService.updateTransStep(transaction, PaymentConstant.STEP_SENT, PaymentConstant.STEP_STATUS_ERROR_CODE, PaymentConstant.STEP_STATUS_ERROR_DESC);
            transactionService.updateErrCodeDesc(transaction, paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc());
            transactionService.updateSentDt(transaction, LocalDateTime.now());

            transactionService.createActivity(transaction, String.valueOf(paymentResponseGW.getData()), paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc(), PaymentConstant.ACTIVITY_STEP_SEND_TO_CORE, PaymentConstant.STEP_STATUS_ERROR_CODE);

            log.error("Lỗi: " + ex);
        }
    }

    @Override
    public PaymentResponseGW genPaymentResGW(PaymentRequestGW paymentRequestGW) {

        PaymentResponseGW paymentResponseGW = new PaymentResponseGW();
        HeaderInfoGW header = paymentRequestGW.getHeader();
        header.setReqResGb(CommonConstant.RES_GB);


        log.info("----------------LUỒNG ĐI PAYMENT -----------------");
        try {
            TransactionEntity transaction = transactionService.createTransaction(paymentRequestGW,"Payment", PaymentConstant.STEP_RECV, PaymentConstant.STEP_STATUS_SUCCESS_CODE, PaymentConstant.STEP_STATUS_SUCCESS_DESC);
            transactionService.createActivity(transaction, String.valueOf(paymentRequestGW.getData()), null, null, PaymentConstant.ACTIVITY_STEP_RECV_FROM_CORE, PaymentConstant.STEP_STATUS_SUCCESS_CODE);
            if (!ValidationHelper.isValid(paymentRequestGW)) {
                //convert bản tin với lỗi tường minh gửi cho core
                header.setErrCode(ErrorDefination.ERR_004.getErrCode());
                header.setErrDesc(ErrorDefination.ERR_004.getDesc() + ": " + ValidationHelper.fieldNames.get());
                paymentResponseGW.setHeader(header);

                //nếu bản tin sai định dạng thì cập nhật lại là nhận được bản tin sai với lỗi tường minh
                transactionService.updateTransStep(transaction, PaymentConstant.STEP_RECV, PaymentConstant.STEP_STATUS_ERROR_CODE, paymentResponseGW.getHeader().getErrDesc());
                log.info("STEP 1: RECV_FROM_CORE: " + transaction);

                //sent to core
//                try {
//                    RestTemplate restTemplate = new RestTemplate();
//
//                    String apiUrl = "http://localhost:8029/issuerbank/qr/v1/issuer/payment";
//
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//
//                    // Tạo một đối tượng HttpEntity để đại diện cho toàn bộ yêu cầu POST
//                    HttpEntity<PaymentResponseGW> requestDTO = new HttpEntity<>(paymentResponseGW, headers);
//
//                    // Gọi API sử dụng phương thức POST và truyền vào body là đối tượng requestEntity
//                    //InfoGW gửi bản tin chuẩn GW sang Issuer Bank
//                    restTemplate.postForLocation(apiUrl, requestDTO);
//
//                    //nếu gửi sang issuerBank thành công
//                    updateTransStep(transaction, "S", "00", "Success");
//                    updateErrCodeDesc(transaction, paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc());
//                    updateSentDt(transaction, LocalDateTime.now());
//
//                    createActivity(transaction, String.valueOf(paymentResponseGW.getData()), paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc(), "SEND_TO_CORE", "00");
//                    log.info("STEP 2: SEND_TO_CORE: " + transaction);
//
//                    return paymentResponseGW;
//                } catch (Exception ex){
//                    //nếu gửi sang issuerBank không thành công
//                    header.setErrCode("068");
//                    header.setErrDesc("System timeout");
//                    paymentResponseGW.setHeader(header);
//
//                    updateTransStep(transaction, "S", "XX", "Sent error");
//                    updateErrCodeDesc(transaction, paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc());
//                    updateSentDt(transaction, LocalDateTime.now());
//
//                    createActivity(transaction, String.valueOf(paymentResponseGW.getData()), paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc(), "SEND_TO_CORE", "XX");
//                    log.info("STEP 2: SEND_TO_CORE: " + transaction);
//                    log.error("Lỗi: " + ex);
//                    return paymentResponseGW;
//                }
                sentToIssuerBank(paymentResponseGW, header, transaction);
                log.info("STEP 2: SEND_TO_CORE: " + transaction);

                return paymentResponseGW;

            } else {

                //nếu bản tin đúng thì cập nhật lại là nhận được được bản tin đúng
                //updateTransStep(transaction, "R", "00", "Success");
                log.info("STEP 1: RECV_FROM_CORE: " + transaction);

                //mapping dữ liệu từ reqGW -> reqNAPAS
                PaymentRequestNAPAS paymentRequestNAPAS = genMappingReqNAPAS(paymentRequestGW);

                transactionService.updateTransStep(transaction, PaymentConstant.STEP_SENT, PaymentConstant.STEP_STATUS_SUCCESS_CODE, PaymentConstant.STEP_STATUS_SUCCESS_DESC);
                transactionService.updateSentDt(transaction, LocalDateTime.now());

                //sent to NAPAS
                try {
                    RestTemplate restTemplate = new RestTemplate();

                    String apiUrl = PaymentConstant.API_URL_SENT_TO_NAPAS;

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    // Tạo một đối tượng HttpEntity để đại diện cho toàn bộ yêu cầu POST
                    HttpEntity<PaymentRequestNAPAS> requestDTO = new HttpEntity<>(paymentRequestNAPAS, headers);

                    // Gọi API sử dụng phương thức POST và truyền vào body là đối tượng requestEntity
                    //NAPAS gửi bản tin chuẩn NAPAS sang Ben Bank, sau đó nhận bản tin về theo chuẩn NAPAS
                    PaymentResponseNAPAS paymentResponseNAPAS = restTemplate.postForObject(apiUrl, requestDTO, PaymentResponseNAPAS.class);
                    transactionService.createActivity(transaction, String.valueOf(paymentRequestNAPAS.getPayload()), null, null, PaymentConstant.ACTIVITY_STEP_SEND_TO_NAPAS, PaymentConstant.STEP_STATUS_SUCCESS_CODE);
                    log.info("STEP 2: SEND_TO_NAPAS: " + transaction);

                    // Nhận bản tin từ NAPAS
                    transactionService.updateTransStep(transaction, PaymentConstant.STEP_RECV, PaymentConstant.STEP_STATUS_SUCCESS_CODE, PaymentConstant.STEP_STATUS_SUCCESS_DESC);
                    transactionService.updateReceivedDt(transaction, LocalDateTime.now());
                    transactionService.updateErrCodeDesc(transaction, PaymentConstant.STEP_STATUS_SUCCESS_CODE, PaymentConstant.STEP_STATUS_SUCCESS_DESC);

                    transactionService.createActivity(transaction, String.valueOf(paymentResponseNAPAS.getPayload()), PaymentConstant.STEP_STATUS_SUCCESS_CODE, PaymentConstant.STEP_STATUS_SUCCESS_DESC, PaymentConstant.ACTIVITY_STEP_RECV_FROM_NAPAS, PaymentConstant.STEP_STATUS_SUCCESS_CODE);
                    log.info("STEP 3: RECV_FROM_NAPAS: " + transaction);

                    //mapping dữ liệu từ ResponseNAPAS sang ResponseInfoGW
                    paymentResponseGW = genMappingResGW(paymentResponseNAPAS);
                    paymentResponseGW.setHeader(header);

                    //sent to core

//                    try {
//                        RestTemplate restTemplateSendToCore = new RestTemplate();
//
//                        String apiUrlSendToCore = "http://localhost:8029/issuerbank/qr/v1/issuer/payment";
//
//                        // Tạo một đối tượng HttpEntity để đại diện cho toàn bộ yêu cầu POST
//                        HttpEntity<PaymentResponseGW> request = new HttpEntity<>(paymentResponseGW, headers);
//
//                        // Gọi API sử dụng phương thức POST và truyền vào body là đối tượng requestEntity
//                        //InfoGW gửi bản tin chuẩn GW sang Issuer Bank
//                        restTemplateSendToCore.postForLocation(apiUrlSendToCore, request);
//
//                        //nếu gửi sang issuerBank thành công
//                        updateTransStep(transaction, "S", "00", "Success");
//                        updateErrCodeDesc(transaction, paymentResponseGW.getData().getResponseCode(), paymentResponseGW.getData().getResponseDesc());
//                        updateSentDt(transaction, LocalDateTime.now());
//
//                        createActivity(transaction, String.valueOf(paymentResponseGW.getData()), paymentResponseGW.getData().getResponseCode(), paymentResponseGW.getData().getResponseDesc(), "SEND_TO_CORE", "00");
//                        log.info("STEP 4: SEND_TO_CORE: " + transaction);
//
//                        return paymentResponseGW;
//                    } catch (Exception ex) {
//                        //nếu gửi sang issuerBank không thành công
//                        header.setErrCode("068");
//                        header.setErrDesc("System timeout");
//                        paymentResponseGW.setHeader(header);
//
//                        updateTransStep(transaction, "S", "XX", "Sent error");
//                        updateErrCodeDesc(transaction, paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc());
//                        updateSentDt(transaction, LocalDateTime.now());
//
//                        createActivity(transaction, String.valueOf(paymentResponseGW.getData()), paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc(), "SEND_TO_CORE", "XX");
//                        log.info("STEP 4: SEND_TO_CORE: " + transaction);
//                        log.error("Lỗi: " + ex);
//                        return paymentResponseGW;
//                    }
                    sentToIssuerBank(paymentResponseGW, header, transaction);
                    log.info("STEP 4: SEND_TO_CORE: " + transaction);

                    return paymentResponseGW;


                } catch (Exception ex) {
                    //nếu không gửi được sang NAPAS
                    header.setErrCode(ErrorDefination.ERR_068.getErrCode());
                    header.setErrDesc(ErrorDefination.ERR_068.getDesc());
                    paymentResponseGW.setHeader(header);

                    transactionService.updateTransStep(transaction, PaymentConstant.STEP_SENT, PaymentConstant.STEP_STATUS_ERROR_CODE, PaymentConstant.STEP_STATUS_ERROR_DESC);
                    transactionService.updateSentDt(transaction, LocalDateTime.now());
                    transactionService.updateErrCodeDesc(transaction, paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc());

                    transactionService.createActivity(transaction, String.valueOf(paymentRequestNAPAS.getPayload()), paymentResponseGW.getHeader().getErrCode(), paymentResponseGW.getHeader().getErrDesc(), PaymentConstant.ACTIVITY_STEP_SEND_TO_NAPAS, PaymentConstant.STEP_STATUS_ERROR_CODE);
                    log.info("STEP 2: SEND_TO_NAPAS: " + transaction);

                    log.error("Lỗi: " + ex);
                    return paymentResponseGW;
                }
            }
        } catch (Exception ex) {
            log.info("STEP 1: ISSUER BANK -> InfoGW: " + paymentRequestGW);
            header.setErrCode(ErrorDefination.ERR_011.getErrCode());
            header.setErrDesc(ErrorDefination.ERR_011.getDesc());
            paymentResponseGW.setHeader(header);
            log.info("STEP 2: InfoGW -> ISSUER BANK: " + paymentResponseGW);
            log.error("Lỗi: " + ex);
            return paymentResponseGW;
        }
    }




//    private TransactionEntity createTransaction(PaymentRequestGW paymentRequestGW, String transStep, String transStepStatus, String transStepDesc) {
//
//        TransactionEntity transaction = new TransactionEntity();
//
//        transaction.setBankCode(paymentRequestGW.getHeader().getBkCd());
//
//        transaction.setBrandCode(paymentRequestGW.getHeader().getBrCd());
//
//        transaction.setTransDate(paymentRequestGW.getHeader().getTrnDt());
//
//        transaction.setRefferenceNo(paymentRequestGW.getHeader().getRefNo());
//
//        transaction.setChannel(paymentRequestGW.getData().getPayment().getChannel());
//
//        transaction.setDirection(paymentRequestGW.getHeader().getDirection());
//
//        transaction.setTransStep(transStep);
//
//        transaction.setTransStepStatus(transStepStatus);
//
//        transaction.setTransStepDesc(transStepDesc);
//
//        transaction.setServiceCode("QR_PUSH");
//
//        transaction.setSenderBank(paymentRequestGW.getHeader().getBkCd());
//
//        transaction.setReceiverBank(paymentRequestGW.getData().getParticipant().getReceivingInstitutionId());
//
//        transaction.setTransAmount(paymentRequestGW.getData().getAmount());
//
//        transaction.setTransCcy(paymentRequestGW.getData().getCurrency());
//
//        transaction.setDebitAcct(paymentRequestGW.getData().getRecipientAccount());
//
//        transaction.setCreditAcct(paymentRequestGW.getData().getRecipientAccount());
//
//        transaction.setReceivedDt(LocalDateTime.now());
//
//        return transactionRepo.save(transaction);
//    }
//
//    private void updateSentDt(TransactionEntity transaction, LocalDateTime time) {
//        transaction.setSentDt(time);
//        transactionRepo.save(transaction);
//    }
//
//    private void updateReceivedDt(TransactionEntity transaction, LocalDateTime time) {
//        transaction.setReceivedDt(time);
//        transactionRepo.save(transaction);
//    }
//
//    private void updateTransStep(TransactionEntity transaction, String transStep, String transStepStatus, String transStepDesc) {
//
//        transaction.setTransStep(transStep);
//
//        transaction.setTransStepStatus(transStepStatus);
//
//        transaction.setTransStepDesc(transStepDesc);
//
//        transactionRepo.save(transaction);
//    }
//
//    private void updateErrCodeDesc(TransactionEntity transaction, String errCode, String errDesc) {
//
//        transaction.setErrorCode(errCode);
//
//        transaction.setErrorDesc(errDesc);
//
//        transactionRepo.save(transaction);
//    }
//
//    private void createActivity(TransactionEntity transaction, String msgContent, String errCode, String errDesc, String activityStep, String activityStepStatus) {
//
//        TransactionActivityEntity activity = new TransactionActivityEntity();
//
//        activity.setTransaction(transaction);
//
//        activity.setMsgContent(msgContent);
//
//        activity.setErrorCode(errCode);
//
//        activity.setErrorDesc(errDesc);
//
//        activity.setCreatedDt(LocalDateTime.now());
//
//        activity.setActivityStep(activityStep);
//
//        activity.setActivityStepStatus(activityStepStatus);
//
//        activityRepo.save(activity);
//
//    }


}
