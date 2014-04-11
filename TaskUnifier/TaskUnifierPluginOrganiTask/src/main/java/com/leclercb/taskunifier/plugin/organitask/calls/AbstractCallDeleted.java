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
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.beans.ModelBean;
import com.leclercb.taskunifier.api.models.utils.ModelFactoryUtils;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class AbstractCallDeleted extends AbstractCall {

    /**
     * Example: [{"team_id":2001,"id":1,"deletion_date":"1397204394"}]
     *
     * @param content
     * @return
     * @throws com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException
     */
    protected ModelBean[] getResponseMessage(ModelType modelType, String content) throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            List<ModelBean> beans = new ArrayList<ModelBean>();
            beans = this.getModelBeans(modelType, root);

            return beans.toArray(new ModelBean[0]);
        } catch (Exception e) {
            throw new SynchronizerParsingException(
                    "Error while parsing response ("
                            + this.getClass().getName()
                            + ")",
                    content,
                    e);
        }
    }

    private List<ModelBean> getModelBeans(ModelType modelType, JsonNode node) {
        List<ModelBean> beans = new ArrayList<ModelBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                ModelBean bean = this.getModelBean(modelType, item);
                beans.add(bean);
            }
        } else {
            ModelBean bean = this.getModelBean(modelType, node);
            beans.add(bean);
        }

        return beans;
    }

    private ModelBean getModelBean(ModelType modelType, JsonNode node) {
        ModelBean bean = ModelFactoryUtils.getFactory(modelType).createOriginalBean();

        bean.getModelReferenceIds().put("organitask", node.path("id").textValue());
        bean.setModelStatus(ModelStatus.DELETED);
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.path("deletion_date").longValue()));

        return bean;
    }

}
