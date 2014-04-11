/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallAddNote extends AbstractCallNote {

    public NoteBean addNote(String accessToken, Note note) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(note);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("title", note.getTitle());

        if (note.getFolder() != null)
            node.put("folder_id", note.getFolder().getModelReferenceId("organitask"));

        node.put("note", note.getNote());

        String content = super.call("POST", "/notes", accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
