/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.beans.GoalBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class AbstractCallGoal extends AbstractCall {

    /**
     * Example: [{"team_id":1,"parent_id":null,"id":1,"creation_date":"1395847564","update_date":"1395847564","title":"Master OrganiTask","color":"FF8C00","level":"SHORT","goals":[]}]
     *
     * @param content
     * @return
     * @throws SynchronizerException
     */
    protected GoalBean[] getResponseMessage(String content) throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            List<GoalBean> goals = new ArrayList<GoalBean>();
            goals = this.getGoalBeans(root, null);

            return goals.toArray(new GoalBean[0]);
        } catch (Exception e) {
            throw new SynchronizerParsingException(
                    "Error while parsing response ("
                            + this.getClass().getName()
                            + ")",
                    content,
                    e);
        }
    }

    private List<GoalBean> getGoalBeans(JsonNode node, ModelId parentId) {
        List<GoalBean> goals = new ArrayList<GoalBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                GoalBean bean = this.getGoalBean(item, parentId);
                goals.add(bean);
                goals.addAll(this.getGoalBeans(item.path("goals"), bean.getModelId()));
            }
        } else {
            GoalBean bean = this.getGoalBean(node, parentId);
            goals.add(bean);

            if (node.has("goals") && node.path("goals").isArray())
                goals.addAll(this.getGoalBeans(node.path("goals"), bean.getModelId()));
        }

        return goals;
    }

    private GoalBean getGoalBean(JsonNode node, ModelId parentId) {
        GoalBean bean = GoalFactory.getInstance().createOriginalBean();

        bean.getModelReferenceIds().put("organitask", node.path("id").asText());
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.path("update_date").asLong()));
        bean.setParent(parentId);
        bean.setTitle(node.path("title").asText());
        bean.setLevel(OrganiTaskTranslations.translateGoalLevel(node.path("level").asText()));

        return bean;
    }

}
