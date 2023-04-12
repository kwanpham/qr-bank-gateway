package com.infoplusvn.qrbankgateway.service;

import com.infoplusvn.qrbankgateway.entity.BankEntity;
import com.infoplusvn.qrbankgateway.repo.BankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {

    @Autowired
    private BankRepo bankRepo;

    public List<BankEntity> getAllbank(){
        return bankRepo.findAll();
    }
}
