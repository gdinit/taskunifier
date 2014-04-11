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

final class CallEditNote extends AbstractCallNote {

    public NoteBean editNote(String accessToken, Note note, boolean syncParent) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(note);

        if (note.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new note");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("title", note.getTitle());

        if (note.getFolder() != null)
            node.put("folder_id", note.getFolder().getModelReferenceId("organitask"));

        node.put("note", note.getNote());

        if (syncParent)
            node.put("parent_id", note.getModelReferenceId("organitask"));

        String content = super.call("PUT", "/notes/" + note.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

    public NoteBean editNoteParent(String accessToken, Note note) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(note);

        if (note.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new note");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("parent_id", note.getModelReferenceId("organitask"));

        String content = super.call("PUT", "/notes/" + note.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
