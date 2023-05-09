package com.infoplusvn.qrbankgateway.controller;

import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.dto.common.payment.TransactionDTO;
import com.infoplusvn.qrbankgateway.dto.response.DataResponse;
import com.infoplusvn.qrbankgateway.entity.TransactionEntity;
import com.infoplusvn.qrbankgateway.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v1/transaction")
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

//    @GetMapping(value = "/getAllTransactions")
//    public DataResponse getAllTransactions() {
//
//        List<TransactionDTO> transactionDTOList = transactionService.getAllTransactions();
//        return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
//                .setMessage(CommonConstant.MESSAGE_SUCCESS)
//                .setData(transactionDTOList);
//    }

    @GetMapping(value = "/getAllTransactions")
    public DataResponse getAllTransactions() {

        List<TransactionEntity> transactionDTOList = transactionService.getAllTransactions();
        return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                .setMessage(CommonConstant.MESSAGE_SUCCESS)
                .setData(transactionDTOList);
    }
}
