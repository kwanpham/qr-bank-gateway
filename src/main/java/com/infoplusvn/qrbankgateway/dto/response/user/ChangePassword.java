package com.infoplusvn.qrbankgateway.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassword {

    private String username;

    private String oldPassword;

    private String newPassword;

}
