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
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class AbstractCallNote extends AbstractCall {

    /**
     * Example: [{"team_id":1,"folder_id":null,"id":1,"creation_date":"1397201202","update_date":"1397201202","title":"My Note","star":false,"tags":"","note":"Content of the note"}]
     *
     * @param content
     * @return
     * @throws SynchronizerException
     */
    protected NoteBean[] getResponseMessage(String content) throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            List<NoteBean> notes = new ArrayList<NoteBean>();
            notes = this.getNoteBeans(root);

            return notes.toArray(new NoteBean[0]);
        } catch (Exception e) {
            throw new SynchronizerParsingException(
                    "Error while parsing response ("
                            + this.getClass().getName()
                            + ")",
                    content,
                    e);
        }
    }

    private List<NoteBean> getNoteBeans(JsonNode node) {
        List<NoteBean> notes = new ArrayList<NoteBean>();

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();

            while (iterator.hasNext()) {
                JsonNode item = iterator.next();

                NoteBean bean = this.getNoteBean(item);
                notes.add(bean);
            }
        } else {
            NoteBean bean = this.getNoteBean(node);
            notes.add(bean);
        }

        return notes;
    }

    private NoteBean getNoteBean(JsonNode node) {
        NoteBean bean = NoteFactory.getInstance().createOriginalBean();

        bean.getModelReferenceIds().put("organitask", this.getNodeTextValue(node.get("id")));
        bean.setModelStatus(ModelStatus.LOADED);
        bean.setModelCreationDate(OrganiTaskTranslations.translateUTCDate(node.get("creation_date").asLong()));
        bean.setModelUpdateDate(OrganiTaskTranslations.translateUTCDate(node.get("update_date").asLong()));
        bean.setTitle(this.getNodeTextValue(node.get("title")));
        bean.setFolder(OrganiTaskTranslations.getModelOrCreateShell(
                ModelType.FOLDER,
                this.getNodeTextValue(node.get("folder_id"))));
        bean.setNote(this.getNodeTextValue(node.get("note")));

        return bean;
    }

}
