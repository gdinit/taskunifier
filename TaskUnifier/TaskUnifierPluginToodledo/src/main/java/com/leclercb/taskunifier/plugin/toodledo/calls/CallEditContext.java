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
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.beans.ContextBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallEditContext extends AbstractCallContext {
	
	public void editContext(
			ToodledoAccountInfo accountInfo,
			String key,
			Context context) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(context);
		
		if (context.getModelReferenceId("toodledo") == null)
			throw new IllegalArgumentException("You cannot edit a new context");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair(
				"id",
				context.getModelReferenceId("toodledo")));
		params.add(new BasicNameValuePair("name", context.getTitle()));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/contexts/edit.php", params);
		
		ContextBean[] contexts = this.getResponseMessage(
				context,
				accountInfo,
				content);
		
		if (contexts.length != 1)
			throw new SynchronizerException(false, "Edition of context "
					+ context.getModelId()
					+ " has failed");
	}
	
}
