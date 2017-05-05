/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.service.server.action.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.adapter.server.IServerAdapter;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.service.server.action.controller.ServerActionsEnum;

@Component
public class ActionManagerImpl implements IActionManager {

    @Autowired
    IServerAdapter serverAdapterImpl;


    @Override
    public int performServerAction(WsmanCredentials wsmanCredentials, String action) throws Exception {
        int status = 1;
        switch (ServerActionsEnum.valueOf(action)) {
        case EJECT:
            status = serverAdapterImpl.ejectServer(wsmanCredentials) ? 0 : 1;
            break;
        case RESET:
            status = serverAdapterImpl.resetServer(wsmanCredentials);
            break;
        case ON:
            status = serverAdapterImpl.manageServerPower(wsmanCredentials, action);
            break;
        case OFF:
            status = serverAdapterImpl.manageServerPower(wsmanCredentials, action);
            break;
        case REBOOT:
            status = serverAdapterImpl.manageServerPower(wsmanCredentials, action);
            break;
        case SET_TSM:
            status = serverAdapterImpl.setServerTroubleShootMode(wsmanCredentials).contains("Success") ? 0 : 1;// May need to change later
            break;
        case CLEAR_TSM:
            status = serverAdapterImpl.clearServerTroubleShootMode(wsmanCredentials).contains("Success") ? 0 : 1;// May need to change later
            break;
        }
        return status;
    }


    @Override
    public boolean actionBlinkLED(WsmanCredentials wsmanCredentials, int duration) throws Exception {
        boolean status = false;
        if (duration <= 0) {
            status = serverAdapterImpl.stopBlinkLed(wsmanCredentials);
        } else {
            status = serverAdapterImpl.startBlinkLed(wsmanCredentials, duration);
        }
        return status;
    }

}
