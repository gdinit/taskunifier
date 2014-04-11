/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallEditTask extends AbstractCallTask {

    public TaskBean editTask(String accessToken, Task task, boolean syncParent) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(task);

        if (task.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new task");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("title", task.getTitle());
        node.put("completed", task.isCompleted());
        node.put("star", task.isStar());
        node.put("tags", task.getTags().toString());
        node.put("duration", task.getLength());
        node.put("node", task.getNote());

        if (syncParent)
            node.put("parent_id", task.getModelReferenceId("organitask"));

        String content = super.call("PUT", "/tasks/" + task.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

    public TaskBean editTaskParent(String accessToken, Task task) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(task);

        if (task.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new task");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("parent", task.getModelReferenceId("organitask"));

        String content = super.call("PUT", "/tasks/" + task.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
