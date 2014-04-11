/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.beans.FolderBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class AbstractCallFolder extends AbstractCall {

    /**
     * Example: [{"team_id":1,"parent_id":null,"id":1,"creation_date":"1395847564","update_date":"1395847564","title":"Getting Started","color":"00FF00","folders":[]}]
     *
     * @param content
     * @return
     * @throws SynchronizerException
     */
    protected FolderBean[] getResponseMessage(String content) throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            List<FolderBean> folders = new ArrayList<FolderBean>();
            folders = this.getFolderBeans(root, null);

            return folders.toArray(new FolderBean[0]);
        } catch (Exception e) {
            throw new SynchronizerParsingException(
                    "Error while parsing response ("
                            + this.getClass().getName()
                            + ")",
                    content,
                    e);
        }
    }

    private List<FolderBean> getFolderBeans(JsonNode node, ModelId parentId) {
        List<FolderBean> folders = new ArrayList<FolderBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                FolderBean bean = this.getFolderBean(item, parentId);
                folders.add(bean);
                folders.addAll(this.getFolderBeans(item.path("folders"), bean.getModelId()));
            }
        } else {
            FolderBean bean = this.getFolderBean(node, parentId);
            folders.add(bean);
            folders.addAll(this.getFolderBeans(node.path("folders"), bean.getModelId()));
        }

        return folders;
    }

    private FolderBean getFolderBean(JsonNode node, ModelId parentId) {
        FolderBean bean = FolderFactory.getInstance().createOriginalBean();

        bean.setModelId(new ModelId());
        bean.getModelReferenceIds().put("organitask", node.path("id").textValue());
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.path("update_date").longValue()));
        bean.setParent(parentId);
        bean.setTitle(node.path("title").textValue());

        return bean;
    }

}
