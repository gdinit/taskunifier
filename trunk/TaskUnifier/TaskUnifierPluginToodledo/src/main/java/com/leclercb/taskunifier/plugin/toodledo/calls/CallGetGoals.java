/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.beans.GoalBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallGetGoals extends AbstractCallGoal {
	
	public GoalBean[] getGoals(ToodledoAccountInfo accountInfo, String key)
			throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(accountInfo);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/goals/get.php", params);
		
		return this.getResponseMessage(null, accountInfo, content);
	}
	
}
