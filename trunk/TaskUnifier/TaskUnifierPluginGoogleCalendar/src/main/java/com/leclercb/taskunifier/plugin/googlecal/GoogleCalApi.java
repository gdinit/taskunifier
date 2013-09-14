/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal;

import java.util.Properties;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.Synchronizer;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

/**
 * This plugin will publish your tasks to Google Calendar.
 * 
 * Before using the plugin, you have to initialize it.
 * You have to set the values for the client id and
 * the client secret.
 * 
 * GoogleCalApi.getInstance().setClientId("");
 * GoogleCalApi.getInstance().setClientSecret("");
 * 
 * http://code.google.com/intl/fr-FR/apis/calendar/v3/using.html
 * 
 * @author leclercb
 */
public class GoogleCalApi extends SynchronizerApi {
	
	private static GoogleCalApi INSTANCE;
	
	public static final GoogleCalApi getInstance() {
		if (INSTANCE == null)
			INSTANCE = new GoogleCalApi();
		
		return INSTANCE;
	}
	
	private String applicationName;
	private String clientId;
	private String clientSecret;
	
	public GoogleCalApi() {
		super(
				"GOOGLE_CALENDAR",
				"Google Calendar",
				"http://www.google.com/calendar");
		
		this.setApplicationName("");
		this.setClientId("");
		this.setClientSecret("");
	}
	
	public String getApplicationName() {
		return this.applicationName;
	}
	
	public void setApplicationName(String applicationName) {
		CheckUtils.isNotNull(applicationName);
		this.applicationName = applicationName;
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public void setClientId(String clientId) {
		CheckUtils.isNotNull(clientId);
		this.clientId = clientId;
	}
	
	public String getClientSecret() {
		return this.clientSecret;
	}
	
	public void setClientSecret(String clientSecret) {
		CheckUtils.isNotNull(clientSecret);
		this.clientSecret = clientSecret;
	}
	
	@Override
	public void flagAsNew(Model model) {
		
	}
	
	@Override
	public Connection getConnection(Properties properties)
			throws SynchronizerException {
		return new GoogleCalConnection();
	}
	
	@Override
	public Synchronizer getSynchronizer(
			Properties properties,
			Connection connection) throws SynchronizerException {
		CheckUtils.isNotNull(connection);
		
		if (!(connection instanceof GoogleCalConnection))
			throw new IllegalArgumentException(
					"Connection must be of type GoogleCalConnection");
		
		return new GoogleCalSynchronizer((GoogleCalConnection) connection);
	}
	
	@Override
	public void resetConnectionParameters(Properties properties) {
		PropertyMap p = new PropertyMap(properties);
		
		p.setStringProperty("plugin.googlecal.access_token", null);
		p.setStringProperty("plugin.googlecal.refresh_token", null);
	}
	
	@Override
	public void resetSynchronizerParameters(Properties properties) {
		
	}
	
}
