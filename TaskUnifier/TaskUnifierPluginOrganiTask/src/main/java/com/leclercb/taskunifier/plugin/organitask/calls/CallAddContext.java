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
import com.leclercb.taskunifier.gui.api.models.GuiModel;

final class CallAddContext extends AbstractCallContext {

    public ContextBean addContext(String accessToken, Context context, boolean syncParent) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(context);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        if (syncParent && context.getParent() != null && context.getParent().getModelReferenceId("organitask") != null)
            node.put("parent_id", context.getParent().getModelReferenceId("organitask"));
        else
            node.put("parent_id", (String) null);

        node.put("title", context.getTitle());

        if (context instanceof GuiModel) {
            node.put("color", OrganiTaskTranslations.translateColor(((GuiModel) context).getColor()));
        }

        String content = super.call("POST", "/contexts", accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
