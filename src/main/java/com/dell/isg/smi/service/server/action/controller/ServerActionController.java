/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.service.server.action.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;
import com.dell.isg.smi.commons.model.common.Credential;
import com.dell.isg.smi.commons.model.common.ResponseString;
import com.dell.isg.smi.service.server.action.manager.IActionManager;
import com.dell.isg.smi.service.server.exception.BadRequestException;
import com.dell.isg.smi.service.server.exception.EnumErrorCode;
import com.dell.isg.smi.service.server.inventory.utilities.CustomRecursiveToStringStyle;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/1.0/server/action")
public class ServerActionController {

    @Autowired
    IActionManager actionManagerImpl;

    private static final Logger logger = LoggerFactory.getLogger(ServerActionController.class.getName());


    @RequestMapping(value = "/power/{action}", method = RequestMethod.POST, headers = "Accept=application/json", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "/power/{action}", nickname = "actions", notes = "This operation allow user to manage server power - OFF/ON/REBOOT/EJECT/RESET/SET_TSM/CLEAR_TSM throu wsman.", response = ResponseString.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = String.class), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure") })
    public ResponseString actions(@RequestBody Credential credential, @PathVariable("action") String action) {
        logger.trace("Credential for server action : ", credential.getAddress(), credential.getUserName());
        String result = "Failed to change the state " + action;
        if (credential == null || StringUtils.isEmpty(credential.getAddress()) || StringUtils.isEmpty(action)) {
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorCode(EnumErrorCode.IOIDENTITY_INVALID_INPUT);
            throw badRequestException;
        }
        try {
            WsmanCredentials wsmanCredentials = new WsmanCredentials(credential.getAddress(), credential.getUserName(), credential.getPassword());
            if (actionManagerImpl.performServerAction(wsmanCredentials, action) == 0) {
                result = "Successfully changed the state " + action;
            }
        } catch (Exception e) {
            logger.error("Exception occured : ", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(EnumErrorCode.ENUM_SERVER_ERROR);
            throw runtimeCoreException;
        }
        logger.trace("Result Response : ", ReflectionToStringBuilder.toString(result, new CustomRecursiveToStringStyle(99)));
        return new ResponseString(result);
    }


    @RequestMapping(value = "/blinkLED/{duration}", method = RequestMethod.POST, headers = "Accept=application/json", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "/blinkLED/{duration}", nickname = "blinkLED", notes = "This operation allow user to start blinking LED if duration is greate than 0 otherwise it stops throu wsman.", response = ResponseString.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = String.class), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure") })

    public ResponseString blinkLED(@RequestBody Credential credential, @PathVariable("duration") int duration) {
        logger.trace("Credential for server action : ", credential.getAddress(), credential.getUserName());
        String result = "Failed started blink LED.";
        if (credential == null || StringUtils.isEmpty(credential.getAddress())) {
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorCode(EnumErrorCode.IOIDENTITY_INVALID_INPUT);
            throw badRequestException;
        }
        try {
            WsmanCredentials wsmanCredentials = new WsmanCredentials(credential.getAddress(), credential.getUserName(), credential.getPassword());
            if (actionManagerImpl.actionBlinkLED(wsmanCredentials, duration)) {
                result = "Successfully started blink LED.";
            }
        } catch (Exception e) {
            logger.error("Exception occured  : ", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(EnumErrorCode.ENUM_SERVER_ERROR);
            throw runtimeCoreException;
        }
        logger.trace("Result Response : ", ReflectionToStringBuilder.toString(result, new CustomRecursiveToStringStyle(99)));
        return new ResponseString(result);
    }

}
