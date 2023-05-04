package com.infoplusvn.qrbankgateway.dto.common.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTORoleAdmin {
    private String username;

    private boolean enabled;

    private LocalDateTime createOn;

    private String roles;
}
