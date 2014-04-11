/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls.exc;

import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerConnectionException;

public class OrganiTaskConnectionException extends SynchronizerConnectionException {

    public OrganiTaskConnectionException(
            boolean expected,
            String apiId,
            String code,
            String message) {
        super(expected, apiId, code, message);
    }

    public OrganiTaskConnectionException(
            boolean expected,
            String apiId,
            String code,
            String message,
            Throwable throwable) {
        super(expected, apiId, code, message, throwable);
    }

}
