/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googletasks;

import java.util.Properties;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.Synchronizer;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

/**
 * This plugin will publish your tasks to Google Tasks.
 * 
 * Before using the plugin, you have to initialize it.
 * You have to set the values for the client id and
 * the client secret.
 * 
 * GoogleTasksApi.getInstance().setClientId("");
 * GoogleTasksApi.getInstance().setClientSecret("");
 * 
 * http://code.google.com/intl/fr-FR/apis/tasks/v1/using.html
 * 
 * @author leclercb
 */
public class GoogleTasksApi extends SynchronizerApi {
	
	private static GoogleTasksApi INSTANCE;
	
	public static final GoogleTasksApi getInstance() {
		if (INSTANCE == null)
			INSTANCE = new GoogleTasksApi();
		
		return INSTANCE;
	}
	
	private String applicationName;
	private String clientId;
	private String clientSecret;
	
	public GoogleTasksApi() {
		super("GOOGLE_TASKS", "Google Tasks", "http://www.google.com");
		
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
		return new GoogleTasksConnection();
	}
	
	@Override
	public Synchronizer getSynchronizer(
			Properties properties,
			Connection connection) throws SynchronizerException {
		CheckUtils.isNotNull(connection);
		
		if (!(connection instanceof GoogleTasksConnection))
			throw new IllegalArgumentException(
					"Connection must be of type GoogleTasksConnection");
		
		return new GoogleTasksSynchronizer((GoogleTasksConnection) connection);
	}
	
	@Override
	public void resetConnectionParameters(Properties properties) {
		PropertyMap p = new PropertyMap(properties);
		
		p.setStringProperty("plugin.googletasks.access_token", null);
		p.setStringProperty("plugin.googletasks.refresh_token", null);
	}
	
	@Override
	public void resetSynchronizerParameters(Properties properties) {
		
	}
	
}
