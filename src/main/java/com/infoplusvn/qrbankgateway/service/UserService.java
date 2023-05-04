package com.infoplusvn.qrbankgateway.service;

import com.infoplusvn.qrbankgateway.dto.common.user.UserDTORoleAdmin;
import com.infoplusvn.qrbankgateway.dto.common.user.UserDTORoleUser;
import com.infoplusvn.qrbankgateway.dto.request.user.UserDTORegisterRequest;
import com.infoplusvn.qrbankgateway.dto.response.user.ChangePassword;
import com.infoplusvn.qrbankgateway.entity.UserEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> getAllUsers();

    UserEntity createUser(UserDTORegisterRequest userRequest);

    UserEntity roleUserUpdateUser(UserDTORoleUser userRequest);

    UserEntity deactiveUser(UserDTORoleAdmin userRequest);

    UserEntity getUserById(long id);

    UserEntity getUserByUserNameRoleUser(String username);

    UserEntity getUserByUserNameRoleAdmin(String username);

    UserEntity getUserByEmail(String email);

    List<String> getAllUsername();

    List<String> getAllEmail();

    boolean checkPassword(String CSDLPassword, String passwordRequest);

    void changePassword(ChangePassword changePassword);

    UserEntity roleAdminUpdateUser(UserDTORoleAdmin userDTORoleAdmin);
}
