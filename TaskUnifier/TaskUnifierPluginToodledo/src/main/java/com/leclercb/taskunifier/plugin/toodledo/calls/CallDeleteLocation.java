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
import com.leclercb.taskunifier.api.models.Location;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerApiException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

final class CallDeleteLocation extends AbstractCallDelete {
	
	public void deleteLocation(
			ToodledoAccountInfo accountInfo,
			String key,
			Location location) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(location);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair(
				"id",
				location.getModelReferenceId("toodledo")));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(
				scheme,
				"/3/locations/delete.php",
				params);
		
		String id = null;
		
		try {
			id = this.getResponseMessage(
					location,
					ToodledoErrorType.LOCATION,
					content);
		} catch (SynchronizerApiException exc) {
			if ("4".equals(exc.getCode()))
				return;
			
			throw exc;
		}
		
		if (!location.getModelReferenceId("toodledo").equals(id))
			throw new SynchronizerException(false, "Deletion of location "
					+ location.getModelId()
					+ " has failed");
	}
	
}
