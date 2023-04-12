package com.infoplusvn.qrbankgateway.controller;

import com.infoplusvn.qrbankgateway.entity.BankEntity;
import com.infoplusvn.qrbankgateway.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v3")
public class BankController {

    @Autowired
    BankService bankService;

    @GetMapping(value = "/banks")
    public List<BankEntity> getAll(){
        return bankService.getAllbank();
    }
}
