/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo;

import java.util.Calendar;
import java.util.Properties;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerConnectionException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoAccountInfo;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoStatement;

public class ToodledoConnection implements Connection {
	
	private ToodledoStatement statement;
	private ToodledoAccountInfo accountInfo;
	
	private boolean connected;

    private String code;

    private String accessToken;
    private String refreshToken;

    ToodledoConnection() {
        this.connected = false;

        this.code = null;

        this.accessToken = null;
        this.refreshToken = null;

        this.statement = new ToodledoStatement(this);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public ToodledoStatement getStatement() {
        return this.statement;
    }

    public ToodledoAccountInfo getAccountInfo() {
        return this.accountInfo;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }
	
	@Override
	public void connect() throws SynchronizerException {
		if (this.connected)
			return;
		
		if (this.userId == null) {
			this.userId = ToodledoStatement.getUserId(this.email, this.password);
		}
		
		double minuteDiff = 0;
		if (this.token != null && this.tokenCreationDate != null) {
			long milliSeconds1 = Calendar.getInstance().getTimeInMillis();
			long milliSeconds2 = this.tokenCreationDate.getTimeInMillis();
			long diff = milliSeconds1 - milliSeconds2;
			minuteDiff = diff / (60 * 1000.0);
		}
		
		if (this.token == null || minuteDiff > 240) {
			this.token = ToodledoStatement.getToken(this.userId);
			this.tokenCreationDate = Calendar.getInstance();
		}
		
		this.key = ToodledoStatement.getKey(this.password, this.token);
		
		try {
			this.accountInfo = this.statement.getAccountInfo();
		} catch (SynchronizerConnectionException e) {
			this.userId = ToodledoStatement.getUserId(this.email, this.password);
			this.token = ToodledoStatement.getToken(this.userId);
			this.tokenCreationDate = Calendar.getInstance();
			this.key = ToodledoStatement.getKey(this.password, this.token);
			this.accountInfo = this.statement.getAccountInfo();
		}
		
		this.connected = true;
	}
	
	@Override
	public void disconnect() {
		this.connected = false;
	}
	
	public void reconnect() throws SynchronizerException {
		if (!this.connected)
			return;
		
		this.disconnect();
		this.connect();
	}
	
	@Override
	public void loadParameters(Properties properties) {
		PropertyMap p = new PropertyMap(properties);
		
		this.userId = p.getStringProperty("toodledo.connection.userid");
		this.token = p.getStringProperty("toodledo.connection.token");
		this.tokenCreationDate = p.getCalendarProperty("toodledo.connection.token_creation_date");
	}
	
	@Override
	public void saveParameters(Properties properties) {
		PropertyMap p = new PropertyMap(properties);
		
		p.setStringProperty("toodledo.connection.userid", this.getUserId());
		p.setStringProperty("toodledo.connection.token", this.getToken());
		p.setCalendarProperty(
				"toodledo.connection.token_creation_date",
				this.getTokenCreationDate());
	}
	
}
