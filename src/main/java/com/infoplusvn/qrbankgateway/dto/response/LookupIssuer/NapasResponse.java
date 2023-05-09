package com.infoplusvn.qrbankgateway.dto.response.LookupIssuer;

import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderGW;
import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderNapas;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
@Data
public class NapasResponse {
    @Valid
    private HeaderNapas headerNapas;

    @Valid
    private Result result;

    @Valid
    private Payload payload;

    @Valid
    private Order_info order_info;

    @Valid
    private Additional_info additional_info;


    @NotBlank
    private String additional_message;

    @lombok.Data
    public static class Result {
        @NotBlank
        private String id;

        private String code;

        private String message;

        private String description;

        private String severity;

        private String href;


    }

    @lombok.Data
    public static class Payload {


        @Valid
        public Payment payment;

        @Valid
        private Participant participant;

        @Valid
        private Recipient recipient;

        @NotBlank
        private String amount;

        @NotBlank
        private String currency;


        @NotBlank
        private  String recipient_account;

        @lombok.Data
        public static class Payment {

            @NotBlank
            private String type;


            private String generation_method;


            private String exchange_rate;


            private String indicator;

            private String fee_fixed;

            private String fee_percentage;

            @NotBlank
            private String payment_reference;

            private String end_to_end_reference;

            private String trace;


        }
    }


    @lombok.Data
    public static class Participant {

        @NotBlank
        private String originating_institution_id;

        @NotBlank
        private String receiving_institution_id;

        @NotBlank
        private String merchant_id;

        @NotBlank
        private  String merchant_category_code;

        @NotBlank
        private  String card_acceptor_id;

        private  String card_acceptor_name;

        private  String card_acceptor_city;

        private  String card_acceptor_country;

        private  String card_postal_code;

        private  String card_language_preference;

        private  String card_name_alternate_language;

        private  String card_payment_system_specific;


    }

    @lombok.Data
    public static class Recipient {


        private  String first_name;

        private  String middle_name ;

        private  String last_name;

        @NotBlank
        private  String full_name;

        private  String date_of_birth;

        private  Address address;

        @lombok.Data
        public static class Address{

            private  String line1;

            private  String line2 ;

            private  String city;

            private  String country_subdivision;

            private  String postal_code;

            @NotBlank
            private  String country;

            private  String phone;

            private  String email;
        }

    }



    @lombok.Data
    public static class Order_info{
        @NotBlank
        private String bill_number;

        private String mobile_number   ;

        private String store_label;

        private String loyalty_number;

        private String customer_label;

        private String terminal_label    ;

        private String transaction_purpose;

        private String additional_data_request;

    }
    @lombok.Data
    public static class Additional_info{
        private List<instruction> instruction;
    }

    @lombok.Data
    public class instruction {

        @NotBlank
        private String key;

        @NotBlank
        private String value;

        // các getter và setter
    }
}
