package com.infoplusvn.qrbankgateway.controller;

import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqInfoGW;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqNAPAS;
import com.infoplusvn.qrbankgateway.dto.request.qribft.DeCodeQRRequest;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResInfoGW;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.qribft.DeCodeQRResponse;
import com.infoplusvn.qrbankgateway.service.impl.QRLookupBenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v1/ben")
public class QRLookupBenController {

    @Autowired
    QRLookupBenServiceImpl qrLookupBenService;

//    @PostMapping(value = "/lookup")
//    public LookupBenReqInfoGW lookup(@RequestBody LookupBenReqNAPAS lookupBenReqNAPAS) throws UnsupportedEncodingException {
//
//        return qrLookupBenService.genMappingReqInfoGW(lookupBenReqNAPAS);
//
//    }

    @PostMapping(value = "/lookup")
    public LookupBenResNAPAS genLookupBenResNAPAS(@RequestBody LookupBenReqNAPAS lookupBenReqNAPAS) throws UnsupportedEncodingException {

        return qrLookupBenService.genLookupBenResNAPAS(lookupBenReqNAPAS);

    }
}
