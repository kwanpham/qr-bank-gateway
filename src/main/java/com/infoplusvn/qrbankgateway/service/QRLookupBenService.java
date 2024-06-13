package com.infoplusvn.qrbankgateway.service;

import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqInfoGW;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResInfoGW;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResNAPAS;

import java.io.UnsupportedEncodingException;

public interface QRLookupBenService {

//    LookupBenReqInfoGW genMappingReqInfoGW(LookupBenReqNAPAS lookupBenReqNAPAS);
//
//    LookupBenResNAPAS genMappingResNAPAS(LookupBenResInfoGW lookupBenResInfoGW);

    LookupBenResNAPAS genLookupBenResNAPAS(LookupBenReqNAPAS lookupBenReqNAPAS) throws UnsupportedEncodingException;

}
