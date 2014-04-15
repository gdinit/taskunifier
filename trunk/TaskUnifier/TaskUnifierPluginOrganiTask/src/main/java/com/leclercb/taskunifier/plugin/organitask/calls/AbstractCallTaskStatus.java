/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.TaskStatusFactory;
import com.leclercb.taskunifier.api.models.beans.TaskStatusBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class AbstractCallTaskStatus extends AbstractCall {

    /**
     * Example: [{"team_id":4,"id":49,"creation_date":"1396878407","update_date":"1396878407","title":"1 - Open","color":"FFFF00"},{"team_id":4,"id":50,"creation_date":"1396878407","update_date":"1396878407","title":"2 - Ongoing","color":"3366FF"}]
     *
     * @param content
     * @return
     * @throws com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException
     */
    protected TaskStatusBean[] getResponseMessage(String content) throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            List<TaskStatusBean> taskStatuses = new ArrayList<TaskStatusBean>();
            taskStatuses = this.getTaskStatusBeans(root);

            return taskStatuses.toArray(new TaskStatusBean[0]);
        } catch (Exception e) {
            throw new SynchronizerParsingException(
                    "Error while parsing response ("
                            + this.getClass().getName()
                            + ")",
                    content,
                    e);
        }
    }

    private List<TaskStatusBean> getTaskStatusBeans(JsonNode node) {
        List<TaskStatusBean> taskStatuses = new ArrayList<TaskStatusBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                TaskStatusBean bean = this.getTaskStatusBean(item);
                taskStatuses.add(bean);
            }
        } else {
            TaskStatusBean bean = this.getTaskStatusBean(node);
            taskStatuses.add(bean);
        }

        return taskStatuses;
    }

    private TaskStatusBean getTaskStatusBean(JsonNode node) {
        TaskStatusBean bean = TaskStatusFactory.getInstance().createOriginalBean();

        bean.getModelReferenceIds().put("organitask", node.path("id").asText());
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelCreationDate(OrganiTaskTranslations.translateUTCDate(node.path("creation_date").asLong()));
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.path("update_date").asLong()));
        bean.setTitle(node.path("title").asText());

        return bean;
    }

}
