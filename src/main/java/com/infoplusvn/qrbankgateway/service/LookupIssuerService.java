package com.infoplusvn.qrbankgateway.service;

import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderGW;
import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderNapas;
import com.infoplusvn.qrbankgateway.dto.request.LookupIssuer.GwRequest;
import com.infoplusvn.qrbankgateway.dto.request.LookupIssuer.NapasRequest;
import com.infoplusvn.qrbankgateway.dto.response.LookupIssuer.GwResponse;
import com.infoplusvn.qrbankgateway.dto.response.LookupIssuer.NapasResponse;
import com.infoplusvn.qrbankgateway.repo.BankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

@Service
public class LookupIssuerService {

    @Autowired
    BankRepo bankRepo;

    private int crc16(byte[] value) {
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)

        byte[] testBytes = value;

        //byte[] bytes = args[0].getBytes();

        for (byte b : testBytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        crc &= 0xffff;
        return crc;
    }

    private String genCRC(String qrString) throws UnsupportedEncodingException {

        return Integer.toHexString(crc16(qrString.getBytes("ASCII"))).toUpperCase();

    }

    private boolean checkCRC(String qrString) throws UnsupportedEncodingException {
        qrString = qrString.trim();
        if (genCRC(qrString.substring(0, qrString.length() - 4)).equals(qrString.substring(qrString.length() - 4))) {
            return true;
        }
        return false;
    }
    private void putHashMapAndCutQrString(String string, LinkedHashMap<String, String> linkedHashMap, String qrString) {
        while (!qrString.isEmpty()) {
            linkedHashMap.put(string + qrString.substring(0, 2), qrString.substring(4, 4 + Integer.parseInt(qrString.substring(2, 4))));
            qrString = qrString.replace(qrString.substring(0, 4) + qrString.substring(4, 4 + Integer.parseInt(qrString.substring(2, 4))), "");
        }
    }

    public NapasRequest convertRequest(GwRequest gwRequest){
        NapasRequest napasRequest = new NapasRequest();
        HeaderNapas headerNapas = new HeaderNapas();
        HeaderNapas.Requestor requestor = new HeaderNapas.Requestor();
        headerNapas.setRequestor(requestor);
        requestor.setId("ID01");
        requestor.setName("Nguyen Xuan Long");
        headerNapas.setReference_id("05050000011234567890ACB123456");
        headerNapas.setOperation(CommonConstant.REQ_GB);
        napasRequest.setHeaderNapas(headerNapas);

        NapasRequest.Payload payload = new NapasRequest.Payload();

        payload.setPayment_reference(CommonConstant.REFERENCE_NUMBER);
        payload.setQr_string(gwRequest.getData().getQrString());

        napasRequest.setPayload(payload);


        return napasRequest;
    }

    public NapasResponse convertResponse2(GwResponse gwResponse){
        NapasResponse napasResponse = new NapasResponse();
        HeaderNapas headerNapas = new HeaderNapas();
        napasResponse.setHeaderNapas(headerNapas);


        NapasResponse.Result result = new NapasResponse.Result();
        result.setCode(gwResponse.getData().getResponseCode());
        result.setMessage(gwResponse.getData().getResponseDesc());

//        NapasResponse.Payload payload = new NapasResponse.Payload();
//        napasResponse.setPayload(payload);

        NapasResponse.Payload.Payment payment = new NapasResponse.Payload.Payment();
        payment.setType("QR_PUSH");
        payment.setGeneration_method(gwResponse.getData().getPayment().getGenerationMethod());
        payment.setExchange_rate(gwResponse.getData().getPayment().getExchangeRate());
        payment.setIndicator(gwResponse.getData().getPayment().getIndicator());
        payment.setFee_fixed(gwResponse.getData().getPayment().getFeeFixed());
        payment.setFee_percentage(gwResponse.getData().getPayment().getFeePercentage());
        payment.setPayment_reference(gwResponse.getHeaderGW().getRefNo());
        payment.setEnd_to_end_reference(gwResponse.getData().getOrder().getReferenceLabel());
        payment.setTrace(gwResponse.getHeaderGW().getRefNo());

        NapasResponse.Payload payload = new NapasResponse.Payload();
        napasResponse.setPayload(payload);
        napasResponse.getPayload().setPayment(payment);
        napasResponse.getPayload().setAmount(gwResponse.getData().getAmount());
        napasResponse.getPayload().setCurrency(gwResponse.getData().getCurrency());
        napasResponse.getPayload().setRecipient_account(gwResponse.getData().getParticipant().getMerchantId());

        NapasResponse.Participant participant = new NapasResponse.Participant();
        participant.setOriginating_institution_id(gwResponse.getHeaderGW().getBkCd());
        participant.setReceiving_institution_id(gwResponse.getData().getParticipant().getReceivingInstitutionId());
        participant.setMerchant_id(gwResponse.getData().getParticipant().getMerchantId());
        participant.setMerchant_category_code(gwResponse.getData().getParticipant().getMerchantCategoryCode());
        participant.setCard_acceptor_id(gwResponse.getData().getParticipant().getCardAcceptorId());
        participant.setCard_acceptor_name(gwResponse.getData().getParticipant().getCardAcceptorName());
        participant.setCard_acceptor_city(gwResponse.getData().getParticipant().getCardAcceptorCity());
        participant.setCard_acceptor_country(gwResponse.getData().getParticipant().getCardAcceptorCountry());
        napasResponse.getPayload().setParticipant(participant);


        NapasResponse.Recipient recipient = new NapasResponse.Recipient();
        napasResponse.getPayload().setRecipient(recipient);
        NapasResponse.Recipient.Address address = new NapasResponse.Recipient.Address();

        napasResponse.getPayload().getRecipient().setAddress(address);

        recipient.setFull_name(gwResponse.getData().getRecipient().getFullName());
        recipient.setDate_of_birth(gwResponse.getData().getRecipient().getDob());
        recipient.getAddress().setLine1(gwResponse.getData().getRecipient().getAddress().getLine1());
        recipient.getAddress().setLine2(gwResponse.getData().getRecipient().getAddress().getLine2());
        recipient.getAddress().setCountry(gwResponse.getData().getRecipient().getAddress().getCountry());
        recipient.getAddress().setPhone(gwResponse.getData().getRecipient().getAddress().getPhone());



        NapasResponse.Order_info order_info = new NapasResponse.Order_info();
        order_info.setMobile_number(gwResponse.getData().getOrder().getMobileNumber());
        order_info.setStore_label(gwResponse.getData().getOrder().getStoreLable());
        order_info.setLoyalty_number(gwResponse.getData().getOrder().getLoyaltyNumber());
        order_info.setCustomer_label(gwResponse.getData().getOrder().getCustomerLabel());
        order_info.setTerminal_label(gwResponse.getData().getOrder().getTerminalLabel());
        order_info.setTransaction_purpose(gwResponse.getData().getOrder().getPurposeOfTrans());
        order_info.setAdditional_data_request(gwResponse.getData().getOrder().getAdditionCosumerData());

        napasResponse.setOrder_info(order_info);
        return napasResponse;
    }

    public GwResponse convertResponse(NapasResponse napasResponse){
        GwResponse gwResponse = new GwResponse();
        HeaderGW headerGW = new HeaderGW();
        gwResponse.setHeaderGW(headerGW);
        gwResponse.getHeaderGW().setReqResGb(CommonConstant.RES_GB);

        GwResponse.Data data = new GwResponse.Data();
        gwResponse.setData(data);

        GwResponse.Data.Participant participant = new GwResponse.Data.Participant();
        GwResponse.Data.Recipient recipient = new GwResponse.Data.Recipient();
        GwResponse.Data.Order order = new GwResponse.Data.Order();
        GwResponse.Payment payment = new GwResponse.Payment();
        GwResponse.Data.Recipient.Address address = new GwResponse.Data.Recipient.Address();

        gwResponse.getData().setPayment(payment);
        headerGW.setRefNo(CommonConstant.REFERENCE_NUMBER);
        headerGW.setBrCd(CommonConstant.BRAND_CODE);
        headerGW.setTrnDt(CommonConstant.TRANSACTION_DATE);
        headerGW.setDirection(CommonConstant.DIRECTION_INBOUND);
        headerGW.setBkCd(napasResponse.getPayload().getParticipant().getOriginating_institution_id());

        data.setRecipientAccount(CommonConstant.CARD_ACCEPTOR_CITY);
        data.setResponseCode(napasResponse.getResult().getCode());
        data.setResponseDesc(napasResponse.getResult().getDescription());

        data.getPayment().setGenerationMethod(napasResponse.getPayload().getPayment().getGeneration_method());
        data.getPayment().setIndicator(napasResponse.getPayload().getPayment().getIndicator());
        data.getPayment().setExchangeRate(napasResponse.getPayload().getPayment().getExchange_rate());
        data.getPayment().setFeeFixed(napasResponse.getPayload().getPayment().getFee_fixed());
        data.getPayment().setFeePercentage(napasResponse.getPayload().getPayment().getFee_percentage());


        data.setAmount(napasResponse.getPayload().getAmount());

        data.setCurrency(napasResponse.getPayload().getCurrency());

        gwResponse.getData().setParticipant(participant);
        data.getParticipant().setReceivingInstitutionId(napasResponse.getPayload().getParticipant().getReceiving_institution_id());
        data.getParticipant().setMerchantId(napasResponse.getPayload().getParticipant().getMerchant_id());
        data.getParticipant().setMerchantCategoryCode(napasResponse.getPayload().getParticipant().getMerchant_category_code());
        data.getParticipant().setCardAcceptorId(napasResponse.getPayload().getParticipant().getCard_acceptor_id());
        data.getParticipant().setCardAcceptorName(napasResponse.getPayload().getParticipant().getCard_acceptor_name());
        data.getParticipant().setCardAcceptorCity(CommonConstant.CARD_ACCEPTOR_CITY);
        data.getParticipant().setCardAcceptorCountry(napasResponse.getPayload().getParticipant().getCard_acceptor_country());


        gwResponse.getData().setRecipient(recipient);
        gwResponse.getData().getRecipient().setAddress(address);
        data.getRecipient().setFullName(napasResponse.getPayload().getRecipient().getFull_name());
        data.getRecipient().setDob(napasResponse.getPayload().getRecipient().getDate_of_birth());
        data.getRecipient().getAddress().setLine1(napasResponse.getPayload().getRecipient().getAddress().getLine1());
        data.getRecipient().getAddress().setLine2(napasResponse.getPayload().getRecipient().getAddress().getLine2());
        data.getRecipient().getAddress().setCountry(napasResponse.getPayload().getRecipient().getAddress().getCountry());
        data.getRecipient().getAddress().setPhone(napasResponse.getPayload().getRecipient().getAddress().getPhone());

        gwResponse.getData().setOrder(order);
        data.getOrder().setReferenceLabel(napasResponse.getPayload().getPayment().getEnd_to_end_reference());
        data.getOrder().setBillNumber(napasResponse.getOrder_info().getBill_number());
        data.getOrder().setMobileNumber(napasResponse.getOrder_info().getMobile_number());
        data.getOrder().setStoreLable(napasResponse.getOrder_info().getStore_label());
        data.getOrder().setLoyaltyNumber(napasResponse.getOrder_info().getLoyalty_number());
        data.getOrder().setCustomerLabel(napasResponse.getOrder_info().getCustomer_label());
        data.getOrder().setTerminalLabel(napasResponse.getOrder_info().getTerminal_label());
        data.getOrder().setPurposeOfTrans(napasResponse.getOrder_info().getTransaction_purpose());




        return gwResponse;
    }
    public GwResponse genLookupResNAPAS(GwRequest gwRequest) throws UnsupportedEncodingException{


//        log.info("----------------LUỒNG ĐI LOOKUP ISSUER -----------------");
        System.out.println("STEP 1: InfoGW -> NAPAS: " + gwRequest);


        // Chuyển đổi gwRequest thành napasRequest
        NapasRequest napasRequest = convertRequest(gwRequest);

        // Gửi napasRequest đến NAPAS
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/infogw/qr/v1/issuer/lookup";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NapasRequest> requestEntity = new HttpEntity<>(napasRequest, headers);
        NapasResponse napasResponse = restTemplate.postForObject(url, requestEntity, NapasResponse.class);
        // ...

        // Xử lý kết quả trả về từ NAPAS thành napasResponse
        GwResponse gwResponse = convertResponse(napasResponse);
        // ...


        return gwResponse;
    }
}
