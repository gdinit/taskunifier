/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallDeleteGoal extends AbstractCallGoal {

    public void deleteGoal(String accessToken, Goal goal) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(goal);

        super.call("DELETE", "/goals/" + goal.getModelReferenceId("organitask"), accessToken, null);
    }

}
