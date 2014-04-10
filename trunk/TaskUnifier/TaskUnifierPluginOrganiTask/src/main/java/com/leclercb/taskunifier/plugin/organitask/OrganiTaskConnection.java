/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask;

import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.commons.api.utils.HttpUtils;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.exc.*;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.utils.DesktopUtils;
import com.leclercb.taskunifier.plugin.organitask.calls.OrganiTaskAuthInfo;
import com.leclercb.taskunifier.plugin.organitask.calls.OrganiTaskStatement;
import com.leclercb.taskunifier.plugin.organitask.calls.OrganiTaskToken;
import com.leclercb.taskunifier.plugin.organitask.translations.PluginTranslations;

public class OrganiTaskConnection implements Connection {

    private OrganiTaskStatement statement;
    private OrganiTaskAuthInfo authInfo;

    private boolean connected;

    private String code;

    private String accessToken;
    private String refreshToken;

    OrganiTaskConnection() {
        this.connected = false;

        this.code = null;

        this.accessToken = null;
        this.refreshToken = null;

        this.statement = new OrganiTaskStatement(this);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public OrganiTaskStatement getStatement() {
        return this.statement;
    }

    public OrganiTaskAuthInfo getAuthInfo() {
        return this.accountInfo;
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
                    OrganiTaskApi.getInstance().getApiId(),
                    null,
                    PluginTranslations.getString("error.no_email"));
        }

        try {
            final String authorizationUrl = "http://www.organitask.com/web/en/app?action=authorize&client_id=" +
                    OrganiTaskApi.getInstance().getClientId() +
                    "&redirect_uri=http%3A%2F%2Fwww.organitask.com%2Fweb%2Fen%2Fapp%3Faction%3Doauth_code&response_type=code";

            OrganiTaskToken token = null;

            if (this.accessToken == null || this.refreshToken == null) {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        DesktopUtils.browse(authorizationUrl);

                        OrganiTaskConnection.this.code = JOptionPane.showInputDialog(
                                PluginApi.getCurrentWindow(),
                                PluginTranslations.getString("connection.code.message"),
                                PluginTranslations.getString("connection.code.title"),
                                JOptionPane.QUESTION_MESSAGE);
                    }

                });

                if (this.code == null)
                    throw new SynchronizerConnectionException(
                            true,
                            OrganiTaskApi.getInstance().getApiId(),
                            null,
                            PluginApi.getTranslation("synchronizer.cancelled_by_user"));

                token = OrganiTaskStatement.getToken(this.code);

                this.accessToken = token.getAccessToken();
                this.refreshToken = token.getRefreshToken();

                this.authInfo = this.statement.getAuthInfo();
            } else {
                try {
                    this.authInfo = this.statement.getAuthInfo();
                } catch (SynchronizerHttpException e) {
                    if (e.getCode() == 403) {
                        token = OrganiTaskStatement.refreshToken(this.refreshToken);

                        this.accessToken = token.getAccessToken();
                        this.refreshToken = token.getRefreshToken();

                        this.authInfo = this.statement.getAuthInfo();
                    } else {
                        throw e;
                    }
                }
            }

            if (!EqualsUtils.equalsStringIgnoreCase(authInfo.getUserEmail(), settingsEmail)) {
                throw new SynchronizerSettingsException(
                        true,
                        OrganiTaskApi.getInstance().getApiId(),
                        null,
                        PluginTranslations.getString("error.invalid_organitask_email"));
            }

            this.connected = true;
        } catch (SynchronizerException e) {
            this.connected = false;

            throw e;
        } catch (Exception e) {
            this.connected = false;

            throw new SynchronizerConnectionException(
                    false,
                    OrganiTaskApi.getInstance().getApiId(),
                    null,
                    e.getMessage(),
                    e);
        }
    }

    public void reconnect() throws SynchronizerException {
        if (!this.connected)
            return;

        this.disconnect();
        this.connect();
    }

    @Override
    public void disconnect() {
        this.connected = false;
    }

    @Override
    public void loadParameters(Properties properties) {
        PropertyMap p = new PropertyMap(properties);

        this.accessToken = p.getStringProperty("plugin.organitask.access_token");
        this.refreshToken = p.getStringProperty("plugin.organitask.refresh_token");
    }

    @Override
    public void saveParameters(Properties properties) {
        PropertyMap p = new PropertyMap(properties);

        p.setStringProperty("plugin.organitask.access_token", this.accessToken);
        p.setStringProperty(
                "plugin.organitask.refresh_token",
                this.refreshToken);
    }

}
