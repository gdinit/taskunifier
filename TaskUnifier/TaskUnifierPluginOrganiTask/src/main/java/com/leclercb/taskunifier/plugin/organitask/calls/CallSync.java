/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import org.apache.http.NameValuePair;

import java.util.ArrayList;

final class CallSync extends AbstractCall {

    public void syncStart() throws SynchronizerException {
        super.call("POST", "/sync/start", new ArrayList<NameValuePair>(), null);
    }

    public void syncEnd() throws SynchronizerException {
        super.call("POST", "/sync/end", new ArrayList<NameValuePair>(), null);
    }

}
