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
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerApiException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

final class CallDeleteContext extends AbstractCallDelete {
	
	public void deleteContext(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			Context context) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		CheckUtils.isNotNull(context);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair(
				"id",
				context.getModelReferenceId("toodledo")));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/contexts/delete.php", params);
		
		String id = null;
		
		try {
			id = this.getResponseMessage(
					context,
					ToodledoErrorType.CONTEXT,
					content);
		} catch (SynchronizerApiException exc) {
			if ("4".equals(exc.getCode()))
				return;
			
			throw exc;
		}
		
		if (!context.getModelReferenceId("toodledo").equals(id))
			throw new SynchronizerException(false, "Deletion of context "
					+ context.getModelId()
					+ " has failed");
	}
	
}
