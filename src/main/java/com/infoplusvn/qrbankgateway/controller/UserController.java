package com.infoplusvn.qrbankgateway.controller;

import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.dto.common.user.UserDTORoleAdmin;
import com.infoplusvn.qrbankgateway.dto.common.user.UserDTORoleUser;
import com.infoplusvn.qrbankgateway.dto.request.user.UserDTORegisterRequest;
import com.infoplusvn.qrbankgateway.dto.response.user.ChangePassword;
import com.infoplusvn.qrbankgateway.dto.response.DataResponse;
import com.infoplusvn.qrbankgateway.entity.UserEntity;
import com.infoplusvn.qrbankgateway.exception.ResourceNotFoundException;
import com.infoplusvn.qrbankgateway.payload.JwtResponse;
import com.infoplusvn.qrbankgateway.service.impl.UserServiceImpl;
import com.infoplusvn.qrbankgateway.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping(value = "/infogw/qr/v1")
@RestController
public class UserController {
    final String TOKEN_PREFIX = "Bearer";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/oauth/token")
    public ResponseEntity<?> createAuthenticationToken(@RequestHeader("BasicAuth") String authorization) throws Exception {

        authorization = authorization.substring(6);
        //log.info("authorization: {} " , authorization );
        byte[] decodedBytes = Base64.getDecoder().decode(authorization);
        String decodedString = new String(decodedBytes);
        String[] result = decodedString.split(":");

        String username = result[0];
        String password = result[1];

        Authentication a = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        String token = jwtTokenUtil.generateToken((UserDetails) a.getPrincipal());

        return ResponseEntity.ok(new JwtResponse(token, TOKEN_PREFIX, jwtTokenUtil.getExpiresIn(jwtTokenUtil.getExpirationDateFromToken(token))));
    }


    @GetMapping(value = "/getAllUsers")
    public DataResponse getAllUsers() throws Exception {

        List<UserEntity> userEntityList = userService.getAllUsers();
        List<UserDTORoleAdmin> listUserDTO = userEntityList
                .stream().map(UserEntity -> modelMapper.map(UserEntity, UserDTORoleAdmin.class))
                .collect(Collectors.toList());


        return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                .setMessage(CommonConstant.MESSAGE_SUCCESS)
                .setData(listUserDTO);

    }

    @PostMapping(value = "/createUser")
    public DataResponse createUser(@RequestBody UserDTORegisterRequest userDTO) throws Exception {


        if (userService.getUserByUserNameRoleAdmin(userDTO.getUsername().trim()) != null
                || userService.getUserByEmail(userDTO.getEmail().trim()) != null) {

            return new DataResponse().setStatus(CommonConstant.STATUS_ERR)
                    .setMessage("Username hoặc Email đã được sử dụng")
                    .setData(null);

        } else {
            UserEntity userEntity = userService.createUser(userDTO);

            return new DataResponse().setStatus(CommonConstant.STATUS_CREATE_SUCCESS)
                    .setMessage(CommonConstant.MESSAGE_CREATED_SUCCESS)
                    .setData(null);

        }
    }


    @PutMapping(value = "/roleUserUpdateUser")
    public DataResponse roleUserUpdateUser(@RequestBody UserDTORoleUser userDTO) throws Exception {

        UserEntity getUserByEmail = userService.getUserByEmail(userDTO.getEmail().trim());
        UserEntity getUserByUserName = userService.getUserByUserNameRoleUser(userDTO.getUsername().trim());

        if (getUserByUserName == null) {

            throw new ResourceNotFoundException("không tìm thấy tài khoản có username = " + userDTO.getUsername());


        } else if (getUserByEmail != null && !getUserByEmail.getId().equals(getUserByUserName.getId())) {

            return new DataResponse().setStatus(CommonConstant.STATUS_ERR)
                    .setMessage("Email đã được sử dụng")
                    .setData(null);

        } else {

            UserEntity userEntity = userService.roleUserUpdateUser(userDTO);
            //UserDTORoleUser user = modelMapper.map(userEntity,UserDTORoleUser.class);
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage(CommonConstant.MESSAGE_UPDATED_SUCCESS)
                    .setData(null);
        }


    }


    @PutMapping(value = "/roleAdminUpdateUser")
    public DataResponse roleAdminUpdateUser(@RequestBody UserDTORoleAdmin userDTO) throws Exception {

        if (userService.getUserByUserNameRoleAdmin(userDTO.getUsername().trim()) == null) {

            throw new ResourceNotFoundException("không tìm thấy tài khoản có username = " + userDTO.getUsername());


        } else {

            UserEntity userEntity = userService.roleAdminUpdateUser(userDTO);
            UserDTORoleAdmin user = modelMapper.map(userEntity, UserDTORoleAdmin.class);

            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage(CommonConstant.MESSAGE_UPDATED_SUCCESS)
                    .setData(user);
        }


    }

    @GetMapping(value = "/getUserByUsername/{username}")
    public DataResponse getUserByUsername(@PathVariable String username) throws Exception {

        UserEntity userEntity = userService.getUserByUserNameRoleUser(username);
        if (userEntity == null) {

            throw new ResourceNotFoundException("không tìm thấy tài khoản có username = " + username);

        } else {
            UserDTORoleUser userDTORoleUser = modelMapper.map(userEntity, UserDTORoleUser.class);
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage("Success")
                    .setData(userDTORoleUser);
        }

    }


    @GetMapping(value = "/getAllUsername")
    public DataResponse getAllUsername() throws Exception {

        List<String> list = userService.getAllUsername();
        return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                .setMessage(CommonConstant.MESSAGE_SUCCESS)
                .setData(list);

    }

    @GetMapping(value = "/getAllEmail")
    public DataResponse getAllEmail() throws Exception {

        List<String> list = userService.getAllEmail();
        return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                .setMessage(CommonConstant.MESSAGE_SUCCESS)
                .setData(list);

    }


    @PutMapping(value = "/changePassword")
    public DataResponse changePassword(@RequestBody ChangePassword changePassword) throws Exception {

        UserEntity userEntity = userService.getUserByUserNameRoleUser(changePassword.getUsername().trim());
        if (userEntity == null) {
            throw new ResourceNotFoundException("không tìm thấy tài khoản có username = " + changePassword.getUsername());
        } else {

            if (!userService.checkPassword(userEntity.getPassword(), changePassword.getOldPassword())) {
                return new DataResponse().setStatus(CommonConstant.STATUS_ERR)
                        .setMessage("Mật khẩu cũ không đúng")
                        .setData(null);

            } else {
                userService.changePassword(changePassword);
                return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                        .setMessage(CommonConstant.MESSAGE_CHANGED_SUCCESS)
                        .setData(null);
            }
        }


    }

}
