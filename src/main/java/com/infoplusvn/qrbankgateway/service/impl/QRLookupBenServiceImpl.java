package com.infoplusvn.qrbankgateway.service.impl;

import com.beust.ah.A;
import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.constant.ErrorDefination;
import com.infoplusvn.qrbankgateway.constant.QRCodeFormat;
import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import com.infoplusvn.qrbankgateway.dto.common.HeaderNAPAS;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqInfoGW;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResInfoGW;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResNAPAS;
import com.infoplusvn.qrbankgateway.entity.PaymentDetailsEntity;
import com.infoplusvn.qrbankgateway.entity.PaymentEntity;
import com.infoplusvn.qrbankgateway.exception.ValidationHelper;
import com.infoplusvn.qrbankgateway.repo.PaymentDetailsRepo;
import com.infoplusvn.qrbankgateway.repo.PaymentRepo;
import com.infoplusvn.qrbankgateway.service.QRLookupBenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Slf4j
@Service
public class QRLookupBenServiceImpl implements QRLookupBenService {

    @Autowired
    QrIBTFServiceImpl qrIBTFService;

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    PaymentDetailsRepo paymentDetailsRepo;


    private void createPayment(LookupBenResInfoGW lookupBenResInfoGW,String paymentType){

        PaymentEntity paymentEntity = new PaymentEntity();

        paymentEntity.setRefNo(lookupBenResInfoGW.getHeader().getRefNo());
        paymentEntity.setDirection(lookupBenResInfoGW.getHeader().getDirection());
        paymentEntity.setPaymentType(paymentType);
        paymentEntity.setOriginatingInstitutionId(lookupBenResInfoGW.getHeader().getBkCd());
        paymentEntity.setReceivingInstitutionId(lookupBenResInfoGW.getData().getParticipant().getReceivingInstitutionId());
        paymentEntity.setCardAcceptorName(lookupBenResInfoGW.getData().getParticipant().getCardAcceptorName());
        paymentEntity.setMerchantCategoryCode(lookupBenResInfoGW.getData().getParticipant().getMerchantCategoryCode());
        paymentEntity.setCardAcceptorCountry(lookupBenResInfoGW.getData().getParticipant().getCardAcceptorCountry());
        paymentEntity.setMerchantId(lookupBenResInfoGW.getData().getParticipant().getMerchantId());
        paymentEntity.setAmount(lookupBenResInfoGW.getData().getAmount());
        paymentEntity.setCurrency(lookupBenResInfoGW.getData().getCurrency());
        paymentEntity.setBillNumber(lookupBenResInfoGW.getData().getOrder().getBillNumber());
        paymentEntity.setCreatedOn(LocalDateTime.now());
        paymentEntity.setResponseCode(lookupBenResInfoGW.getData().getResponseCode());
        paymentEntity.setResponseDesc(lookupBenResInfoGW.getData().getResponseDesc());

        paymentRepo.save(paymentEntity);

    }

    private void createPaymentDetail(String refNo,String start,String end,LookupBenReqNAPAS lookupBenReqNAPAS, LookupBenReqInfoGW lookupBenReqInfoGW, LookupBenResInfoGW lookupBenResInfoGW, LookupBenResNAPAS lookupBenResNAPAS){

        PaymentDetailsEntity paymentDetail = new PaymentDetailsEntity();

        paymentDetail.setRefNo(refNo);
        paymentDetail.setStart(start);
        paymentDetail.setEnd(end);


        if(lookupBenReqNAPAS != null){
            paymentDetail.setRequestorId(lookupBenReqNAPAS.getHeader().getRequestor().getId());
            paymentDetail.setRequestorName(lookupBenReqNAPAS.getHeader().getRequestor().getName());

            paymentDetail.setReqResGb(lookupBenReqNAPAS.getHeader().getOperation());
            paymentDetail.setSignature(lookupBenReqNAPAS.getHeader().getSignature());

            paymentDetail.setQrString(lookupBenReqNAPAS.getPayload().getQr_string());
        }

        if(lookupBenReqInfoGW != null) {
            //header
            paymentDetail.setBkCd(lookupBenReqInfoGW.getHeader().getBkCd());
            paymentDetail.setBrCd(lookupBenReqInfoGW.getHeader().getBrCd());
            paymentDetail.setTrnDt(lookupBenReqInfoGW.getHeader().getTrnDt());
            paymentDetail.setReqResGb(lookupBenReqInfoGW.getHeader().getReqResGb());
            paymentDetail.setErrCode(lookupBenReqInfoGW.getHeader().getErrCode());
            paymentDetail.setErrDesc(lookupBenReqInfoGW.getHeader().getErrDesc());

            //data
            paymentDetail.setChannel(lookupBenReqInfoGW.getData().getChannel());

            //payment
            paymentDetail.setPaymentGenerationMethod(lookupBenReqInfoGW.getData().getPayment().getGenerationMethod());
            paymentDetail.setPaymentIndicator(lookupBenReqInfoGW.getData().getPayment().getIndicator());
            paymentDetail.setPaymentFeeFixed(lookupBenReqInfoGW.getData().getPayment().getFeeFixed());
            paymentDetail.setPaymentFeePercentage(lookupBenReqInfoGW.getData().getPayment().getFeePercentage());
            paymentDetail.setPaymentEndToEndReference(lookupBenReqInfoGW.getData().getPayment().getEndToEndReference());

            //data
            paymentDetail.setAmount(lookupBenReqInfoGW.getData().getAmount());
            paymentDetail.setCurrency(lookupBenReqInfoGW.getData().getCurrency());

            //participant
            paymentDetail.setParticipantOriginatingInstitutionId(lookupBenReqInfoGW.getData().getParticipant().getOriginatingInstitutionId());
            paymentDetail.setParticipantReceivingInstitutionId(lookupBenReqInfoGW.getData().getParticipant().getReceivingInstitutionId());
            paymentDetail.setParticipantMerchantId(lookupBenReqInfoGW.getData().getParticipant().getMerchantId());
            paymentDetail.setParticipantMerchantCategoryCode(lookupBenReqInfoGW.getData().getParticipant().getMerchantCategoryCode());
            paymentDetail.setParticipantCardAcceptorName(lookupBenReqInfoGW.getData().getParticipant().getCardAcceptorName());
            paymentDetail.setParticipantCardAcceptorCity(lookupBenReqInfoGW.getData().getParticipant().getCardAcceptorCity());
            paymentDetail.setParticipantCardAcceptorCountry(lookupBenReqInfoGW.getData().getParticipant().getCardAcceptorCountry());
            paymentDetail.setParticipantCardPostalCode(lookupBenReqInfoGW.getData().getParticipant().getCardPostalCode());
            paymentDetail.setParticipantCardLanguagePreference(lookupBenReqInfoGW.getData().getParticipant().getCardLanguagePreference());
            paymentDetail.setParticipantCardNameAlternateLanguage(lookupBenReqInfoGW.getData().getParticipant().getCardNameAlternateLanguage());
            paymentDetail.setParticipantCityAlternateLanguage(lookupBenReqInfoGW.getData().getParticipant().getCardCityAlternateLanguage());
            paymentDetail.setParticipantCardPaymentSystemSpecific(lookupBenReqInfoGW.getData().getParticipant().getCardPaymentSystemSpecific());

            //data
            paymentDetail.setRecipientAccount(lookupBenReqInfoGW.getData().getRecipientAccount());

            //order
            paymentDetail.setOrderBillNumber(lookupBenReqInfoGW.getData().getOrder().getBillNumber());
            paymentDetail.setOrderMobileNumber(lookupBenReqInfoGW.getData().getOrder().getMobileNumber());
            paymentDetail.setOrderStoreLabel(lookupBenReqInfoGW.getData().getOrder().getStoreLabel());
            paymentDetail.setOrderLoyaltyNumber(lookupBenReqInfoGW.getData().getOrder().getLoyaltyNumber());
            paymentDetail.setOrderReferenceLabel(lookupBenReqInfoGW.getData().getOrder().getReferenceLabel());
            paymentDetail.setOrderCustomerLabel(lookupBenReqInfoGW.getData().getOrder().getCustomerLabel());
            paymentDetail.setOrderTerminalLabel(lookupBenReqInfoGW.getData().getOrder().getTerminalLabel());
            paymentDetail.setOrderPurposeOfTrans(lookupBenReqInfoGW.getData().getOrder().getPurposeOfTrans());
            paymentDetail.setOrderAdditionConsumerData(lookupBenReqInfoGW.getData().getOrder().getAdditionConsumerData());
        }

        if(lookupBenResInfoGW != null) {
            paymentDetail.setBkCd(lookupBenResInfoGW.getHeader().getBkCd());
            paymentDetail.setBrCd(lookupBenResInfoGW.getHeader().getBrCd());
            paymentDetail.setTrnDt(lookupBenResInfoGW.getHeader().getTrnDt());
            paymentDetail.setReqResGb(lookupBenResInfoGW.getHeader().getReqResGb());
            paymentDetail.setErrCode(lookupBenResInfoGW.getHeader().getErrCode());
            paymentDetail.setErrDesc(lookupBenResInfoGW.getHeader().getErrDesc());

            //data
            paymentDetail.setResponseCode(lookupBenResInfoGW.getData().getResponseCode());
            paymentDetail.setResponseDesc(lookupBenResInfoGW.getData().getResponseDesc());
            paymentDetail.setFundingReference(lookupBenResInfoGW.getData().getFundingReference());

            //payment
            paymentDetail.setPaymentGenerationMethod(lookupBenResInfoGW.getData().getPayment().getGenerationMethod());
            paymentDetail.setPaymentIndicator(lookupBenResInfoGW.getData().getPayment().getIndicator());
            paymentDetail.setPaymentTrace(lookupBenResInfoGW.getData().getPayment().getTrace());
            paymentDetail.setPaymentExchangeRate(lookupBenResInfoGW.getData().getPayment().getExchangeRate());
            paymentDetail.setPaymentFeeFixed(lookupBenResInfoGW.getData().getPayment().getFeeFixed());
            paymentDetail.setPaymentFeePercentage(lookupBenResInfoGW.getData().getPayment().getFeePercentage());

            //data
            paymentDetail.setAmount(lookupBenResInfoGW.getData().getAmount());
            paymentDetail.setCurrency(lookupBenResInfoGW.getData().getCurrency());

            //participant
            paymentDetail.setParticipantMerchantId(lookupBenResInfoGW.getData().getParticipant().getMerchantId());
            paymentDetail.setParticipantReceivingInstitutionId(lookupBenResInfoGW.getData().getParticipant().getReceivingInstitutionId());
            paymentDetail.setParticipantMerchantCategoryCode(lookupBenResInfoGW.getData().getParticipant().getMerchantCategoryCode());
            paymentDetail.setParticipantCardAcceptorId(lookupBenResInfoGW.getData().getParticipant().getCardAcceptorId());
            paymentDetail.setParticipantCardAcceptorCountry(lookupBenResInfoGW.getData().getParticipant().getCardAcceptorCountry());
            paymentDetail.setParticipantCardAcceptorName(lookupBenResInfoGW.getData().getParticipant().getCardAcceptorName());
            paymentDetail.setParticipantCardAcceptorCity(lookupBenResInfoGW.getData().getParticipant().getCardAcceptorCity());

            //data
            paymentDetail.setRecipientAccount(lookupBenResInfoGW.getData().getRecipientAccount());

            //recipient
            paymentDetail.setRecipientFullName(lookupBenResInfoGW.getData().getRecipient().getFullName());
            paymentDetail.setRecipientDob(lookupBenResInfoGW.getData().getRecipient().getDob());

            //address
            paymentDetail.setAddressLine1(lookupBenResInfoGW.getData().getRecipient().getAddress().getLine1());
            paymentDetail.setAddressLine2(lookupBenResInfoGW.getData().getRecipient().getAddress().getLine2());
            paymentDetail.setAddressCountry(lookupBenResInfoGW.getData().getRecipient().getAddress().getCountry());
            paymentDetail.setAddressPhone(lookupBenResInfoGW.getData().getRecipient().getAddress().getPhone());

            //order
            paymentDetail.setOrderBillNumber(lookupBenResInfoGW.getData().getOrder().getBillNumber());
            paymentDetail.setOrderMobileNumber(lookupBenResInfoGW.getData().getOrder().getMobileNumber());
            paymentDetail.setOrderStoreLabel(lookupBenResInfoGW.getData().getOrder().getStoreLabel());
            paymentDetail.setOrderLoyaltyNumber(lookupBenResInfoGW.getData().getOrder().getLoyaltyNumber());
            paymentDetail.setOrderReferenceLabel(lookupBenResInfoGW.getData().getOrder().getReferenceLabel());
            paymentDetail.setOrderCustomerLabel(lookupBenResInfoGW.getData().getOrder().getCustomerLabel());
            paymentDetail.setOrderTerminalLabel(lookupBenResInfoGW.getData().getOrder().getTerminalLabel());
            paymentDetail.setOrderPurposeOfTrans(lookupBenResInfoGW.getData().getOrder().getPurposeOfTrans());
            paymentDetail.setOrderAdditionConsumerData(lookupBenResInfoGW.getData().getOrder().getAdditionConsumerData());

        }
        if(lookupBenResNAPAS != null){
            //header
            paymentDetail.setRequestorId(lookupBenResNAPAS.getHeader().getRequestor().getId());
            paymentDetail.setRequestorName(lookupBenResNAPAS.getHeader().getRequestor().getName());

            paymentDetail.setReqResGb(lookupBenResNAPAS.getHeader().getOperation());
            paymentDetail.setSignature(lookupBenResNAPAS.getHeader().getSignature());

            //result
            paymentDetail.setResponseCode(lookupBenResNAPAS.getResult().getCode());
            paymentDetail.setResponseDesc(lookupBenResNAPAS.getResult().getMessage());

            //payment
            paymentDetail.setPaymentGenerationMethod(lookupBenResNAPAS.getPayload().getPayment().getGeneration_method());
            paymentDetail.setPaymentExchangeRate(lookupBenResNAPAS.getPayload().getPayment().getExchange_rate());
            paymentDetail.setPaymentIndicator(lookupBenResNAPAS.getPayload().getPayment().getIndicator());
            paymentDetail.setPaymentFeeFixed(lookupBenResNAPAS.getPayload().getPayment().getFee_fixed());
            paymentDetail.setPaymentFeePercentage(lookupBenResNAPAS.getPayload().getPayment().getFee_percentage());
            paymentDetail.setPaymentEndToEndReference(lookupBenResNAPAS.getPayload().getPayment().getEnd_to_end_reference());

            //payload
            paymentDetail.setAmount(lookupBenResNAPAS.getPayload().getAmount());
            paymentDetail.setCurrency(lookupBenResNAPAS.getPayload().getCurrency());

            //participant
            paymentDetail.setParticipantOriginatingInstitutionId(lookupBenResNAPAS.getPayload().getParticipant().getOriginating_institution_id());
            paymentDetail.setParticipantReceivingInstitutionId(lookupBenResNAPAS.getPayload().getParticipant().getReceiving_institution_id());
            paymentDetail.setParticipantMerchantId(lookupBenResNAPAS.getPayload().getParticipant().getMerchant_id());
            paymentDetail.setParticipantMerchantCategoryCode(lookupBenResNAPAS.getPayload().getParticipant().getMerchant_category_code());
            paymentDetail.setParticipantCardAcceptorId(lookupBenResNAPAS.getPayload().getParticipant().getCard_acceptor_id());
            paymentDetail.setParticipantCardAcceptorName(lookupBenResNAPAS.getPayload().getParticipant().getCard_acceptor_name());
            paymentDetail.setParticipantCardAcceptorCity(lookupBenResNAPAS.getPayload().getParticipant().getCard_acceptor_city());
            paymentDetail.setParticipantCardAcceptorCountry(lookupBenResNAPAS.getPayload().getParticipant().getCard_acceptor_country());
            paymentDetail.setParticipantCardPostalCode(lookupBenResNAPAS.getPayload().getParticipant().getCard_postal_code());
            paymentDetail.setParticipantCardLanguagePreference(lookupBenResNAPAS.getPayload().getParticipant().getCard_language_preference());
            paymentDetail.setParticipantCardNameAlternateLanguage(lookupBenResNAPAS.getPayload().getParticipant().getCard_name_alternate_language());
            paymentDetail.setParticipantCityAlternateLanguage(lookupBenResNAPAS.getPayload().getParticipant().getCity_alternate_language());
            paymentDetail.setParticipantCardPaymentSystemSpecific(lookupBenResNAPAS.getPayload().getParticipant().getCard_payment_system_specific());

            //payload
            paymentDetail.setRecipientAccount(lookupBenResNAPAS.getPayload().getRecipient_account());

            //recipient
            paymentDetail.setRecipientFullName(lookupBenResNAPAS.getPayload().getRecipient().getFull_name());
            paymentDetail.setRecipientDob(lookupBenResNAPAS.getPayload().getRecipient().getDate_of_birth());

            //address
            paymentDetail.setAddressLine1(lookupBenResNAPAS.getPayload().getRecipient().getAddress().getLine1());
            paymentDetail.setAddressLine2(lookupBenResNAPAS.getPayload().getRecipient().getAddress().getLine2());
            paymentDetail.setAddressCity(lookupBenResNAPAS.getPayload().getRecipient().getAddress().getCity());
            paymentDetail.setAddressCountrySubdivision(lookupBenResNAPAS.getPayload().getRecipient().getAddress().getCountry_subdivision());
            paymentDetail.setAddressPostalCode(lookupBenResNAPAS.getPayload().getRecipient().getAddress().getPostal_code());
            paymentDetail.setAddressCountry(lookupBenResNAPAS.getPayload().getRecipient().getAddress().getCountry());
            paymentDetail.setAddressPhone(lookupBenResNAPAS.getPayload().getRecipient().getAddress().getPhone());
            paymentDetail.setAddressEmail(lookupBenResNAPAS.getPayload().getRecipient().getAddress().getEmail());

            //additionalMessage

            //orderInfo
            paymentDetail.setOrderBillNumber(lookupBenResNAPAS.getOrder_info().getBill_number());
            paymentDetail.setOrderMobileNumber(lookupBenResNAPAS.getOrder_info().getMobile_number());
            paymentDetail.setOrderStoreLabel(lookupBenResNAPAS.getOrder_info().getStore_label());
            paymentDetail.setOrderLoyaltyNumber(lookupBenResNAPAS.getOrder_info().getLoyalty_number());
            paymentDetail.setOrderCustomerLabel(lookupBenResNAPAS.getOrder_info().getCustomer_label());
            paymentDetail.setOrderTerminalLabel(lookupBenResNAPAS.getOrder_info().getTerminal_label());
            paymentDetail.setOrderPurposeOfTrans(lookupBenResNAPAS.getOrder_info().getTransaction_purpose());
            paymentDetail.setOrderAdditionConsumerData(lookupBenResNAPAS.getOrder_info().getAdditional_data_request());

        }


        paymentDetailsRepo.save(paymentDetail);
    }


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

            String apiUrl = "http://localhost:8029/benbank/qr/v1/ben/lookup";

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

                    //luu vao csdl
//                    createLookupBenEntity(lookupBenResInfoGW);
//
//                    createReqNAPAS(lookupBenReqNAPAS);
//
//                    createReqGW(lookupBenReqInfoGWMapping);
//
//                    createResGW(lookupBenResInfoGW);
//
//                    createResNAPAS(lookupBenResNAPAS);

//                    String refNo = lookupBenResInfoGW.getHeader().getRefNo();

//                    createPayment(lookupBenResInfoGW,"Lookup");

//                    createPaymentDetail(refNo, "NAPAS","InfoGW",lookupBenReqNAPAS, null,null,null);
//
//                    createPaymentDetail(refNo, "InfoGW","Bank",null, lookupBenReqInfoGWMapping,null,null);
//
//                    createPaymentDetail(refNo, "Bank","InfoGW",null, null,lookupBenResInfoGW,null);
//
//                    createPaymentDetail(refNo, "InfoGW","NAPAS",null, null,null,lookupBenResNAPAS);
                }

            } catch (Exception ex) {
                log.error(String.valueOf(ex));
                return null;
            }
        }



        return lookupBenResNAPAS;

    }


}
