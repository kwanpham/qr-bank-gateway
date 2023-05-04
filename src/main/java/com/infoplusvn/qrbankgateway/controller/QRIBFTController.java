package com.infoplusvn.qrbankgateway.controller;

import com.google.zxing.WriterException;
import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.dto.common.qribft.QrCodeDTORoleUser;
import com.infoplusvn.qrbankgateway.dto.request.qribft.ChangeQRNameRequest;
import com.infoplusvn.qrbankgateway.dto.request.qribft.DeCodeQRRequest;
import com.infoplusvn.qrbankgateway.dto.request.qribft.GenerateAdQR;
import com.infoplusvn.qrbankgateway.dto.request.qribft.GenerateQRRequest;
import com.infoplusvn.qrbankgateway.dto.response.DataResponse;
import com.infoplusvn.qrbankgateway.dto.response.qribft.DeCodeQRResponse;
import com.infoplusvn.qrbankgateway.dto.response.qribft.GenerateQRResponse;
import com.infoplusvn.qrbankgateway.entity.QRCodeEntity;
import com.infoplusvn.qrbankgateway.exception.ResourceNotFoundException;
import com.infoplusvn.qrbankgateway.service.impl.QrIBTFServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v1")
public class QRIBFTController {

    @Autowired
    QrIBTFServiceImpl qrService;


    @PostMapping(value = "/genQR")
    public GenerateQRResponse generateQRCode(@RequestBody GenerateQRRequest generateQRRequest) throws IOException, WriterException {

        return qrService.genQRResponse(generateQRRequest);

    }

    @PostMapping(value = "/readQR")
    public DeCodeQRResponse readQRCode(@RequestBody DeCodeQRRequest deCodeQRRequest) throws UnsupportedEncodingException {

        return qrService.parseQRString(deCodeQRRequest);

    }

    @PostMapping(value = "/genAdQR")
    public GenerateQRResponse genAdQR(@RequestBody GenerateAdQR generateAdQR) throws IOException, WriterException {
        return qrService.genAdQR(generateAdQR);
    }


    @GetMapping(value = "/getQRByCreatedUser/{createdUser}")
    public DataResponse getUserByUsername(@PathVariable String createdUser) throws Exception {

        List<QrCodeDTORoleUser> qrCodeDTORoleUsers = qrService.finByCreatedUserRoleUser(createdUser.trim());

        if (qrCodeDTORoleUsers.size() < 0) {
            throw new ResourceNotFoundException("không tìm thấy qrcode có createdUser = " + createdUser);
        } else {
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage("Success")
                    .setData(qrCodeDTORoleUsers);
        }
    }

    @GetMapping(value = "/getQRDeleted/{createdUser}")
    public DataResponse getQRDeleted(@PathVariable String createdUser) throws Exception {

        List<QrCodeDTORoleUser> qrCodeDTORoleUsers = qrService.findByCreatedUserAndEnabledFalseRoleUser(createdUser.trim());

        if (qrCodeDTORoleUsers.size() < 0) {
            throw new ResourceNotFoundException("không tìm thấy qrcode có createdUser = " + createdUser);
        } else {
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage("Success")
                    .setData(qrCodeDTORoleUsers);
        }
    }

    @PutMapping(value = "/disableQR/{id}")
    public DataResponse disableQR(@PathVariable Long id) throws Exception {

        QRCodeEntity qrCodeEntity = qrService.findByQrId(id);
        if (qrCodeEntity == null) {
            throw new ResourceNotFoundException("không tìm thấy qrcode có id = " + id);

        } else {
            qrService.disableQRCode(id);
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage(CommonConstant.MESSAGE_DEACTIVATED_SUCCESS)
                    .setData(null);
        }

    }

    @PutMapping(value = "/enableQR/{id}")
    public DataResponse enableQR(@PathVariable Long id) throws Exception {

        QRCodeEntity qrCodeEntity = qrService.findByQrId(id);
        if (qrCodeEntity == null) {
            throw new ResourceNotFoundException("không tìm thấy qrcode có id = " + id);

        } else {
            qrService.enableQRCode(id);
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage("Enabled Success")
                    .setData(null);
        }

    }

    @PutMapping(value = "/changeQRName")
    public DataResponse changeQRName(@RequestBody ChangeQRNameRequest changeQRNameRequest) throws Exception {

        QRCodeEntity qrCodeEntity = qrService.findByQrId(changeQRNameRequest.getQrId());
        if (qrCodeEntity == null) {
            throw new ResourceNotFoundException("không tìm thấy qrcode có id = " + changeQRNameRequest.getQrId());

        } else {
            qrService.changeQRName(changeQRNameRequest);
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage("Success")
                    .setData(null);
        }

    }

    @GetMapping(value = "/getQrDTOById/{id}")
    public DataResponse getQrDTOById(@PathVariable Long id) throws Exception {

        QRCodeEntity qrCodeEntity = qrService.findByQrId(id);
        if (qrCodeEntity == null) {
            throw new ResourceNotFoundException("không tìm thấy qrcode có id = " + id);

        } else {
            QrCodeDTORoleUser qrDTOById = qrService.getQrDTOById(id);
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage("Success")
                    .setData(qrDTOById);
        }

    }

    @GetMapping(value = "/getQrThemeImageById/{id}")
    public DataResponse getQrThemeImageById(@PathVariable Long id) throws Exception {

        QRCodeEntity qrCodeEntity = qrService.findByQrId(id);
        if (qrCodeEntity == null) {
            throw new ResourceNotFoundException("không tìm thấy qrcode có id = " + id);

        } else {
            String qrThemeImage = qrService.getQrThemeImageById(id);
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage("Success")
                    .setData(qrThemeImage);
        }

    }

    @DeleteMapping(value = "/deleteQR/{id}")
    public DataResponse deleteQR(@PathVariable Long id) throws Exception {
        QRCodeEntity qrCodeEntity = qrService.findByQrId(id);
        if (qrCodeEntity == null) {
            throw new ResourceNotFoundException("không tìm thấy qrcode có id = " + id);

        } else {
            qrService.deleteQR(id);
            return new DataResponse().setStatus(CommonConstant.STATUS_SUCCESS)
                    .setMessage("Deleted Success")
                    .setData(null);
        }
    }





}
