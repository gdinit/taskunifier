/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal;

import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.commons.api.utils.HttpUtils;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerConnectionException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerNotConnectedException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerSettingsException;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.utils.DesktopUtils;
import com.leclercb.taskunifier.plugin.googlecal.translations.PluginTranslations;

public class GoogleCalConnection implements Connection {
	
	private boolean connected;
	
	private Calendar service;
	
	private String code;
	
	private String accessToken;
	private String refreshToken;
	
	GoogleCalConnection() {
		this.connected = false;
		this.service = null;
		
		this.code = null;
		
		this.accessToken = null;
		this.refreshToken = null;
	}
	
	public Calendar getService() {
		return this.service;
	}
	
	@Override
	public boolean isConnected() {
		return this.connected;
	}
	
	@Override
	public void connect() throws SynchronizerException {
		String settingsEmail = PluginApi.getUserSettings().getStringProperty(
				"plugin.googlecal.email");
		
		if (settingsEmail == null || settingsEmail.length() == 0) {
			throw new SynchronizerSettingsException(
					true,
					GoogleCalApi.getInstance().getApiId(),
					null,
					PluginTranslations.getString("error.no_email"));
		}
		
		try {
			HttpTransport httpTransport = new NetHttpTransport();
			JacksonFactory jsonFactory = new JacksonFactory();
			
			String clientId = GoogleCalApi.getInstance().getClientId();
			String clientSecret = GoogleCalApi.getInstance().getClientSecret();
			
			String redirectUri = "urn:ietf:wg:oauth:2.0:oob";
			String[] scopes = new String[] {
					"https://www.googleapis.com/auth/calendar",
					"https://www.googleapis.com/auth/userinfo.email" };
			
			Credential credential = null;
			
			if (this.accessToken == null || this.refreshToken == null) {
				GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
						httpTransport,
						jsonFactory,
						clientId,
						clientSecret,
						Arrays.asList(scopes)).setAccessType("offline").setApprovalPrompt(
						"auto").build();
				
				final String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(
						redirectUri).build();
				
				SwingUtilities.invokeAndWait(new Runnable() {
					
					@Override
					public void run() {
						DesktopUtils.browse(authorizationUrl);
						
						GoogleCalConnection.this.code = JOptionPane.showInputDialog(
								PluginApi.getCurrentWindow(),
								PluginTranslations.getString("connection.code.message"),
								PluginTranslations.getString("connection.code.title"),
								JOptionPane.QUESTION_MESSAGE);
					}
					
				});
				
				if (this.code == null)
					throw new SynchronizerConnectionException(
							true,
							GoogleCalApi.getInstance().getApiId(),
							null,
							PluginApi.getTranslation("synchronizer.cancelled_by_user"));
				
				GoogleTokenResponse response = flow.newTokenRequest(this.code).setRedirectUri(
						redirectUri).execute();
				
				credential = flow.createAndStoreCredential(response, null);
				
				this.accessToken = credential.getAccessToken();
				this.refreshToken = credential.getRefreshToken();
			} else {
				credential = new GoogleCredential.Builder().setClientSecrets(
						clientId,
						clientSecret).setJsonFactory(jsonFactory).setTransport(
						httpTransport).build().setRefreshToken(
						this.refreshToken).setAccessToken(this.accessToken);
			}
			
			// Get Google Email
			HttpResponse response = HttpUtils.getHttpGetResponse(
					new URI(
							"https://www.googleapis.com/userinfo/email?alt=json&access_token="
									+ credential.getAccessToken()),
					GoogleCalApi.getInstance().getProxyHost(),
					GoogleCalApi.getInstance().getProxyPort(),
					GoogleCalApi.getInstance().getProxyUsername(),
					GoogleCalApi.getInstance().getProxyPassword());
			
			if (response.isSuccessfull()) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode root = mapper.readTree(response.getContent());
				String email = root.path("data").path("email").textValue();
				
				if (!EqualsUtils.equalsStringIgnoreCase(email, settingsEmail)) {
					throw new SynchronizerSettingsException(
							true,
							GoogleCalApi.getInstance().getApiId(),
							null,
							PluginTranslations.getString("error.invalid_google_email"));
				}
			}
			
			Calendar.Builder builder = new Calendar.Builder(
					httpTransport,
					jsonFactory,
					credential);
			builder.setApplicationName(GoogleCalApi.getInstance().getApplicationName());
			
			this.service = builder.build();
			
			this.connected = true;
		} catch (SynchronizerException e) {
			this.service = null;
			this.connected = false;
			
			throw e;
		} catch (NoRouteToHostException e) {
			throw new SynchronizerNotConnectedException(
					true,
					e.getMessage(),
					PluginApi.getTranslation("error.not_connected_internet"));
		} catch (UnknownHostException e) {
			throw new SynchronizerNotConnectedException(
					true,
					e.getMessage(),
					PluginApi.getTranslation("error.not_connected_internet"));
		} catch (Exception e) {
			this.service = null;
			this.connected = false;
			
			throw new SynchronizerConnectionException(
					false,
					GoogleCalApi.getInstance().getApiId(),
					null,
					e.getMessage(),
					e);
		}
	}
	
	@Override
	public void disconnect() {
		this.service = null;
		this.connected = false;
	}
	
	@Override
	public void loadParameters(Properties properties) {
		PropertyMap p = new PropertyMap(properties);
		
		this.accessToken = p.getStringProperty("plugin.googlecal.access_token");
		this.refreshToken = p.getStringProperty("plugin.googlecal.refresh_token");
	}
	
	@Override
	public void saveParameters(Properties properties) {
		PropertyMap p = new PropertyMap(properties);
		
		p.setStringProperty("plugin.googlecal.access_token", this.accessToken);
		p.setStringProperty("plugin.googlecal.refresh_token", this.refreshToken);
	}
	
}
