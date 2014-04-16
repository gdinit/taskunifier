/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.beans.ContextBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.gui.api.models.beans.GuiModelBean;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class AbstractCallContext extends AbstractCall {

    /**
     * Example: [{"team_id":4,"parent_id":null,"id":20,"creation_date":"1396878407","update_date":"1396878407","title":"@Work","color":"FF0000","contexts":[]},{"team_id":4,"parent_id":null,"id":19,"creation_date":"1396878407","update_date":"1396878407","title":"@Home","color":"0000FF","contexts":[]}]
     *
     * @param content
     * @return
     * @throws SynchronizerException
     */
    protected ContextBean[] getResponseMessage(String content) throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            List<ContextBean> contexts = new ArrayList<ContextBean>();
            contexts = this.getContextBeans(root);

            return contexts.toArray(new ContextBean[0]);
        } catch (Exception e) {
            throw new SynchronizerParsingException(
                    "Error while parsing response ("
                            + this.getClass().getName()
                            + ")",
                    content,
                    e);
        }
    }

    private List<ContextBean> getContextBeans(JsonNode node) {
        List<ContextBean> contexts = new ArrayList<ContextBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                ContextBean bean = this.getContextBean(item);
                contexts.add(bean);
                contexts.addAll(this.getContextBeans(item.path("contexts")));
            }
        } else {
            ContextBean bean = this.getContextBean(node);
            contexts.add(bean);

            if (node.has("contexts") && node.path("contexts").isArray())
                contexts.addAll(this.getContextBeans(node.path("contexts")));
        }

        return contexts;
    }

    private ContextBean getContextBean(JsonNode node) {
        ContextBean bean = ContextFactory.getInstance().createOriginalBean();

        bean.getModelReferenceIds().put("organitask", this.getNodeTextValue(node.path("id")));
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelCreationDate(OrganiTaskTranslations.translateUTCDate(node.path("creation_date").asLong()));
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.path("update_date").asLong()));
        bean.setParent(OrganiTaskTranslations.getModelOrCreateShell(
                ModelType.CONTEXT,
                this.getNodeTextValue(node.path("parent_id"))));
        bean.setTitle(this.getNodeTextValue(node.path("title")));

        if (bean instanceof GuiModelBean) {
            ((GuiModelBean) bean).setColor(OrganiTaskTranslations.translateColor(this.getNodeTextValue(node.path("color"))));
        }

        return bean;
    }

}
