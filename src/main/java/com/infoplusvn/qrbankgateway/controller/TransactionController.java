package com.infoplusvn.qrbankgateway.controller;

import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.dto.common.payment.TransactionDTO;
import com.infoplusvn.qrbankgateway.dto.common.qribft.QrCodeDTORoleUser;
import com.infoplusvn.qrbankgateway.dto.response.DataResponse;
import com.infoplusvn.qrbankgateway.entity.TransactionActivityEntity;
import com.infoplusvn.qrbankgateway.entity.TransactionEntity;
import com.infoplusvn.qrbankgateway.exception.ResourceNotFoundException;
import com.infoplusvn.qrbankgateway.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v1/transaction")
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

    @GetMapping(value = "/getAllTransactions")
    public DataResponse getAllTransactions() {

        List<TransactionDTO> transactionDTOList = transactionService.getAllTransactions();
        return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                .setMessage(CommonConstant.MESSAGE_SUCCESS)
                .setData(transactionDTOList);
    }

    @GetMapping(value = "/getAllActivity")
    public DataResponse getAllActivity() {

        List<TransactionActivityEntity> transactionActivityEntities = transactionService.getAllActivity();
        return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                .setMessage(CommonConstant.MESSAGE_SUCCESS)
                .setData(transactionActivityEntities);
    }

    @GetMapping(value = "/getActivityByTransactionId/{transactionId}")
    public DataResponse getActivityByTransactionId(@PathVariable Long transactionId) throws Exception {

        List<TransactionActivityEntity> list = transactionService.getActivityByTransactionId(transactionId);

        if (list.size() <= 0) {
            throw new ResourceNotFoundException("không tìm thấy transactionActivity có transactionId = " + transactionId);
        } else {
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage("Success")
                    .setData(list);
        }
    }

//    @GetMapping(value = "/getAllTransactions")
//    public DataResponse getAllTransactions() {
//
//        List<TransactionEntity> transactionDTOList = transactionService.getAllTransactions();
//        return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
//                .setMessage(CommonConstant.MESSAGE_SUCCESS)
//                .setData(transactionDTOList);
//    }
}
