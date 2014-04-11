/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallDeleteTask extends AbstractCallTask {

    public void deleteTask(String accessToken, Task task) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(task);

        super.call("DELETE", "/tasks/" + task.getModelReferenceId("organitask"), accessToken, null);
    }

}
