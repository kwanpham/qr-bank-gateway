package com.infoplusvn.qrbankgateway.controller;

import com.infoplusvn.qrbankgateway.dto.request.LookupIssuer.GwRequest;
import com.infoplusvn.qrbankgateway.dto.request.LookupIssuer.NapasRequest;
import com.infoplusvn.qrbankgateway.dto.response.LookupIssuer.GwResponse;
import com.infoplusvn.qrbankgateway.dto.response.LookupIssuer.NapasResponse;
import com.infoplusvn.qrbankgateway.service.LookupIssuerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v1")
public class LookupIssuerController {

    @Autowired
    LookupIssuerService lookupIssuerService;



    @PostMapping(value =  "/issuer/lookup/")
    public GwResponse ResponseNapas1(@RequestBody GwRequest gwRequest) throws UnsupportedEncodingException {
        return lookupIssuerService.genLookupResNAPAS(gwRequest);
    }

    @PostMapping(value =  "/issuer/lookup/res")
    public GwResponse ResponseNapas(@RequestBody NapasResponse napasResponse){
        return lookupIssuerService.convertResponse(napasResponse);
    }
}
