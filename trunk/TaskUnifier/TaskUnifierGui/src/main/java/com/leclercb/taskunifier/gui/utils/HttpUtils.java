/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   - Neither the name of TaskUnifier or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.leclercb.taskunifier.gui.utils;

import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.taskunifier.gui.main.Main;
import org.apache.http.NameValuePair;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.List;

public final class HttpUtils {

    private HttpUtils() {

    }

    static {
        CookieHandler.setDefault(new CookieManager(
                null,
                CookiePolicy.ACCEPT_ALL));
    }

    public static HttpResponse getHttpResponse(
            String requestMethod,
            URI uri,
            String body,
            String contentType) throws Exception {
        if (!Main.getSettings().getBooleanProperty("proxy.use_system_proxies")
                && Main.getSettings().getBooleanProperty("proxy.enabled")) {
            return com.leclercb.commons.api.utils.HttpUtils.getHttpResponse(
                    requestMethod,
                    uri,
                    body,
                    contentType,
                    Main.getSettings().getStringProperty("proxy.host"),
                    Main.getSettings().getIntegerProperty("proxy.port"),
                    Main.getSettings().getStringProperty("proxy.login"),
                    Main.getSettings().getStringProperty("proxy.password"));
        } else {
            return com.leclercb.commons.api.utils.HttpUtils.getHttpResponse(
                    requestMethod,
                    uri,
                    body,
                    contentType,
                    null,
                    0,
                    null,
                    null);
        }
    }

    public static HttpResponse getHttpGetResponse(URI uri) throws Exception {
        if (!Main.getSettings().getBooleanProperty("proxy.use_system_proxies")
                && Main.getSettings().getBooleanProperty("proxy.enabled")) {
            return com.leclercb.commons.api.utils.HttpUtils.getHttpGetResponse(
                    uri,
                    Main.getSettings().getStringProperty("proxy.host"),
                    Main.getSettings().getIntegerProperty("proxy.port"),
                    Main.getSettings().getStringProperty("proxy.login"),
                    Main.getSettings().getStringProperty("proxy.password"));
        } else {
            return com.leclercb.commons.api.utils.HttpUtils.getHttpGetResponse(uri);
        }
    }

    public static HttpResponse getHttpPostResponse(
            URI uri,
            List<NameValuePair> parameters) throws Exception {
        if (!Main.getSettings().getBooleanProperty("proxy.use_system_proxies")
                && Main.getSettings().getBooleanProperty("proxy.enabled")) {
            return com.leclercb.commons.api.utils.HttpUtils.getHttpPostResponse(
                    uri,
                    parameters,
                    Main.getSettings().getStringProperty("proxy.host"),
                    Main.getSettings().getIntegerProperty("proxy.port"),
                    Main.getSettings().getStringProperty("proxy.login"),
                    Main.getSettings().getStringProperty("proxy.password"));
        } else {
            return com.leclercb.commons.api.utils.HttpUtils.getHttpPostResponse(
                    uri,
                    parameters);
        }
    }

}
