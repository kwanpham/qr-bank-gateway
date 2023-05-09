package com.infoplusvn.qrbankgateway.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDataGenQR {

    private String firstName;

    private String lastName;

    private String accountNumber;



}
