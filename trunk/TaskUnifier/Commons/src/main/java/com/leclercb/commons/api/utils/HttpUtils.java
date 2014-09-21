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
package com.leclercb.commons.api.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.InputStream;
import java.net.*;
import java.util.List;

public final class HttpUtils {

    private HttpUtils() {

    }

    public static HttpResponse getHttpGetResponse(URI uri) throws Exception {
        return getHttpGetResponse(uri, null, 0, null, null);
    }

    public static HttpResponse getHttpGetResponse(
            URI uri,
            final String proxyHost,
            final int proxyPort,
            final String proxyUsername,
            final String proxyPassword) throws Exception {
        return getHttpResponse("GET", uri, null, null, proxyHost, proxyPort, proxyUsername, proxyPassword, null, null);
    }

    public static HttpResponse getHttpPostResponse(
            URI uri,
            List<NameValuePair> parameters) throws Exception {
        return getHttpPostResponse(uri, parameters, null, 0, null, null);
    }

    public static HttpResponse getHttpPostResponse(
            URI uri,
            List<NameValuePair> parameters,
            final String proxyHost,
            final int proxyPort,
            final String proxyUsername,
            final String proxyPassword) throws Exception {
        return getHttpPostResponse(uri, parameters, proxyHost, proxyPort, proxyUsername, proxyPassword, null, null);
    }

    public static HttpResponse getHttpPostResponse(
            URI uri,
            List<NameValuePair> parameters,
            final String proxyHost,
            final int proxyPort,
            final String proxyUsername,
            final String proxyPassword,
            final String basicAuthUsername,
            final String basicAuthPassword) throws Exception {
        return getHttpResponse(
                "POST",
                uri,
                URLEncodedUtils.format(parameters, "UTF-8"),
                null,
                proxyHost,
                proxyPort,
                proxyUsername,
                proxyPassword,
                basicAuthUsername,
                basicAuthPassword);
    }

    public static HttpResponse getHttpResponse(
            String requestMethod,
            URI uri,
            String body,
            String contentType,
            final String proxyHost,
            final int proxyPort,
            final String proxyUsername,
            final String proxyPassword,
            final String basicAuthUsername,
            final String basicAuthPassword) throws Exception {
        HttpURLConnection connection = null;

        if (proxyHost == null || proxyHost.length() == 0) {
            connection = (HttpURLConnection) uri.toURL().openConnection();
        } else {
            if (proxyUsername != null && proxyUsername.length() != 0 && proxyPassword != null) {
                Authenticator.setDefault(new Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                proxyUsername,
                                proxyPassword.toCharArray());
                    }

                });
            }

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                    proxyHost,
                    proxyPort));
            connection = (HttpURLConnection) uri.toURL().openConnection(proxy);
        }

        if (basicAuthUsername != null && basicAuthUsername.length() != 0) {
            String authString = basicAuthUsername + ":" + basicAuthPassword;
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + new String(authEncBytes));
        }

        connection.setRequestMethod(requestMethod);
        connection.setUseCaches(false);

        if (contentType != null)
            connection.setRequestProperty("Content-Type", contentType);

        if (body != null) {
            connection.setDoOutput(true);
            IOUtils.write(
                    body,
                    connection.getOutputStream(),
                    "UTF-8");
        }

        InputStream inputStream = null;

        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300)
            inputStream = connection.getInputStream();
        else
            inputStream = connection.getErrorStream();

        if (inputStream == null)
            throw new Exception("HTTP error: "
                    + connection.getResponseCode()
                    + " - "
                    + connection.getResponseMessage());

        byte[] bytes = IOUtils.toByteArray(inputStream);

        return new HttpResponse(
                connection.getResponseCode(),
                connection.getResponseMessage(),
                bytes);
    }

}
