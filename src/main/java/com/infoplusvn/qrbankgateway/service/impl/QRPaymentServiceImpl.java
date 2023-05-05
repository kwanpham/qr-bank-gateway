package com.infoplusvn.qrbankgateway.service.impl;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqInfoGW;
import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestGW;
import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResInfoGW;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseGW;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseNAPAS;
import com.infoplusvn.qrbankgateway.entity.TransactionEntity;
import com.infoplusvn.qrbankgateway.exception.ValidationHelper;
import com.infoplusvn.qrbankgateway.repo.TransactionActivityRepo;
import com.infoplusvn.qrbankgateway.repo.TransactionRepo;
import com.infoplusvn.qrbankgateway.service.QRPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
public class QRPaymentServiceImpl implements QRPaymentService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private TransactionActivityRepo activityRepo;

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
        payment.setGeneration_method("12");
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
        data.setResponseCode("00");
        data.setResponseDesc("Success");
        data.setPayment(payment);
        data.setAmount(responseNAPAS.getPayload().getAmount());
        data.setCurrency(responseNAPAS.getPayload().getCurrency());


        paymentResponseGW.setData(data);

        return paymentResponseGW;

    }

    @Override
    public PaymentResponseGW genPaymentResGW(PaymentRequestGW paymentRequestGW) {
        log.info("----------------LUỒNG ĐI PAYMENT -----------------");
        log.info("STEP 1: ISSUER BANK -> InfoGW: " + paymentRequestGW);
        TransactionEntity transaction = createTransaction(paymentRequestGW,"R","00","Success");
        log.info("createTrans: " + transaction);

        PaymentResponseGW paymentResponseGW = new PaymentResponseGW();
        HeaderInfoGW header = paymentRequestGW.getHeader();
        header.setReqResGb("RES");
        paymentResponseGW.setHeader(header);

        if (!ValidationHelper.isValid(paymentRequestGW)) {
            header.setErrCode("004");
            header.setErrDesc("Wrong message format: " + ValidationHelper.fieldNames.get());
            updateTransStep(transaction,"R","XX",header.getErrDesc());
            log.info("STEP 2: InfoGW -> ISSUER BANK: " + paymentResponseGW);
        } else {
            //mapping dữ liệu từ reqGW -> reqNAPAS
            PaymentRequestNAPAS paymentRequestNAPAS = genMappingReqNAPAS(paymentRequestGW);
            log.info("STEP 2: InfoGW -> NAPAS: " + paymentRequestNAPAS);

            RestTemplate restTemplate = new RestTemplate();

            String apiUrl = "http://localhost:8029/benbank/qr/v1/issuer/payment";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo một đối tượng HttpEntity để đại diện cho toàn bộ yêu cầu POST
            HttpEntity<PaymentRequestNAPAS> requestDTO = new HttpEntity<>(paymentRequestNAPAS, headers);

            try {

                // Gọi API sử dụng phương thức POST và truyền vào body là đối tượng requestEntity
                //NAPAS gửi bản tin chuẩn NAPAS sang Ben Bank, sau đó nhận bản tin về theo chuẩn NAPAS
                PaymentResponseNAPAS paymentResponseNAPAS = restTemplate.postForObject(apiUrl, requestDTO, PaymentResponseNAPAS.class);
                log.info("STEP 3: NAPAS -> InfoGW: " + paymentResponseNAPAS);

                //mapping dữ liệu từ ResponseNAPAS sang ResponseInfoGW
                paymentResponseGW = genMappingResGW(paymentResponseNAPAS);
                paymentResponseGW.setHeader(header);
                log.info("STEP 4: InfoGW -> ISSUER BANK: " + paymentResponseGW);


            } catch (Exception ex) {
                log.error(String.valueOf(ex));
                return null;
            }
        }


        return paymentResponseGW;
    }

    private TransactionEntity createTransaction(PaymentRequestGW paymentRequestGW, String transStep, String transStepStatus, String transStepDesc) {

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

        return transactionRepo.save(transaction);
    }

    private void updateSentDt(TransactionEntity transaction, LocalDateTime time) {
        transaction.setSentDt(time);
        transactionRepo.save(transaction);
    }

    private void updateTransStep(TransactionEntity transaction, String transStep, String transStepStatus, String transStepDesc) {

        transaction.setTransStep(transStep);

        transaction.setTransStepStatus(transStepStatus);

        transaction.setTransStepDesc(transStepDesc);

        transactionRepo.save(transaction);
    }

    private void updateErrCodeDesc(TransactionEntity transaction,String errCode, String errDesc){

        transaction.setErrorCode(errCode);

        transaction.setErrorDesc(errDesc);

        transactionRepo.save(transaction);
    }
}
