/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

final class CallGetTasks extends AbstractCallTask {

    public TaskBean[] getTasks(String accessToken, Calendar updatedAfter)
            throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(new BasicNameValuePair("notes", "true"));
        params.add(new BasicNameValuePair("flat", "true"));

        if (updatedAfter != null)
            params.add(new BasicNameValuePair("update_date", OrganiTaskTranslations.translateUTCDate(updatedAfter) + ""));

        String content = super.callGet("/tasks", params);

        return this.getResponseMessage(content);
    }

}
