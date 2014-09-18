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
import com.leclercb.taskunifier.api.models.beans.LocationBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallEditLocation extends AbstractCallLocation {
	
	public void editLocation(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			Location location) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		CheckUtils.isNotNull(location);
		
		if (location.getModelReferenceId("toodledo") == null)
			throw new IllegalArgumentException("You cannot edit a new location");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair(
				"id",
				location.getModelReferenceId("toodledo")));
		params.add(new BasicNameValuePair("name", location.getTitle()));
		params.add(new BasicNameValuePair(
				"description",
				location.getDescription()));
		params.add(new BasicNameValuePair("lat", location.getLatitude() + ""));
		params.add(new BasicNameValuePair("lon", location.getLongitude() + ""));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/locations/edit.php", params);
		
		LocationBean[] locations = this.getResponseMessage(
				location,
				accountInfo,
				content);
		
		if (locations.length != 1)
			throw new SynchronizerException(false, "Edition of location "
					+ location.getModelId()
					+ " has failed");
	}
	
}
