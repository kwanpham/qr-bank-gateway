package com.infoplusvn.qrbankgateway.service.impl;

import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.dto.request.qr_void.VoidRequestGW;
import com.infoplusvn.qrbankgateway.dto.request.qr_void.VoidRequestNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.qr_void.VoidResponseGW;
import com.infoplusvn.qrbankgateway.service.QRVoidService;
import org.springframework.stereotype.Service;

@Service
public class QRVoidServiceImpl implements QRVoidService {

//    private VoidRequestNAPAS genMappingReqNAPAS(VoidRequestGW request) {
//
//        VoidRequestNAPAS voidRequestNAPAS = new VoidRequestNAPAS();
//        VoidRequestNAPAS.Original original = new VoidRequestNAPAS.Original();
//        VoidRequestNAPAS.Payload payload = new VoidRequestNAPAS.Payload();
//        VoidRequestNAPAS.Payment payment = new VoidRequestNAPAS.Payment();
//        VoidRequestNAPAS.Sender sender = new VoidRequestNAPAS.Sender();
//        VoidRequestNAPAS.Participant participant = new VoidRequestNAPAS.Participant();
//        VoidRequestNAPAS.Recipient recipient = new VoidRequestNAPAS.Recipient();
//        VoidRequestNAPAS.Address senderAddress = new VoidRequestNAPAS.Address();
//        VoidRequestNAPAS.Address recipientAddress = new VoidRequestNAPAS.Address();
//        VoidRequestNAPAS.OrderInfo orderInfo = new VoidRequestNAPAS.OrderInfo();
//
//        //payment
//        payment.setType("QR_PUSH");
//        payment.setGeneration_method(CommonConstant.METHOD_DYNAMIC);
//        payment.setChannel(request.getData().getChannel());
//        payment.setPayment_reference(request.getHeader().getRefNo());
//        payment.setTrace(request.getHeader().getRefNo());
//
//        //senderAddress
//        senderAddress.setLine1(request.getData().getSender().getAddress().getLine1());
//        senderAddress.setLine2(request.getData().getSender().getAddress().getLine2());
//        senderAddress.setCountry(request.getData().getSender().getCountry());
//        senderAddress.setPhone(request.getData().getSender().getPhone());
//
//        //sender
//        sender.setFull_name(request.getData().getSender().getFullName());
//        sender.setAddress(senderAddress);
//
//        //participant
//        participant.setReceiving_institution_id(request.getData().getParticipant().getReceivingInstitutionId());
//
//        //recipientAddress
//        recipientAddress.setLine1(request.getData().getRecipient().getAddress().getLine1());
//        recipientAddress.setLine2(request.getData().getRecipient().getAddress().getLine2());
//
//        //recipient
//        recipient.setFull_name(request.getData().getRecipient().getFullName());
//        recipient.setAddress(recipientAddress);
//
//        //orderInfo
//        orderInfo.setBill_number(request.getData().getOrder().getBillNumber());
//
//        //payload
//        payload.setPayment(payment);
//        payload.setSender(sender);
//        payload.setParticipant(participant);
//        payload.setRecipient_account(request.getData().getRecipientAccount());
//        payload.setRecipient(recipient);
//        payload.setAdditional_message(request.getData().getAdditionMessage());
//        payload.setOrder_info(orderInfo);
//
//        //original
//        original.setPayment(payment);
//        original.setAmount(request.getData().getAmount());
//        original.setCurrency(request.getData().getCurrency());
//        original.setSender_account(request.getData().getSenderAccount());
//
//
//        voidRequestNAPAS.setPayload(payload);
//
//        return voidRequestNAPAS;
//    }
    
    @Override
    public VoidResponseGW genVoidResGW(VoidRequestGW voidRequestGW) {
        return null;
    }
}
