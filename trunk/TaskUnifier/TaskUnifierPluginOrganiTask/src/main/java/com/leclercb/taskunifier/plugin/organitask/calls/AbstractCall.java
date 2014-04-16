/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.JsonNode;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.commons.api.utils.HttpUtils;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerHttpException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerNotConnectedException;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.organitask.OrganiTaskApi;
import com.leclercb.taskunifier.plugin.organitask.calls.exc.OrganiTaskConnectionException;
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
            List<NameValuePair> params) throws SynchronizerException {
        return this.call("GET", path, params, null);
    }

    protected String call(
            String requestMethod,
            String path,
            String accessToken,
            String body) throws SynchronizerException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        return this.call(requestMethod, path, params, body);
    }

    protected String call(
            String requestMethod,
            String path,
            List<NameValuePair> params,
            String body) throws SynchronizerException {
        try {
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

            if (body == null) {
                PluginLogger.getLogger().fine(requestMethod + ": " + uri.toString());
            } else {
                PluginLogger.getLogger().fine(requestMethod + ": " + uri + "\nBody:\n" + body);
            }

            if (!response.isSuccessfull()) {
                PluginLogger.getLogger().warning(
                        response.getCode() + ": " + response.getMessage());

                if (response.getCode() == 401 || response.getCode() == 403) {
                    throw new OrganiTaskConnectionException(
                            true,
                            OrganiTaskApi.getInstance().getApiId(),
                            response.getCode() + "",
                            response.getMessage());
                } else {
                    throw new SynchronizerHttpException(
                            false,
                            response.getCode(),
                            response.getMessage());
                }
            }

            PluginLogger.getLogger().fine(response.getContent());

            return response.getContent();
        } catch (OrganiTaskConnectionException e) {
            throw e;
        } catch (SynchronizerHttpException e) {
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
            throw new SynchronizerHttpException(false, 0, e.getMessage(), e);
        }
    }

    public String getNodeTextValue(JsonNode node) {
        if (node.isNull())
            return null;

        return node.asText();
    }

}
