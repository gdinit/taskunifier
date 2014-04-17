/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.networkshare;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.Synchronizer;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

import java.util.Properties;

/**
 * This plugin will publish your data to a shared folder.
 *
 * @author leclercb
 */
public class NetworkShareApi extends SynchronizerApi {

    private static NetworkShareApi INSTANCE;

    public static final NetworkShareApi getInstance() {
        if (INSTANCE == null)
            INSTANCE = new NetworkShareApi();

        return INSTANCE;
    }

    public NetworkShareApi() {
        super("NETWORK_SHARE", "Network Share", "http://www.taskunifer.com");
    }

    @Override
    public void flagAsNew(Model model) {

    }

    @Override
    public Connection getConnection(Properties properties)
            throws SynchronizerException {
        return new NetworkShareConnection();
    }

    @Override
    public Synchronizer getSynchronizer(
            Properties properties,
            Connection connection) throws SynchronizerException {
        CheckUtils.isNotNull(connection);

        if (!(connection instanceof NetworkShareConnection))
            throw new IllegalArgumentException(
                    "Connection must be of type NetworkShareConnection");

        return new NetworkShareSynchronizer((NetworkShareConnection) connection);
    }

    @Override
    public void resetConnectionParameters(Properties properties) {

    }

    @Override
    public void resetSynchronizerParameters(Properties properties) {

    }

}
