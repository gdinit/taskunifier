/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallDeleteContext extends AbstractCallContext {

    public void deleteContext(String accessToken, Context context) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(context);

        super.call("DELETE", "/contexts/" + context.getModelReferenceId("organitask"), accessToken, null);
    }

}
