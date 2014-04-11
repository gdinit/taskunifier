/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallDeleteFolder extends AbstractCallFolder {

    public void deleteFolder(String accessToken, Folder folder) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(folder);

        super.call("DELETE", "/folders/" + folder.getModelReferenceId("organitask"), accessToken, null);
    }

}
