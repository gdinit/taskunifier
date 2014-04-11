/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.commons.api.utils.HttpUtils;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerHttpException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerNotConnectedException;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.organitask.OrganiTaskApi;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractCall {

    public AbstractCall() {

    }

    protected String getScheme() {
        if (EqualsUtils.equals(
                PluginApi.getUserSettings().getBooleanProperty(
                        "plugin.organitask.enable_ssl"),
                true))
            return "https";

        return "http";
    }

    protected String callGet(
            String path,
            List<NameValuePair> parameters) throws SynchronizerException {
        try {
            HttpResponse response = HttpUtils.getHttpGetResponse(
                    URIUtils.createURI(
                            this.getScheme(),
                            OrganiTaskApi.getInstance().getApiUrl(),
                            -1,
                            path,
                            URLEncodedUtils.format(parameters, "UTF-8"),
                            null),
                    OrganiTaskApi.getInstance().getProxyHost(),
                    OrganiTaskApi.getInstance().getProxyPort(),
                    OrganiTaskApi.getInstance().getProxyUsername(),
                    OrganiTaskApi.getInstance().getProxyPassword());

            PluginLogger.getLogger().fine(
                    URIUtils.createURI(
                            this.getScheme(),
                            OrganiTaskApi.getInstance().getApiUrl(),
                            -1,
                            path,
                            URLEncodedUtils.format(parameters, "UTF-8"),
                            null).toString());

            if (!response.isSuccessfull()) {
                PluginLogger.getLogger().warning(
                        response.getCode() + ": " + response.getMessage());

                throw new SynchronizerHttpException(
                        false,
                        response.getCode(),
                        response.getMessage());
            }

            PluginLogger.getLogger().fine(response.getContent());

            return response.getContent();
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
            throw new SynchronizerHttpException(false, 0, e.getMessage(), e);
        }
    }

    protected String call(
            String requestMethod,
            String path,
            String accessToken,
            String body) throws SynchronizerException {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("access_token", accessToken));

            URI uri = URIUtils.createURI(
                    this.getScheme(),
                    OrganiTaskApi.getInstance().getApiUrl(),
                    -1,
                    path,
                    URLEncodedUtils.format(params, "UTF-8"),
                    null);

            HttpResponse response = HttpUtils.getHttpResponse(
                    requestMethod,
                    uri,
                    body,
                    "application/json",
                    OrganiTaskApi.getInstance().getProxyHost(),
                    OrganiTaskApi.getInstance().getProxyPort(),
                    OrganiTaskApi.getInstance().getProxyUsername(),
                    OrganiTaskApi.getInstance().getProxyPassword());

            PluginLogger.getLogger().fine(uri + "\nBody:\n" + body);

            if (!response.isSuccessfull()) {
                PluginLogger.getLogger().warning(
                        response.getCode() + ": " + response.getMessage());

                throw new SynchronizerHttpException(
                        false,
                        response.getCode(),
                        response.getMessage());
            }

            PluginLogger.getLogger().fine(response.getContent());

            return response.getContent();
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
            throw new SynchronizerHttpException(false, 0, e.getMessage(), e);
        }
    }

}
