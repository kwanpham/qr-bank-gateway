package com.infoplusvn.qrbankgateway.service.impl;

import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.dto.common.user.UserDTORoleAdmin;
import com.infoplusvn.qrbankgateway.dto.common.user.UserDTORoleUser;
import com.infoplusvn.qrbankgateway.dto.request.user.UserDTORegisterRequest;
import com.infoplusvn.qrbankgateway.dto.response.user.ChangePassword;
import com.infoplusvn.qrbankgateway.entity.UserEntity;
import com.infoplusvn.qrbankgateway.repo.UserRepo;
import com.infoplusvn.qrbankgateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserEntity> getAllUsers() {

        return userRepo.findAll();
    }

    @Override
    public UserEntity createUser(UserDTORegisterRequest userRequest) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(userRequest.getUsername().trim());
        userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword().trim()));
        userEntity.setEmail(userRequest.getEmail().trim());
        userEntity.setEnabled(true);
        userEntity.setCreateOn(LocalDateTime.now());
        userEntity.setRoles(CommonConstant.ROLE_USER);

        return userRepo.save(userEntity);
    }

    @Override
    public UserEntity roleUserUpdateUser(UserDTORoleUser userRequest) {

        UserEntity userEntity = userRepo.findByUsernameAndEnabled(userRequest.getUsername());

        userEntity.setUsername(userRequest.getUsername().trim());
        //userEntity.setPassword(userRequest.getPassword());
        userEntity.setEmail(userRequest.getEmail().trim());
        userEntity.setPhone(userRequest.getPhone());
        userEntity.setCompany(userRequest.getCompany());
        userEntity.setAddress(userRequest.getAddress());
        userEntity.setFirstName(userRequest.getFirstName());
        userEntity.setLastName(userRequest.getLastName());

        return userRepo.save(userEntity);
    }


    @Override
    public UserEntity deactiveUser(UserDTORoleAdmin userRequest) {

        UserEntity user = userRepo.findByUsername(userRequest.getUsername());
        user.setEnabled(false);
        return userRepo.save(user);
    }

    @Override
    public UserEntity getUserById(long id) {
        return userRepo.findOneById(id);
    }

    @Override
    public UserEntity getUserByUserNameRoleUser(String username) {
        return userRepo.findByUsernameAndEnabled(username.trim());
    }

    @Override
    public UserEntity getUserByUserNameRoleAdmin(String username) {
        return userRepo.findByUsername(username.trim());
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepo.findByEmail(email.trim());
    }

    @Override
    public List<String> getAllUsername() {
        List<UserEntity> userEntityList = userRepo.findAll();
        List<String> listUsername = new ArrayList<>();
        for (UserEntity userEntity : userEntityList) {
            listUsername.add(userEntity.getUsername());
        }
        return listUsername;
    }

    @Override
    public List<String> getAllEmail() {
        List<UserEntity> userEntityList = userRepo.findAll();
        List<String> listUsername = new ArrayList<>();
        for (UserEntity userEntity : userEntityList) {
            listUsername.add(userEntity.getEmail());
        }
        return listUsername;
    }

    @Override
    public boolean checkPassword(String CSDLPassword, String passwordRequest) {
        return passwordEncoder.matches(passwordRequest, CSDLPassword);

    }

    @Override
    public void changePassword(ChangePassword changePassword) {
        UserEntity userEntity = userRepo.findByUsernameAndEnabled(changePassword.getUsername());

        String oldPassword = changePassword.getOldPassword().trim();
        String newPassword = changePassword.getNewPassword().trim();

        if (checkPassword(userEntity.getPassword(), oldPassword)) {
            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(userEntity);
        }
    }

    @Override
    public UserEntity roleAdminUpdateUser(UserDTORoleAdmin userDTORoleAdmin) {

        UserEntity userEntity = userRepo.findByUsername(userDTORoleAdmin.getUsername());

        userEntity.setRoles(userDTORoleAdmin.getRoles());
        userEntity.setEnabled(userDTORoleAdmin.isEnabled());

        return userRepo.save(userEntity);

    }
}
