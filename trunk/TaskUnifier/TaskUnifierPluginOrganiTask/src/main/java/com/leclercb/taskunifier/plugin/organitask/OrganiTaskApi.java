/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask;

import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoStatement;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoSettingsException;

/**
 * This plugin will synchronize your contexts, folders,
 * goals, locations, notes and tasks with OrganiTask.
 * 
 * Before using the plugin, you have to initialize it.
 * You have to set the values for the application id,
 * the version and the api key.
 * 
 * OrganiTaskApi.getInstance().setClientId("");
 * OrganiTaskApi.getInstance().setClientRandomId("");
 * OrganiTaskApi.getInstance().setClientSecret("");
 * 
 * http://www.organitask.com/web/api
 * 
 * @author leclercb
 */
public final class OrganiTaskApi extends SynchronizerApi {
	
	private static OrganiTaskApi INSTANCE;
	
	public static final OrganiTaskApi getInstance() {
		if (INSTANCE == null)
			INSTANCE = new OrganiTaskApi();
		
		return INSTANCE;
	}
	
	private String clientId;
	private String clientRandomId;
	private String clientSecret;
	
	private OrganiTaskApi() {
		super("ORGANITASK", "OrganiTask", "http://www.organitask.com");
		
		this.setClientId("");
        this.setClientRandomId("");
        this.setClientSecret("");
	}

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        CheckUtils.isNotNull(clientId);
        this.clientId = clientId;
    }

    public String getClientRandomId() {
        return clientRandomId;
    }

    public void setClientRandomId(String clientRandomId) {
        CheckUtils.isNotNull(clientRandomId);
        this.clientRandomId = clientRandomId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        CheckUtils.isNotNull(clientSecret);
        this.clientSecret = clientSecret;
    }

    public void createAccount(String email, String password)
			throws SynchronizerException {
		ToodledoStatement.createAccount(email, password);
	}
	
	@Override
	public void flagAsNew(Model model) {
		model.removeModelReferenceId("organitask");
	}
	
	@Override
	public OrganiTaskConnection getConnection(Properties properties)
			throws SynchronizerException {
		CheckUtils.isNotNull(properties);
		PropertyMap p = new PropertyMap(properties);
		
		if (p.getStringProperty("toodledo.email") == null)
			throw new ToodledoSettingsException(
					null,
					ToodledoErrors.ERROR_ACCOUNT_10);
		
		if (p.getStringProperty("toodledo.password") == null)
			throw new ToodledoSettingsException(
					null,
					ToodledoErrors.ERROR_ACCOUNT_11);
		
		return new OrganiTaskConnection(
				p.getStringProperty("toodledo.email"),
				p.getStringProperty("toodledo.password"));
	}
	
	@Override
	public OrganiTaskSynchronizer getSynchronizer(
			Properties properties,
			Connection connection) throws SynchronizerException {
		CheckUtils.isNotNull(connection);
		
		if (!(connection instanceof OrganiTaskConnection))
			throw new IllegalArgumentException(
					"Connection must be of type OrganiTaskConnection");
		
		return new OrganiTaskSynchronizer((OrganiTaskConnection) connection);
	}
	
	@Override
	public void resetConnectionParameters(Properties properties) {
		CheckUtils.isNotNull(properties);
		PropertyMap p = new PropertyMap(properties);
		
		p.setStringProperty("toodledo.connection.userid", null);
		p.setStringProperty("toodledo.connection.token", null);
		p.setCalendarProperty("toodledo.connection.token_creation_date", null);
	}
	
	@Override
	public void resetSynchronizerParameters(Properties properties) {
		CheckUtils.isNotNull(properties);
		PropertyMap p = new PropertyMap(properties);
		
		p.setCalendarProperty("organitask.synchronizer.last_context_edit", null);
		p.setCalendarProperty("organitask.synchronizer.last_folder_edit", null);
		p.setCalendarProperty("organitask.synchronizer.last_goal_edit", null);
		p.setCalendarProperty("organitask.synchronizer.last_location_edit", null);
		p.setCalendarProperty("organitask.synchronizer.last_note_edit", null);
		p.setCalendarProperty("organitask.synchronizer.last_note_delete", null);
		p.setCalendarProperty("organitask.synchronizer.last_task_edit", null);
		p.setCalendarProperty("organitask.synchronizer.last_task_delete", null);
	}
	
}
