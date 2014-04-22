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
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.beans.GoalBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.gui.api.models.beans.GuiModelBean;

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
            goals = this.getGoalBeans(root);

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

    private List<GoalBean> getGoalBeans(JsonNode node) {
        List<GoalBean> goals = new ArrayList<GoalBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                GoalBean bean = this.getGoalBean(item);
                goals.add(bean);
                goals.addAll(this.getGoalBeans(item.get("goals")));
            }
        } else {
            GoalBean bean = this.getGoalBean(node);
            goals.add(bean);

            if (node.has("goals") && node.get("goals").isArray())
                goals.addAll(this.getGoalBeans(node.get("goals")));
        }

        return goals;
    }

    private GoalBean getGoalBean(JsonNode node) {
        GoalBean bean = GoalFactory.getInstance().createBean();

        bean.getModelReferenceIds().put("organitask", this.getNodeTextValue(node.get("id")));
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelCreationDate(OrganiTaskTranslations.translateUTCDate(node.get("creation_date").asLong()));
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.get("update_date").asLong()));
        bean.setParent(OrganiTaskTranslations.getModelOrCreateShell(
                ModelType.GOAL,
                this.getNodeTextValue(node.get("parent_id"))));
        bean.setTitle(this.getNodeTextValue(node.get("title")));
        bean.setLevel(OrganiTaskTranslations.translateGoalLevel(this.getNodeTextValue(node.get("level"))));

        if (bean instanceof GuiModelBean) {
            ((GuiModelBean) bean).setColor(OrganiTaskTranslations.translateColor(this.getNodeTextValue(node.get("color"))));
        }

        return bean;
    }

}
