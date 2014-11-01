/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.beans.FolderBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

final class CallGetFolders extends AbstractCallFolder {

    public FolderBean[] getFolders(String accessToken, Calendar updatedAfter)
            throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(new BasicNameValuePair("flat", "true"));

        if (updatedAfter != null)
            params.add(new BasicNameValuePair("update_date", OrganiTaskTranslations.translateUTCDate(updatedAfter) + ""));

        String content = super.callGet("/folders", params);

        return this.getResponseMessage(content);
    }

}
