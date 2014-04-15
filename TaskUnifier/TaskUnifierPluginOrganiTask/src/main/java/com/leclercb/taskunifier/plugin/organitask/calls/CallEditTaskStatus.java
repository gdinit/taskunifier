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

final class CallEditTaskStatus extends AbstractCallTaskStatus {

    public TaskStatusBean editTaskStatus(String accessToken, TaskStatus taskStatus) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(taskStatus);

        if (taskStatus.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new task status");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("title", taskStatus.getTitle());

        String content = super.call("PUT", "/task_statuses/" + taskStatus.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
