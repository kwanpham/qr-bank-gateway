package com.infoplusvn.qrbankgateway.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTORegisterRequest {

    private String username;

    private String password;

    private String email;


}
