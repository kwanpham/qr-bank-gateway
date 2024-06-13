package com.infoplusvn.qrbankgateway.dto.response;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class DataResponse {
    private String status;
    private String message;
    private Object data;
}
