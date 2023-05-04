package com.infoplusvn.qrbankgateway.service.impl;

import com.infoplusvn.qrbankgateway.entity.BankEntity;
import com.infoplusvn.qrbankgateway.repo.BankRepo;
import com.infoplusvn.qrbankgateway.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepo bankRepo;


    @Override
    public List<BankEntity> getAllbank(){
        return bankRepo.findAll();
    }
}
