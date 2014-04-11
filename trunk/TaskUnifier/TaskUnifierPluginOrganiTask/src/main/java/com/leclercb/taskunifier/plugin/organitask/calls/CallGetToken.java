/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.organitask.OrganiTaskApi;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

final class CallGetToken extends AbstractCall {

    public OrganiTaskToken getToken(String code) throws SynchronizerException {
        CheckUtils.isNotNull(code);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", OrganiTaskApi.getInstance().getClientId() + "_" + OrganiTaskApi.getInstance().getClientRandomId()));
        params.add(new BasicNameValuePair("client_secret", OrganiTaskApi.getInstance().getClientSecret()));
        params.add(new BasicNameValuePair("redirect_uri", "http%3A%2F%2Fwww.organitask.com%2Fweb%2Fen%2Fapp%3Faction%3Doauth_code"));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code", code));

        String content = super.callGet("/auth/oauth/v2/token", params);

        return this.getResponseMessage(content);
    }

    public OrganiTaskToken refreshToken(String refreshToken) throws SynchronizerException {
        CheckUtils.isNotNull(refreshToken);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", OrganiTaskApi.getInstance().getClientId() + "_" + OrganiTaskApi.getInstance().getClientRandomId()));
        params.add(new BasicNameValuePair("client_secret", OrganiTaskApi.getInstance().getClientSecret()));
        params.add(new BasicNameValuePair("redirect_uri", "http%3A%2F%2Fwww.organitask.com%2Fweb%2Fen%2Fapp%3Faction%3Doauth_code"));
        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
        params.add(new BasicNameValuePair("refresh_token", refreshToken));

        String content = super.callGet("/auth/oauth/v2/token", params);

        return this.getResponseMessage(content);
    }

    /**
     * Example: {"access_token":"NTQzZDZlZDI5MTUxY2FjNGU5ZWZkMGIwODExODY3YmZlNzNlZjA2ZTU0NTlmMWZkZWIxMjA2MmIzZDZlZWFmYQ","expires_in":3600,"token_type":"bearer","scope":null,"refresh_token":"NDc1MjhmN2Y4M2RiNWRhZTY2MDg3MGY2NDk2ZWIwZmVhZmNlOWY4MDQyMmExZjM5NTZiYjU2M2E3MTQ0MmE1YQ"}
     *
     * @param content
     * @return
     * @throws SynchronizerException
     */
    private OrganiTaskToken getResponseMessage(String content)
            throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            OrganiTaskToken token = new OrganiTaskToken();

            token.setAccessToken(root.path("access_token").textValue());
            token.setExpiresIn(root.path("expires_in").intValue());
            token.setTokenType(root.path("token_type").textValue());
            token.setScope(root.path("scope").textValue());
            token.setRefreshToken(root.path("refresh_token").textValue());

            return token;
        } catch (Exception e) {
            throw new SynchronizerParsingException(
                    "Error while parsing response ("
                            + this.getClass().getName()
                            + ")",
                    content,
                    e);
        }
    }

}
