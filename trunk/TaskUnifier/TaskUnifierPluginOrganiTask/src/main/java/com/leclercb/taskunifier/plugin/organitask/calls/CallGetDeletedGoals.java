/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.beans.GoalBean;
import com.leclercb.taskunifier.api.models.beans.ModelBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

final class CallGetDeletedGoals extends AbstractCallDeleted {

    public ModelBean[] getDeletedGoals(String accessToken, Calendar deletedAfter)
            throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(new BasicNameValuePair("deleted", "true"));

        if (deletedAfter != null)
            params.add(new BasicNameValuePair("deletion_date", OrganiTaskTranslations.translateUTCDate(deletedAfter) + ""));

        String content = super.callGet("/goals", params);

        return this.getResponseMessage(ModelType.GOAL, content);
    }

}
