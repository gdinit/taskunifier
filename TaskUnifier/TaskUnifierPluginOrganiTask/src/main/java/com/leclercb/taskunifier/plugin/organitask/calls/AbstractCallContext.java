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
import com.leclercb.taskunifier.api.models.beans.ContextBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;

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
            contexts = this.getContextBeans(root, null);

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

    private List<ContextBean> getContextBeans(JsonNode node, ModelId parentId) {
        List<ContextBean> contexts = new ArrayList<ContextBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                ContextBean bean = this.getContextBean(item, parentId);
                contexts.add(bean);
                contexts.addAll(this.getContextBeans(item.path("contexts"), bean.getModelId()));
            }
        } else {
            ContextBean bean = this.getContextBean(node, parentId);
            contexts.add(bean);
            contexts.addAll(this.getContextBeans(node.path("contexts"), bean.getModelId()));
        }

        return contexts;
    }

    private ContextBean getContextBean(JsonNode node, ModelId parentId) {
        ContextBean bean = ContextFactory.getInstance().createOriginalBean();

        bean.setModelId(new ModelId());
        bean.getModelReferenceIds().put("organitask", node.path("id").textValue());
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.path("update_date").longValue()));
        bean.setParent(parentId);
        bean.setTitle(node.path("title").textValue());

        return bean;
    }

}
