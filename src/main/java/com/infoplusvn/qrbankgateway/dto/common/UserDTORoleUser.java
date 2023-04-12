package com.infoplusvn.qrbankgateway.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTORoleUser {

    private String username;

    //private String password;

    private String email;

    private String phone;

    private String company;

    private String address;

    private String firstName;

    private String lastName;

}
