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

final class CallEditContext extends AbstractCallContext {

    public ContextBean editContext(String accessToken, Context context, boolean syncParent) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(context);

        if (context.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new context");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("title", context.getTitle());

        if (syncParent)
            node.put("parent_id", context.getModelReferenceId("organitask"));

        String content = super.call("PUT", "/contexts/" + context.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

    public ContextBean editContextParent(String accessToken, Context context) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(context);

        if (context.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new context");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("parent_id", context.getModelReferenceId("organitask"));

        String content = super.call("PUT", "/contexts/" + context.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
