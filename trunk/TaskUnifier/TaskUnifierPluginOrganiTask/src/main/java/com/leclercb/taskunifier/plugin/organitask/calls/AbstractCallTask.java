/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class AbstractCallTask extends AbstractCall {

    /**
     * Example: [{"team_id":4,"parent_id":null,"context_id":19,"folder_id":22,"goal_id":10,"task_status_id":49,"assignee_id":null,"id":1132,"creation_date":"1396878407","update_date":"1396878407","title":"Adapt the settings","tasks":[],"completed":false,"completion_date":null,"star":false,"progress":0,"tags":"Help","start_date":null,"due_date":null,"start_date_reminder":0,"due_date_reminder":0,"duration":30,"repeat":"","repeat_from":"DUE_DATE","priority":"MEDIUM"},{"team_id":4,"parent_id":null,"context_id":19,"folder_id":22,"goal_id":10,"task_status_id":49,"assignee_id":null,"id":1131,"creation_date":"1396878407","update_date":"1396878407","title":"Add your first task","tasks":[],"completed":false,"completion_date":null,"star":false,"progress":0,"tags":"Help","start_date":null,"due_date":null,"start_date_reminder":0,"due_date_reminder":0,"duration":30,"repeat":"","repeat_from":"DUE_DATE","priority":"HIGH"},{"team_id":4,"parent_id":null,"context_id":19,"folder_id":22,"goal_id":10,"task_status_id":49,"assignee_id":null,"id":1130,"creation_date":"1396878407","update_date":"1396878407","title":"Define your own categories","tasks":[],"completed":false,"completion_date":null,"star":false,"progress":0,"tags":"Help","start_date":null,"due_date":null,"start_date_reminder":0,"due_date_reminder":0,"duration":30,"repeat":"","repeat_from":"DUE_DATE","priority":"HIGH"},{"team_id":4,"parent_id":null,"context_id":19,"folder_id":22,"goal_id":10,"task_status_id":49,"assignee_id":null,"id":1129,"creation_date":"1396878407","update_date":"1396878407","title":"Watch our \"Getting Started\" video","tasks":[],"completed":false,"completion_date":null,"star":true,"progress":0,"tags":"Help","start_date":null,"due_date":null,"start_date_reminder":0,"due_date_reminder":0,"duration":30,"repeat":"","repeat_from":"DUE_DATE","priority":"HIGH"},{"team_id":4,"parent_id":null,"context_id":19,"folder_id":22,"goal_id":10,"task_status_id":53,"assignee_id":null,"id":1128,"creation_date":"1396878407","update_date":"1396878407","title":"Register to OrganiTask","tasks":[],"completed":true,"completion_date":"1396878407","star":false,"progress":0,"tags":"Help","start_date":null,"due_date":null,"start_date_reminder":0,"due_date_reminder":0,"duration":5,"repeat":"","repeat_from":"DUE_DATE","priority":"TOP"}]
     *
     * @param content
     * @return
     * @throws SynchronizerException
     */
    protected TaskBean[] getResponseMessage(String content) throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            List<TaskBean> tasks = new ArrayList<TaskBean>();
            tasks = this.getTaskBeans(root);

            return tasks.toArray(new TaskBean[0]);
        } catch (Exception e) {
            throw new SynchronizerParsingException(
                    "Error while parsing response ("
                            + this.getClass().getName()
                            + ")",
                    content,
                    e);
        }
    }

    private List<TaskBean> getTaskBeans(JsonNode node) {
        List<TaskBean> tasks = new ArrayList<TaskBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                TaskBean bean = this.getTaskBean(item);
                tasks.add(bean);
                tasks.addAll(this.getTaskBeans(item.path("tasks")));
            }
        } else {
            TaskBean bean = this.getTaskBean(node);
            tasks.add(bean);

            if (node.has("tasks") && node.path("tasks").isArray())
                tasks.addAll(this.getTaskBeans(node.path("tasks")));
        }

        return tasks;
    }

    private TaskBean getTaskBean(JsonNode node) {
        TaskBean bean = TaskFactory.getInstance().createOriginalBean();

        bean.getModelReferenceIds().put("organitask", this.getNodeTextValue(node.path("id")));
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelCreationDate(OrganiTaskTranslations.translateUTCDate(node.path("creation_date").asLong()));
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.path("update_date").asLong()));
        bean.setTitle(this.getNodeTextValue(node.path("title")));
        bean.setCompleted(node.path("completed").asBoolean());
        bean.setCompletedOn(OrganiTaskTranslations.translateUTCDate(node.path("completion_date").asLong()));
        bean.setStar(node.path("star").asBoolean());
        bean.setTags(TagList.fromString(this.getNodeTextValue(node.path("tags"))));
        bean.getContexts().add(OrganiTaskTranslations.getModelOrCreateShell(
                ModelType.CONTEXT,
                this.getNodeTextValue(node.path("context_id"))));
        bean.setFolder(OrganiTaskTranslations.getModelOrCreateShell(
                ModelType.FOLDER,
                this.getNodeTextValue(node.path("folder_id"))));
        bean.getGoals().add(OrganiTaskTranslations.getModelOrCreateShell(
                ModelType.GOAL,
                this.getNodeTextValue(node.path("goal_id"))));
        bean.setParent(OrganiTaskTranslations.getModelOrCreateShell(
                ModelType.TASK,
                this.getNodeTextValue(node.path("parent_id"))));
        bean.setStatus(OrganiTaskTranslations.getModelOrCreateShell(
                ModelType.TASK_STATUS,
                this.getNodeTextValue(node.path("task_status_id"))));
        bean.setStartDate(OrganiTaskTranslations.translateUTCDate(node.path("start_date").asLong()));
        bean.setDueDate(OrganiTaskTranslations.translateUTCDate(node.path("due_date").asLong()));
        bean.setStartDateReminder(node.path("start_date_reminder").asInt());
        bean.setDueDateReminder(node.path("due_date_reminder").asInt());
        bean.setLength(node.path("duration").asInt());
        bean.setRepeatFrom(OrganiTaskTranslations.translateTaskRepeatFrom(this.getNodeTextValue(node.path("repeat_from"))));
        bean.setProgress(node.path("progress").asInt() / 100);
        bean.setPriority(OrganiTaskTranslations.translateTaskPriority(this.getNodeTextValue(node.path("priority"))));
        bean.setNote(this.getNodeTextValue(node.path("note")));

        return bean;
    }

}
