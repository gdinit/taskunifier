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
import com.leclercb.taskunifier.gui.api.models.GuiModel;

final class CallEditFolder extends AbstractCallFolder {

    public FolderBean editFolder(String accessToken, Folder folder, boolean syncParent) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(folder);

        if (folder.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new folder");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        if (syncParent && folder.getParent() != null && folder.getParent().getModelReferenceId("organitask") != null)
            node.put("parent_id", folder.getParent().getModelReferenceId("organitask"));
        else
            node.put("parent_id", (String) null);

        node.put("title", folder.getTitle());

        if (folder instanceof GuiModel) {
            node.put("color", OrganiTaskTranslations.translateColor(((GuiModel) folder).getColor()));
        }

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

        if (folder.getParent() != null && folder.getParent().getModelReferenceId("organitask") != null)
            node.put("parent_id", folder.getParent().getModelReferenceId("organitask"));
        else
            node.put("parent_id", (String) null);

        String content = super.call("PUT", "/folders/" + folder.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
