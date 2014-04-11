/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallDeleteNote extends AbstractCallNote {

    public void deleteNote(String accessToken, Note note) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(note);

        super.call("DELETE", "/notes/" + note.getModelReferenceId("organitask"), accessToken, null);
    }

}
