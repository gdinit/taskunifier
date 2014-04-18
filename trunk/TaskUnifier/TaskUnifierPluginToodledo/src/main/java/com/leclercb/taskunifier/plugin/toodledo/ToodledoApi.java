/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoStatement;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoSettingsException;
import org.apache.commons.lang3.SystemUtils;

import java.util.Properties;

/**
 * This plugin will synchronize your contexts, folders,
 * goals, locations, notes and tasks with Toodledo.
 * <p/>
 * Before using the plugin, you have to initialize it.
 * You have to set the values for the application id,
 * the version and the api key.
 * <p/>
 * ToodledoApi.getInstance().setApplicationId("");
 * ToodledoApi.getInstance().setVersion("");
 * ToodledoApi.getInstance().setApiKey("");
 * <p/>
 * http://api.toodledo.com/2/index.php
 *
 * @author leclercb
 */
public final class ToodledoApi extends SynchronizerApi {

    private static ToodledoApi INSTANCE;

    public static final ToodledoApi getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ToodledoApi();

        return INSTANCE;
    }

    private String applicationId;
    private int version;
    private String device;
    private int os;
    private String apiKey;
    private String apiUrl;

    private ToodledoApi() {
        super("TOODLEDO", "Toodledo", "http://www.toodledo.com");

        this.setApplicationId("");
        this.setVersion(0);
        this.setDevice(getDeviceFromSystemUtils());
        this.setOS(getOSFromSystemUtils());
        this.setApiKey("");
        this.setApiUrl("api.toodledo.com");
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(String applicationId) {
        CheckUtils.isNotNull(applicationId);
        this.applicationId = applicationId;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDevice() {
        return this.device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getOS() {
        return this.os;
    }

    public void setOS(int os) {
        this.os = os;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        CheckUtils.isNotNull(apiKey);
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        CheckUtils.isNotNull(apiUrl);
        this.apiUrl = apiUrl;
    }

    @Override
    public boolean allowCustomTaskStatuses() {
        return false;
    }

    public void createAccount(String email, String password)
            throws SynchronizerException {
        ToodledoStatement.createAccount(email, password);
    }

    @Override
    public void flagAsNew(Model model) {
        model.removeModelReferenceId("toodledo");
    }

    @Override
    public ToodledoConnection getConnection(Properties properties)
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

        return new ToodledoConnection(
                p.getStringProperty("toodledo.email"),
                p.getStringProperty("toodledo.password"));
    }

    @Override
    public ToodledoSynchronizer getSynchronizer(
            Properties properties,
            Connection connection) throws SynchronizerException {
        CheckUtils.isNotNull(connection);

        if (!(connection instanceof ToodledoConnection))
            throw new IllegalArgumentException(
                    "Connection must be of type ToodledoConnection");

        return new ToodledoSynchronizer((ToodledoConnection) connection);
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

        p.setCalendarProperty("toodledo.synchronizer.last_context_edit", null);
        p.setCalendarProperty("toodledo.synchronizer.last_folder_edit", null);
        p.setCalendarProperty("toodledo.synchronizer.last_goal_edit", null);
        p.setCalendarProperty("toodledo.synchronizer.last_location_edit", null);
        p.setCalendarProperty("toodledo.synchronizer.last_note_edit", null);
        p.setCalendarProperty("toodledo.synchronizer.last_note_delete", null);
        p.setCalendarProperty("toodledo.synchronizer.last_task_edit", null);
        p.setCalendarProperty("toodledo.synchronizer.last_task_delete", null);
    }

    private static String getDeviceFromSystemUtils() {
        return SystemUtils.OS_ARCH + " - " + SystemUtils.JAVA_VERSION;
    }

    private static int getOSFromSystemUtils() {
        if (SystemUtils.IS_OS_LINUX)
            return 1;

        if (SystemUtils.IS_OS_MAC)
            return 2;

        if (SystemUtils.IS_OS_WINDOWS)
            return 3;

        return 0;
    }

}