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
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.beans.FolderBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.gui.api.models.beans.GuiModelBean;

import java.awt.*;
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
            folders = this.getFolderBeans(root);

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

    private List<FolderBean> getFolderBeans(JsonNode node) {
        List<FolderBean> folders = new ArrayList<FolderBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                FolderBean bean = this.getFolderBean(item);
                folders.add(bean);
                folders.addAll(this.getFolderBeans(item.path("folders")));
            }
        } else {
            FolderBean bean = this.getFolderBean(node);
            folders.add(bean);

            if (node.has("folders") && node.path("folders").isArray())
                folders.addAll(this.getFolderBeans(node.path("folders")));
        }

        return folders;
    }

    private FolderBean getFolderBean(JsonNode node) {
        FolderBean bean = FolderFactory.getInstance().createOriginalBean();

        bean.getModelReferenceIds().put("organitask", node.path("id").textValue());
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelCreationDate(OrganiTaskTranslations.translateUTCDate(node.path("creation_date").asLong()));
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.path("update_date").asLong()));
        bean.setParent(OrganiTaskTranslations.getModelOrCreateShell(
                ModelType.FOLDER,
                node.path("parent_id").textValue()));
        bean.setTitle(node.path("title").textValue());

        if (bean instanceof GuiModelBean) {
            ((GuiModelBean) bean).setColor(OrganiTaskTranslations.translateColor(node.path("color").textValue()));
        }

        return bean;
    }

}
