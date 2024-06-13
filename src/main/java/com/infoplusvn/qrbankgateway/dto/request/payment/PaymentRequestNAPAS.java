package com.infoplusvn.qrbankgateway.dto.request.payment;

import com.infoplusvn.qrbankgateway.dto.common.HeaderNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseNAPAS;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PaymentRequestNAPAS {

    @Valid
    private HeaderNAPAS header;

    @Valid
    private Payload payload;



    @lombok.Data
    public static class Payload {

        @Valid
        private Payment payment;

        @NotBlank
        private String amount;

        @NotBlank
        private String currency;

        @NotBlank
        private String sender_account;

        @Valid
        private Sender sender;

        @Valid
        private Participant participant;

        @NotBlank
        private String recipient_account;

        @Valid
        private Recipient recipient;

        private String additional_message;

        @Valid
        private OrderInfo order_info;

        @Valid
        private AdditionalInfo additional_info;



    }

    @lombok.Data
    public static class Sender{

        private String first_name;

        private String middle_name;

        private String last_name;

        @NotBlank
        private String full_name;

        private String date_of_birth;

        @Valid
        private Address address;
    }

    @lombok.Data
    public static class Payment {

        private String funding_reference;

        @NotBlank
        private String type;

        private String generation_method;

        private String channel;

        private String device_id;

        private String location;

        private String transaction_local_date_time;

        private String interbank_amount;

        private String interbank_currency;

        private String exchange_rate;

        private String indicator;

        private String fee_fixed;

        private String fee_percentage;

        @NotBlank
        private String payment_reference;

        private String end_to_end_reference;

        private String trace;


    }

    @lombok.Data
    public static class Participant {

        //@NotBlank
        private String originating_institution_id;

        @NotBlank
        private String receiving_institution_id;

        //@NotBlank
        private String merchant_id;

        //@NotNull
        private String merchant_category_code;

        //@NotNull
        private String card_acceptor_id;

        private String card_acceptor_name;

        private String card_acceptor_city;

        private String card_acceptor_country;

        private String card_postal_code;

        private String card_language_preference;

        private String card_name_alternate_language;

        private String card_city_alternate_language;

        private String card_payment_system_specific;


    }

    @lombok.Data
    public static class Recipient {

        private String first_name;

        private String middle_name;

        private String last_name;

        @NotBlank
        private String full_name;

        private String date_of_birth;

        @Valid
        private Address address;


    }

    @lombok.Data
    public static class Address{

        private String line1;

        private String line2;

        private String city;

        private String country_subdivision;

        private String postal_code;

        @NotNull
        private String country;

        private String phone;

        private String email;

    }

    @lombok.Data
    public static class OrderInfo{

        @NotBlank
        private String bill_number;

        private String mobile_number;

        private String store_label;

        private String loyalty_number;

        private String customer_label;

        private String terminal_label;

        private String transaction_purpose;

        private String additional_data_request;

    }

    @lombok.Data
    public static class AdditionalInfo {

        private List<Instruction> instruction;


    }

    @lombok.Data
    public static class Instruction {

        private String key;

        private String value;

    }
}
