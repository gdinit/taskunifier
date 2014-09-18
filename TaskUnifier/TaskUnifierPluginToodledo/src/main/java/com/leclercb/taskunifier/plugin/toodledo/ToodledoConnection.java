/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerConnectionException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerSettingsException;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.utils.DesktopUtils;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoAccountInfo;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoStatement;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoToken;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoConnectionException;
import com.leclercb.taskunifier.plugin.toodledo.translations.PluginTranslations;

import javax.swing.*;
import java.util.Properties;

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
        String settingsEmail = PluginApi.getUserSettings().getStringProperty(
                "plugin.toodledo.email");

        if (settingsEmail == null || settingsEmail.length() == 0) {
            throw new SynchronizerSettingsException(
                    true,
                    ToodledoApi.getInstance().getApiId(),
                    null,
                    PluginTranslations.getString("error.no_email"));
        }

        try {
            final String authorizationUrl = ToodledoStatement.getAuthorizeUrl();

            ToodledoToken token = null;

            if (this.accessToken == null || this.refreshToken == null) {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        DesktopUtils.browse(authorizationUrl);

                        ToodledoConnection.this.code = JOptionPane.showInputDialog(
                                PluginApi.getCurrentWindow(),
                                PluginTranslations.getString("connection.code.message"),
                                PluginTranslations.getString("connection.code.title"),
                                JOptionPane.QUESTION_MESSAGE);
                    }

                });

                if (this.code == null)
                    throw new SynchronizerConnectionException(
                            true,
                            ToodledoApi.getInstance().getApiId(),
                            null,
                            PluginApi.getTranslation("synchronizer.cancelled_by_user"));

                token = ToodledoStatement.getToken(this.code);

                this.accessToken = token.getAccessToken();
                this.refreshToken = token.getRefreshToken();

                this.accountInfo = this.statement.getAccountInfo();
            } else {
                try {
                    this.accountInfo = this.statement.getAccountInfo();
                } catch (ToodledoConnectionException e) {
                    token = ToodledoStatement.refreshToken(this.refreshToken);

                    this.accessToken = token.getAccessToken();
                    this.refreshToken = token.getRefreshToken();

                    this.accountInfo = this.statement.getAccountInfo();
                }
            }

            if (!EqualsUtils.equalsStringIgnoreCase(accountInfo.getEmail(), settingsEmail)) {
                throw new SynchronizerSettingsException(
                        true,
                        ToodledoApi.getInstance().getApiId(),
                        null,
                        PluginTranslations.getString("error.invalid_toodledo_email"));
            }

            this.connected = true;
        } catch (SynchronizerException e) {
            this.connected = false;

            throw e;
        } catch (Exception e) {
            this.connected = false;

            throw new SynchronizerConnectionException(
                    false,
                    ToodledoApi.getInstance().getApiId(),
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

        this.accessToken = p.getStringProperty("plugin.toodledo.access_token");
        this.refreshToken = p.getStringProperty("plugin.toodledo.refresh_token");
    }

    @Override
    public void saveParameters(Properties properties) {
        PropertyMap p = new PropertyMap(properties);

        p.setStringProperty("plugin.toodledo.access_token", this.accessToken);
        p.setStringProperty(
                "plugin.toodledo.refresh_token",
                this.refreshToken);
    }

}
