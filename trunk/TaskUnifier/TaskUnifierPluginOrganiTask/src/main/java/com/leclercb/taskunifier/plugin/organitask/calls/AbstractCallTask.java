/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.TagList;
import com.leclercb.taskunifier.api.models.TaskFactory;
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
            tasks = this.getTaskBeans(root, null);

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

    private List<TaskBean> getTaskBeans(JsonNode node, ModelId parentId) {
        List<TaskBean> tasks = new ArrayList<TaskBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                TaskBean bean = this.getTaskBean(item, parentId);
                tasks.add(bean);
                tasks.addAll(this.getTaskBeans(item.path("tasks"), bean.getModelId()));
            }
        } else {
            TaskBean bean = this.getTaskBean(node, parentId);
            tasks.add(bean);
            tasks.addAll(this.getTaskBeans(node.path("tasks"), bean.getModelId()));
        }

        return tasks;
    }

    private TaskBean getTaskBean(JsonNode node, ModelId parentId) {
        TaskBean bean = TaskFactory.getInstance().createOriginalBean();

        bean.setModelId(new ModelId());
        bean.getModelReferenceIds().put("organitask", node.path("id").textValue());
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.path("update_date").longValue()));
        bean.setParent(parentId);
        bean.setTitle(node.path("title").textValue());
        bean.setCompleted(node.path("completed").booleanValue());
        bean.setStar(node.path("star").booleanValue());
        bean.setTags(TagList.fromString(node.path("tags").textValue()));
        bean.setLength(node.path("duration").intValue());
        bean.setNote(node.path("note").textValue());

        return bean;
    }

}
