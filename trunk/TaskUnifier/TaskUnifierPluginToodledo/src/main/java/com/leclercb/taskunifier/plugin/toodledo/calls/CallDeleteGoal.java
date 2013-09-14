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
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerApiException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

final class CallDeleteGoal extends AbstractCallDelete {
	
	public void deleteGoal(
			ToodledoAccountInfo accountInfo,
			String key,
			Goal goal) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(goal);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair(
				"id",
				goal.getModelReferenceId("toodledo")));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/2/goals/delete.php", params);
		
		String id = null;
		
		try {
			id = this.getResponseMessage(goal, ToodledoErrorType.GOAL, content);
		} catch (SynchronizerApiException exc) {
			if ("4".equals(exc.getCode()))
				return;
			
			throw exc;
		}
		
		if (!goal.getModelReferenceId("toodledo").equals(id))
			throw new SynchronizerException(false, "Deletion of goal "
					+ goal.getModelId()
					+ " has failed");
	}
	
}
