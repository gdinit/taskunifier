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

final class CallAddTask extends AbstractCallTask {

    public TaskBean addTask(String accessToken, Task task, boolean syncParent) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(task);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        if (syncParent && task.getParent() != null && task.getParent().getModelReferenceId("organitask") != null)
            node.put("parent_id", task.getParent().getModelReferenceId("organitask"));
        else
            node.put("parent_id", (String) null);

        node.put("title", task.getTitle());
        node.put("completed", task.isCompleted());
        node.put("completion_date", OrganiTaskTranslations.translateUTCDate(task.getCompletedOn()));
        node.put("star", task.isStar());
        node.put("tags", task.getTags().toString());

        if (task.getContexts().size() != 0 && task.getContexts().get(0).getModelReferenceId("organitask") != null)
            node.put("context_id", task.getContexts().get(0).getModelReferenceId("organitask"));
        else
            node.put("context_id", (String) null);

        if (task.getFolder() != null && task.getFolder().getModelReferenceId("organitask") != null)
            node.put("folder_id", task.getFolder().getModelReferenceId("organitask"));
        else
            node.put("folder_id", (String) null);

        if (task.getGoals().size() != 0 && task.getGoals().get(0).getModelReferenceId("organitask") != null)
            node.put("goal_id", task.getGoals().get(0).getModelReferenceId("organitask"));
        else
            node.put("goal_id", (String) null);

        if (task.getStatus() != null && task.getStatus().getModelReferenceId("organitask") != null)
            node.put("task_status_id", task.getStatus().getModelReferenceId("organitask"));
        else
            node.put("task_status_id", (String) null);

        node.put("start_date", OrganiTaskTranslations.translateUTCDate(task.getStartDate()));
        node.put("due_date", OrganiTaskTranslations.translateUTCDate(task.getDueDate()));
        node.put("start_date_reminder", (task.getStartDateReminder() < 0 ? 0 : task.getStartDateReminder()));
        node.put("due_date_reminder", (task.getDueDateReminder() < 0 ? 0 : task.getDueDateReminder()));
        node.put("duration", task.getLength());
        node.put("repeat", RepeatConverter.getRepeat(task.getRepeat()));
        node.put("repeat_from", OrganiTaskTranslations.translateTaskRepeatFrom(task.getRepeatFrom()));
        node.put("progress", (int) (task.getProgress() * 100));
        node.put("priority", OrganiTaskTranslations.translateTaskPriority(task.getPriority()));
        node.put("note", task.getNote());

        String content = super.call("POST", "/tasks", accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
