/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.TaskStatus;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallDeleteTaskStatus extends AbstractCallTaskStatus {

    public void deleteTaskStatus(String accessToken, TaskStatus taskStatus) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(taskStatus);

        super.call("DELETE", "/task_statuses/" + taskStatus.getModelReferenceId("organitask"), accessToken, null);
    }

}
