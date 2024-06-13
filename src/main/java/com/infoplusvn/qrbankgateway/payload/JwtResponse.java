package com.infoplusvn.qrbankgateway.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;
    private String access_token;
    private String token_type;
    private String expires_in;


//    public JwtResponse(String jwtToken) {
//        this.jwtToken = jwtToken;
//
//    }
//
//
//    public String getToken() {
//        return this.jwtToken;
//    }
}

