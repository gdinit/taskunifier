/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.beans.ContextBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

final class CallGetContexts extends AbstractCallContext {

    public ContextBean[] getContexts(String accessToken, Calendar updatedAfter)
            throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        if (updatedAfter != null)
            params.add(new BasicNameValuePair("update_date", OrganiTaskTranslations.translateUTCDate(updatedAfter) + ""));

        String content = super.callGet("/contexts", params);

        return this.getResponseMessage(content);
    }

}
