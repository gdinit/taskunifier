/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask;

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
import com.google.api.services.tasks.Tasks;
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

public class OrganiTaskConnection implements Connection {

    private boolean connected;

    private Tasks service;

    private String code;

    private String accessToken;
    private String refreshToken;

    OrganiTaskConnection() {
        this.connected = false;
        this.service = null;

        this.code = null;

        this.accessToken = null;
        this.refreshToken = null;
    }

    public Tasks getService() {
        return this.service;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void connect() throws SynchronizerException {
        String settingsEmail = PluginApi.getUserSettings().getStringProperty(
                "plugin.googletasks.email");

        if (settingsEmail == null || settingsEmail.length() == 0) {
            throw new SynchronizerSettingsException(
                    true,
                    GoogleTasksApi.getInstance().getApiId(),
                    null,
                    PluginTranslations.getString("error.no_email"));
        }

        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();

            String clientId = GoogleTasksApi.getInstance().getClientId();
            String clientSecret = GoogleTasksApi.getInstance().getClientSecret();

            String redirectUri = "urn:ietf:wg:oauth:2.0:oob";

            String[] scopes = new String[] {
                    "https://www.googleapis.com/auth/tasks",
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

                        GoogleTasksConnection.this.code = JOptionPane.showInputDialog(
                                PluginApi.getCurrentWindow(),
                                PluginTranslations.getString("connection.code.message"),
                                PluginTranslations.getString("connection.code.title"),
                                JOptionPane.QUESTION_MESSAGE);
                    }

                });

                if (this.code == null)
                    throw new SynchronizerConnectionException(
                            true,
                            GoogleTasksApi.getInstance().getApiId(),
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
                    GoogleTasksApi.getInstance().getProxyHost(),
                    GoogleTasksApi.getInstance().getProxyPort(),
                    GoogleTasksApi.getInstance().getProxyUsername(),
                    GoogleTasksApi.getInstance().getProxyPassword());

            if (response.isSuccessfull()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getContent());
                String email = root.path("data").path("email").textValue();

                if (!EqualsUtils.equalsStringIgnoreCase(email, settingsEmail)) {
                    throw new SynchronizerSettingsException(
                            true,
                            GoogleTasksApi.getInstance().getApiId(),
                            null,
                            PluginTranslations.getString("error.invalid_google_email"));
                }
            }

            Tasks.Builder builder = new Tasks.Builder(
                    httpTransport,
                    jsonFactory,
                    credential);
            builder.setApplicationName(GoogleTasksApi.getInstance().getApplicationName());

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
                    GoogleTasksApi.getInstance().getApiId(),
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

        this.accessToken = p.getStringProperty("plugin.googletasks.access_token");
        this.refreshToken = p.getStringProperty("plugin.googletasks.refresh_token");
    }

    @Override
    public void saveParameters(Properties properties) {
        PropertyMap p = new PropertyMap(properties);

        p.setStringProperty("plugin.googletasks.access_token", this.accessToken);
        p.setStringProperty(
                "plugin.googletasks.refresh_token",
                this.refreshToken);
    }

}
