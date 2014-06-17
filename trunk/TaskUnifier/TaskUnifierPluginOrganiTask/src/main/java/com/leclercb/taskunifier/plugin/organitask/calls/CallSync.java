/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallSync extends AbstractCall {

    public void syncStart(String accessToken) throws SynchronizerException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("application_name", "TaskUnifier");

        super.call("POST", "/sync/start", accessToken, node.toString());
    }

    public void syncEnd(String accessToken) throws SynchronizerException {
        super.call("POST", "/sync/end", accessToken, null);
    }

}
