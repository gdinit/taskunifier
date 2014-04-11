/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.beans.FolderBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallEditFolder extends AbstractCallFolder {

    public FolderBean editFolder(String accessToken, Folder folder, boolean syncParent) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(folder);

        if (folder.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new folder");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("title", folder.getTitle());

        if (syncParent)
            node.put("parent_id", folder.getModelReferenceId("organitask"));

        String content = super.call("PUT", "/folders/" + folder.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

    public FolderBean editFolderParent(String accessToken, Folder folder) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(folder);

        if (folder.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new folder");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("parent_id", folder.getModelReferenceId("organitask"));

        String content = super.call("PUT", "/folders/" + folder.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}