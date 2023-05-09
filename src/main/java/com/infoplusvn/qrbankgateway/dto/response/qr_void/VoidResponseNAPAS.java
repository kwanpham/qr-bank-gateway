package com.infoplusvn.qrbankgateway.dto.response.qr_void;


import lombok.Data;

import java.util.List;

@Data
public class VoidResponseNAPAS {

    private Payload payload;

    private AdditionalInfo additional_info;

    @lombok.Data
    public static class Payload {

        private Original original;

    }

    @lombok.Data
    public static class Original {

        private Payment payment;

        private String amount;

        private String currency;

        private String settlement_amount;

        private String settlement_currency;

        private String settlement_date;

        private String sender_account;

        private Sender sender;

        private Participant participant;

        private String recipient_account;

        private Recipient recipient;

        private String additional_message;

        private OrderInfo order_info;


    }


    @lombok.Data
    public static class Payment {

        private String reference;

        private String authorization_code;

        private String funding_reference;

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

        private String payment_reference;

        private String end_to_end_reference;

        private String trace;

    }

    @lombok.Data
    public static class Sender {

        private String first_name;

        private String middle_name;

        private String last_name;

        private String full_name;

        private String date_of_birth;

        private Address address;
    }

    @lombok.Data
    public static class Participant {

        private String originating_institution_id;

        private String receiving_institution_id;

        private String merchant_id;

        private String merchant_category_code;

        private String card_acceptor_id;

        private String card_acceptor_name;

        private String card_acceptor_city;

        private String card_acceptor_country;

        private String card_postal_code;

        private String card_language_preference;

        private String card_name_alternate_language;

        private String city_alternate_language;

        private String card_payment_system_specific;


    }

    @lombok.Data
    public static class Recipient {

        private String first_name;

        private String middle_name;

        private String last_name;

        private String full_name;

        private String date_of_birth;

        private Address address;


    }

    @lombok.Data
    public static class Address {

        private String line1;

        private String line2;

        private String city;

        private String country_subdivision;

        private String postal_code;

        private String country;

        private String phone;

        private String email;

    }

    @lombok.Data
    public static class OrderInfo {

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
