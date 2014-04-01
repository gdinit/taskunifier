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
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallAddGoal extends AbstractCallGoal {
	
	public String addGoal(ToodledoAccountInfo accountInfo, String key, Goal goal)
			throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(goal);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("name", goal.getTitle()));
		params.add(new BasicNameValuePair(
				"level",
				ToodledoTranslations.translateGoalLevel(goal.getLevel())));
		
		if (goal.getContributes() != null
				&& goal.getContributes().getModelReferenceId("toodledo") != null)
			params.add(new BasicNameValuePair(
					"contributes",
					goal.getContributes().getModelReferenceId("toodledo")));
		else
			params.add(new BasicNameValuePair("contributes", "0"));
		
		params.add(new BasicNameValuePair(
				"archived",
				(goal.isArchived() ? "1" : "0")));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/2/goals/add.php", params);
		
		return this.getResponseMessage(goal, accountInfo, content)[0].getModelReferenceIds().get(
				"toodledo");
	}
	
}
