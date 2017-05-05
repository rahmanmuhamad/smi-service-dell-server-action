/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.service.server.action.manager;

import com.dell.isg.smi.adapter.server.model.WsmanCredentials;

public interface IActionManager {

    public int performServerAction(WsmanCredentials wsmanCredentials, String action) throws Exception;


    public boolean actionBlinkLED(WsmanCredentials wsmanCredentials, int duration) throws Exception;
}
