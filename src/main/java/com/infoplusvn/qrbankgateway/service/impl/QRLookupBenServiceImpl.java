package com.infoplusvn.qrbankgateway.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoplusvn.qrbankgateway.constant.ErrorDefination;
import com.infoplusvn.qrbankgateway.constant.LookupConstant;
import com.infoplusvn.qrbankgateway.constant.PaymentConstant;
import com.infoplusvn.qrbankgateway.constant.QRCodeFormat;
import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import com.infoplusvn.qrbankgateway.dto.common.HeaderNAPAS;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqInfoGW;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResInfoGW;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseGW;
import com.infoplusvn.qrbankgateway.entity.TransactionEntity;
import com.infoplusvn.qrbankgateway.exception.ValidationHelper;
import com.infoplusvn.qrbankgateway.service.QRLookupBenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Slf4j
@Service
public class QRLookupBenServiceImpl implements QRLookupBenService {

    @Autowired
    QrIBTFServiceImpl qrIBTFService;

    @Autowired
    TransactionServiceImpl transactionService;

    private LookupBenReqInfoGW genMappingReqInfoGW(LookupBenReqNAPAS lookupBenReqNAPAS) {

        LookupBenReqInfoGW lookupBenReqInfoGW = new LookupBenReqInfoGW();
        HeaderInfoGW header = new HeaderInfoGW();
        LookupBenReqInfoGW.Data data = new LookupBenReqInfoGW.Data();
        LookupBenReqInfoGW.Participant participant = new LookupBenReqInfoGW.Participant();
        LookupBenReqInfoGW.Payment payment = new LookupBenReqInfoGW.Payment();
        LookupBenReqInfoGW.Order order = new LookupBenReqInfoGW.Order();

        header.setBkCd("970415");
        header.setBrCd("HN");
        header.setTrnDt(lookupBenReqNAPAS.getHeader().getTimestamp().toString());
        header.setDirection("I");
        header.setReqResGb("REQ");
        header.setRefNo(lookupBenReqNAPAS.getPayload().getPayment_reference());


        String qrString = lookupBenReqNAPAS.getPayload().getQr_string().trim();
//        if (!ValidationHelper.isValid(lookupBenReqNAPAS)) {
//            header.setErrCode(ErrorDefination.ERR_004.getErrCode());
//            header.setErrDesc(ErrorDefination.ERR_004.getDesc() + ": " + ValidationHelper.fieldNames.get());
//            header.setReqResGb("RES");
//        } else if (!qrIBTFService.checkCRC(qrString)) {
//            header.setErrCode(ErrorDefination.ERR_008.getErrCode());
//            header.setErrDesc(ErrorDefination.ERR_008.getDesc());
//            header.setReqResGb("RES");
//        } else {
        LinkedHashMap<String, String> linkedHashMapQRString = new LinkedHashMap<>();

        qrIBTFService.putHashMapAndCutQrString("", linkedHashMapQRString, qrString);

        String valueOfID38 = linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId());
        qrIBTFService.putHashMapAndCutQrString(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + ".", linkedHashMapQRString, valueOfID38);

        String valueOfID38_01 = linkedHashMapQRString.get(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.MEMBER_BANKS.getId());
        qrIBTFService.putHashMapAndCutQrString(QRCodeFormat.CONSUMER_ACCOUNT_INFO.getId() + "." + QRCodeFormat.MEMBER_BANKS.getId() + ".", linkedHashMapQRString, valueOfID38_01);

        String valueOfID62 = linkedHashMapQRString.get(QRCodeFormat.ADDITION_INFO.getId());
        if (valueOfID62 != null) {
            qrIBTFService.putHashMapAndCutQrString(QRCodeFormat.ADDITION_INFO.getId() + ".", linkedHashMapQRString, valueOfID62);
        }

        String valueOfID64 = linkedHashMapQRString.get("64");
        if (valueOfID64 != null) {
            qrIBTFService.putHashMapAndCutQrString("64" + ".", linkedHashMapQRString, valueOfID64);
        }

        //log.info("LinkedHashMap" + linkedHashMapQRString);


        //data.payment
        payment.setGenerationMethod(linkedHashMapQRString.get("01"));

        String tag55Value = linkedHashMapQRString.get("55");
        if (tag55Value != null) {
            payment.setIndicator(tag55Value);
        }

        String tag56Value = linkedHashMapQRString.get("56");
        if (tag56Value != null) {
            payment.setFeeFixed(tag56Value);
        }

        String tag57Value = linkedHashMapQRString.get("57");
        if (tag57Value != null) {
            payment.setFeePercentage(tag57Value);
        }

        String tag62_05Value = linkedHashMapQRString.get("62.05");
        if (tag62_05Value != null) {
            payment.setEndToEndReference(tag62_05Value);
            order.setReferenceLabel(tag62_05Value);
        }


        //data
        data.setAmount(linkedHashMapQRString.get("54"));

        data.setCurrency(linkedHashMapQRString.get("53"));

        //data.participant
        participant.setOriginatingInstitutionId(header.getBkCd());

        participant.setReceivingInstitutionId(linkedHashMapQRString.get("38.01.00"));

        participant.setMerchantId(linkedHashMapQRString.get("38.01.01"));

        participant.setMerchantCategoryCode(linkedHashMapQRString.get("52"));

        String tag59value = linkedHashMapQRString.get("59");
        if (tag59value != null) {
            participant.setCardAcceptorName(tag59value);
        }

        String tag60value = linkedHashMapQRString.get("60");
        if (tag60value != null) {
            participant.setCardAcceptorCity(tag60value);
        }

        String tag58value = linkedHashMapQRString.get("58");
        if (tag58value != null) {
            participant.setCardAcceptorCountry(tag58value);
        }

        String tag61value = linkedHashMapQRString.get("61");
        if (tag61value != null) {
            participant.setCardPostalCode(tag61value);
        }

        String tag64_00Value = linkedHashMapQRString.get("64.00");
        if (tag64_00Value != null) {
            participant.setCardLanguagePreference(tag64_00Value);
        }

        String tag64_01Value = linkedHashMapQRString.get("64.01");
        if (tag64_01Value != null) {
            participant.setCardNameAlternateLanguage(tag64_01Value);
        }

        String tag64_02Value = linkedHashMapQRString.get("64.02");
        if (tag64_02Value != null) {
            participant.setCardCityAlternateLanguage(tag64_02Value);
        }

        //data
        data.setRecipientAccount(linkedHashMapQRString.get("38.01.01"));

        //order
        String tag62_01Value = linkedHashMapQRString.get("62.01");
        if (tag62_01Value != null) {
            order.setBillNumber(tag62_01Value);
        }

        String tag62_02Value = linkedHashMapQRString.get("62.02");
        if (tag62_02Value != null) {
            order.setMobileNumber(tag62_02Value);
        }

        String tag62_03Value = linkedHashMapQRString.get("62.03");
        if (tag62_03Value != null) {
            order.setStoreLabel(tag62_03Value);
        }

        String tag62_04Value = linkedHashMapQRString.get("62.04");
        if (tag62_04Value != null) {
            order.setLoyaltyNumber(tag62_04Value);
        }


        String tag62_06Value = linkedHashMapQRString.get("62.06");
        if (tag62_06Value != null) {
            order.setCustomerLabel(tag62_06Value);
        }

        String tag62_07Value = linkedHashMapQRString.get("62.07");
        if (tag62_07Value != null) {
            order.setTerminalLabel(tag62_07Value);
        }

        String tag62_08Value = linkedHashMapQRString.get("62.08");
        if (tag62_08Value != null) {
            order.setPurposeOfTrans(tag62_08Value);
        }

        String tag62_09Value = linkedHashMapQRString.get("62.09");
        if (tag62_09Value != null) {
            order.setAdditionConsumerData(tag62_09Value);
        }


        data.setParticipant(participant);
        data.setPayment(payment);
        data.setOrder(order);
        lookupBenReqInfoGW.setData(data);


        //}
        lookupBenReqInfoGW.setHeader(header);


        return lookupBenReqInfoGW;
    }

    private LookupBenResNAPAS genMappingResNAPAS(LookupBenResInfoGW lookupBenResInfoGW) {

        LookupBenResNAPAS lookupBenResNAPAS = new LookupBenResNAPAS();
//        HeaderNAPAS header = new HeaderNAPAS();
//        HeaderNAPAS.Requestor requestor = new HeaderNAPAS.Requestor();
        LookupBenResNAPAS.Result result = new LookupBenResNAPAS.Result();

        LookupBenResNAPAS.Payload payload = new LookupBenResNAPAS.Payload();
        LookupBenResNAPAS.Payment payment = new LookupBenResNAPAS.Payment();
        LookupBenResNAPAS.Participant participant = new LookupBenResNAPAS.Participant();

        LookupBenResNAPAS.Recipient recipient = new LookupBenResNAPAS.Recipient();
        LookupBenResNAPAS.Address address = new LookupBenResNAPAS.Address();

        LookupBenResNAPAS.OrderInfo order_info = new LookupBenResNAPAS.OrderInfo();

        LookupBenResNAPAS.AdditionalInfo additional_info = new LookupBenResNAPAS.AdditionalInfo();


        //header
        //header.setRequestor(requestor);

        //result
        String responseCode = lookupBenResInfoGW.getData().getResponseCode();
        if (responseCode != null) {
            result.setCode(responseCode);
        }

        String responseDesc = lookupBenResInfoGW.getData().getResponseDesc();
        if (responseDesc != null) {
            result.setMessage(responseDesc);
        }

        //payment
        payment.setType("QR_PUSH");

        String generation_method = lookupBenResInfoGW.getData().getPayment().getGenerationMethod();
        if (generation_method != null) {
            payment.setGeneration_method(generation_method);
        }

        String exchange_rate = lookupBenResInfoGW.getData().getPayment().getExchangeRate();
        if (exchange_rate != null) {
            payment.setExchange_rate(exchange_rate);
        }

        String indicator = lookupBenResInfoGW.getData().getPayment().getIndicator();
        if (indicator != null) {
            payment.setIndicator(indicator);
        }

        String fee_fixed = lookupBenResInfoGW.getData().getPayment().getFeeFixed();
        if (fee_fixed != null) {
            payment.setFee_fixed(fee_fixed);
        }

        String fee_percentage = lookupBenResInfoGW.getData().getPayment().getFeePercentage();
        if (fee_percentage != null) {
            payment.setFee_percentage(fee_percentage);
        }

        payment.setPayment_reference(lookupBenResInfoGW.getHeader().getRefNo());

        String end_to_end_reference = lookupBenResInfoGW.getData().getOrder().getReferenceLabel();
        if (end_to_end_reference != null) {
            payment.setEnd_to_end_reference(end_to_end_reference);
        }

        payment.setTrace(lookupBenResInfoGW.getHeader().getRefNo());

        //participant
        participant.setOriginating_institution_id(lookupBenResInfoGW.getHeader().getBkCd());

        participant.setReceiving_institution_id(lookupBenResInfoGW.getData().getParticipant().getReceivingInstitutionId());

        participant.setMerchant_id(lookupBenResInfoGW.getData().getParticipant().getMerchantId());

        participant.setMerchant_category_code(lookupBenResInfoGW.getData().getParticipant().getMerchantCategoryCode());

        participant.setCard_acceptor_id(lookupBenResInfoGW.getData().getParticipant().getMerchantId());

        String card_acceptor_name = lookupBenResInfoGW.getData().getParticipant().getCardAcceptorName();
        if (card_acceptor_name != null) {
            participant.setCard_acceptor_name(card_acceptor_name);
        }

        String card_acceptor_city = lookupBenResInfoGW.getData().getParticipant().getCardAcceptorCity();
        if (card_acceptor_city != null) {
            participant.setCard_acceptor_city(card_acceptor_city);
        }

        String card_acceptor_country = lookupBenResInfoGW.getData().getParticipant().getCardAcceptorCountry();
        if (card_acceptor_country != null) {
            participant.setCard_acceptor_country(card_acceptor_country);
        }

        //address
        String line1 = lookupBenResInfoGW.getData().getRecipient().getAddress().getLine1();
        if (line1 != null) {
            address.setLine1(line1);
        }

        String line2 = lookupBenResInfoGW.getData().getRecipient().getAddress().getLine2();
        if (line2 != null) {
            address.setLine1(line2);
        }

        address.setCountry(lookupBenResInfoGW.getData().getRecipient().getAddress().getCountry());

        String phone = lookupBenResInfoGW.getData().getRecipient().getAddress().getPhone();
        if (phone != null) {
            address.setPhone(phone);
        }


        //recipient
        recipient.setFull_name(lookupBenResInfoGW.getData().getRecipient().getFullName());

        String date_of_birth = lookupBenResInfoGW.getData().getRecipient().getDob();
        if (date_of_birth != null) {
            recipient.setDate_of_birth(date_of_birth);
        }

        recipient.setAddress(address);


        //payload
        payload.setPayment(payment);

        payload.setAmount(lookupBenResInfoGW.getData().getAmount());

        payload.setCurrency(lookupBenResInfoGW.getData().getCurrency());

        payload.setParticipant(participant);

        payload.setRecipient_account(lookupBenResInfoGW.getData().getParticipant().getMerchantId());

        payload.setRecipient(recipient);


        //order_info
        order_info.setBill_number(lookupBenResInfoGW.getData().getOrder().getBillNumber());

        String mobile_number = lookupBenResInfoGW.getData().getOrder().getMobileNumber();
        if (mobile_number != null) {
            order_info.setMobile_number(mobile_number);
        }

        String store_label = lookupBenResInfoGW.getData().getOrder().getStoreLabel();
        if (store_label != null) {
            order_info.setStore_label(store_label);
        }

        String loyalty_number = lookupBenResInfoGW.getData().getOrder().getLoyaltyNumber();
        if (loyalty_number != null) {
            order_info.setLoyalty_number(loyalty_number);
        }

        String customer_label = lookupBenResInfoGW.getData().getOrder().getCustomerLabel();
        if (customer_label != null) {
            order_info.setCustomer_label(customer_label);
        }

        String terminal_label = lookupBenResInfoGW.getData().getOrder().getTerminalLabel();
        if (terminal_label != null) {
            order_info.setTerminal_label(terminal_label);
        }

        String transaction_purpose = lookupBenResInfoGW.getData().getOrder().getPurposeOfTrans();
        if (transaction_purpose != null) {
            order_info.setTransaction_purpose(transaction_purpose);
        }

        String additional_data_request = lookupBenResInfoGW.getData().getOrder().getAdditionConsumerData();
        if (additional_data_request != null) {
            order_info.setAdditional_data_request(additional_data_request);
        }

        //additional_info


        //lookupBenResNAPAS
//        lookupBenResNAPAS.setHeader(header);
        lookupBenResNAPAS.setResult(result);
        lookupBenResNAPAS.setPayload(payload);
        lookupBenResNAPAS.setOrder_info(order_info);
        lookupBenResNAPAS.setAdditional_info(additional_info);


        return lookupBenResNAPAS;
    }

    private void sentToNapas(LookupBenResNAPAS lookupBenResNAPAS, LookupBenResNAPAS.Result result, TransactionEntity transaction) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String lookupBenResNAPASJson = objectMapper.writeValueAsString(lookupBenResNAPAS);
        try {
            RestTemplate restTemplate = new RestTemplate();

            String apiUrl = LookupConstant.API_URL_SENT_TO_NAPAS;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo một đối tượng HttpEntity để đại diện cho toàn bộ yêu cầu POST
            HttpEntity<LookupBenResNAPAS> requestDTO = new HttpEntity<>(lookupBenResNAPAS, headers);

            // Gọi API sử dụng phương thức POST và truyền vào body là đối tượng requestEntity
            //InfoGW gửi bản tin chuẩn NAPAS sang NAPAS
            restTemplate.postForLocation(apiUrl, requestDTO);

            //nếu gửi sang NAPAS thành công
            transactionService.updateTransStep(transaction, PaymentConstant.STEP_SENT, PaymentConstant.STEP_STATUS_SUCCESS_CODE, PaymentConstant.STEP_STATUS_SUCCESS_DESC);
            transactionService.updateErrCodeDesc(transaction, lookupBenResNAPAS.getResult().getCode(), lookupBenResNAPAS.getResult().getMessage());
            transactionService.createActivity(transaction, lookupBenResNAPASJson, lookupBenResNAPAS.getResult().getCode(), lookupBenResNAPAS.getResult().getMessage(), LookupConstant.ACTIVITY_STEP_SEND_TO_NAPAS, PaymentConstant.STEP_STATUS_SUCCESS_CODE);

            transactionService.updateSentDt(transaction, LocalDateTime.now());


        } catch (Exception ex) {
            //nếu gửi sang issuerBank không thành công
            result.setCode(ErrorDefination.ERR_068.getErrCode());
            result.setMessage(ErrorDefination.ERR_068.getDesc());
            lookupBenResNAPAS.setResult(result);

            transactionService.updateTransStep(transaction, PaymentConstant.STEP_SENT, PaymentConstant.STEP_STATUS_ERROR_CODE, PaymentConstant.STEP_STATUS_ERROR_DESC);
            transactionService.updateErrCodeDesc(transaction, lookupBenResNAPAS.getResult().getCode(), lookupBenResNAPAS.getResult().getMessage());
            transactionService.updateSentDt(transaction, LocalDateTime.now());

            transactionService.createActivity(transaction, lookupBenResNAPASJson, lookupBenResNAPAS.getResult().getCode(), lookupBenResNAPAS.getResult().getMessage(), LookupConstant.ACTIVITY_STEP_SEND_TO_NAPAS, PaymentConstant.STEP_STATUS_ERROR_CODE);

            log.error("Lỗi: " + ex);
        }
    }

    @Override
    public LookupBenResNAPAS genLookupBenResNAPAS(LookupBenReqNAPAS lookupBenReqNAPAS) throws UnsupportedEncodingException {
        log.info("----------------LUỒNG ĐI LOOKUP BEN -----------------");
        log.info("STEP 1: NAPAS -> InfoGW: " + lookupBenReqNAPAS);

        LookupBenResNAPAS lookupBenResNAPAS = new LookupBenResNAPAS();

        lookupBenResNAPAS.setHeader(lookupBenReqNAPAS.getHeader());
        lookupBenResNAPAS.getHeader().setOperation("RES");

        LookupBenResNAPAS.Result result = new LookupBenResNAPAS.Result();

        //build ReqInfoGW
        //LookupBenReqInfoGW lookupBenReqInfoGWMapping = genMappingReqInfoGW(lookupBenReqNAPAS);
        String qrString = lookupBenReqNAPAS.getPayload().getQr_string().trim();
        if (!ValidationHelper.isValid(lookupBenReqNAPAS)) {
            result.setCode(ErrorDefination.ERR_004.getErrCode());
            result.setMessage(ErrorDefination.ERR_004.getDesc() + ": " + ValidationHelper.fieldNames.get());
            lookupBenResNAPAS.setResult(result);

            log.info("STEP 2: InfoGW -> NAPAS: " + lookupBenResNAPAS);

        } else if (!qrIBTFService.checkCRC(qrString)) {
            result.setCode(ErrorDefination.ERR_008.getErrCode());
            result.setMessage(ErrorDefination.ERR_008.getDesc());
            lookupBenResNAPAS.setResult(result);

            log.info("STEP 2: InfoGW -> NAPAS: " + lookupBenResNAPAS);
        } else {

            //mapping dữ liệu từ RequestNAPAS sang RequestInfoGW
            LookupBenReqInfoGW lookupBenReqInfoGWMapping = genMappingReqInfoGW(lookupBenReqNAPAS);
            log.info("STEP 2: InfoGW -> Bank: " + lookupBenReqInfoGWMapping);

            RestTemplate restTemplate = new RestTemplate();

            String apiUrl = "http://localhost:8029/benbank/qr/v1/ben/sentBenBank";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo một đối tượng HttpEntity để đại diện cho toàn bộ yêu cầu POST
            HttpEntity<LookupBenReqInfoGW> requestDTO = new HttpEntity<>(lookupBenReqInfoGWMapping, headers);

            try {

                // Gọi API sử dụng phương thức POST và truyền vào body là đối tượng requestEntity
                //InfoGW gửi bản tin chuẩn infoGW sang Ben Bank, sau đó nhận bản tin về theo chuẩn infoGW
                LookupBenResInfoGW lookupBenResInfoGW = restTemplate.postForObject(apiUrl, requestDTO, LookupBenResInfoGW.class);
                log.info("STEP 3: BANK -> InfoGW: " + lookupBenResInfoGW);

                if (lookupBenResInfoGW.getData() == null) {

                    log.error("Nhận được bản tin sai định dạng từ Ben Bank!");
                    return null;
                } else {
                    //mapping dữ liệu từ ResponseInfoGW sang ResponseNAPAS
                    lookupBenResNAPAS = genMappingResNAPAS(lookupBenResInfoGW);
                    lookupBenResNAPAS.setHeader(lookupBenReqNAPAS.getHeader());

                    log.info("STEP 4: InfoGW -> NAPAS: " + lookupBenResNAPAS);

                }

            } catch (Exception ex) {
                log.error(String.valueOf(ex));
                return null;
            }
        }


        return lookupBenResNAPAS;

    }


}
