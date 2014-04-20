/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

import java.util.Properties;

/**
 * This plugin will synchronize your contexts, folders,
 * goals, locations, notes and tasks with OrganiTask.
 * <p/>
 * Before using the plugin, you have to initialize it.
 * You have to set the values for the application id,
 * the version and the api key.
 * <p/>
 * OrganiTaskApi.getInstance().setClientId("");
 * OrganiTaskApi.getInstance().setClientRandomId("");
 * OrganiTaskApi.getInstance().setClientSecret("");
 * <p/>
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

    private String apiUrl;
    private String webUrl;
    private String clientId;
    private String clientRandomId;
    private String clientSecret;

    private OrganiTaskApi() {
        super("ORGANITASK", "OrganiTask", "http://www.organitask.com");

        this.setApiUrl("www.organitask.com/api/v1");
        this.setWebUrl("http://www.organitask.com");
        this.setClientId("");
        this.setClientRandomId("");
        this.setClientSecret("");
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        CheckUtils.isNotNull(apiUrl);
        this.apiUrl = apiUrl;
    }

    public String getWebUrl() {
        return this.webUrl;
    }

    public void setWebUrl(String webUrl) {
        CheckUtils.isNotNull(webUrl);
        this.webUrl = webUrl;
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

    @Override
    public void flagAsNew(Model model) {
        model.removeModelReferenceId("organitask");
    }

    @Override
    public OrganiTaskConnection getConnection(Properties properties)
            throws SynchronizerException {
        return new OrganiTaskConnection();
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

        p.setStringProperty("plugin.organitask.access_token", null);
        p.setStringProperty("plugin.organitask.refresh_token", null);
    }

    @Override
    public void resetSynchronizerParameters(Properties properties) {
        CheckUtils.isNotNull(properties);
        PropertyMap p = new PropertyMap(properties);

        p.setCalendarProperty("plugin.organitask.synchronizer.last_sync", null);
    }

}
