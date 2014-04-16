/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.TaskStatus;
import com.leclercb.taskunifier.api.models.beans.TaskStatusBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.gui.api.models.GuiModel;

final class CallAddTaskStatus extends AbstractCallTaskStatus {

    public TaskStatusBean addTaskStatus(String accessToken, TaskStatus taskStatus) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(taskStatus);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("title", taskStatus.getTitle());

        if (taskStatus instanceof GuiModel) {
            node.put("color", OrganiTaskTranslations.translateColor(((GuiModel) taskStatus).getColor()));
        }

        String content = super.call("POST", "/task_statuses", accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
