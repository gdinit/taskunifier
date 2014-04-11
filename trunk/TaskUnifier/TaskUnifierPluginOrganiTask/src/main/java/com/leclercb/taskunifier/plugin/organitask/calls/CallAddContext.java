/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.beans.ContextBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallAddContext extends AbstractCallContext {

    public ContextBean addContext(String accessToken, Context context) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(context);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("title", context.getTitle());

        String content = super.call("POST", "/contexts", accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
