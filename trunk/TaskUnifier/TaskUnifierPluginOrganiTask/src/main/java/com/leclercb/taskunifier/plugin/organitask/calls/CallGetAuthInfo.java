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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

final class CallGetAuthInfo extends AbstractCall {

    public OrganiTaskAuthInfo getAuthInfo(String accessToken)
            throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        String content = super.callGet("/auth/check", params);

        return this.getResponseMessage(content);
    }

    /**
     * Example: {"locale":"en","team_member_id":3001,"user_id":1001,"team_id":2001,"team_main":true,"offer_id":3,"role":"ADMIN","account_type":"PRO","points":100,"user_email":"test1001@me.com","team_title":"test1001@me.com","subscription_validity":1396877371,"subscription_days_left":0,"messages":[]}
     *
     * @param content
     * @return
     * @throws SynchronizerException
     */
    private OrganiTaskAuthInfo getResponseMessage(String content)
            throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            OrganiTaskAuthInfo token = new OrganiTaskAuthInfo();

            token.setLocale(root.path("locale").asText());
            token.setTeamMemberId(root.path("team_member_id").asInt());
            token.setUserId(root.path("user_id").asInt());
            token.setTeamId(root.path("team_id").asInt());
            token.setTeamMain(root.path("team_main").asBoolean());
            token.setOfferId(root.path("offer_id").asInt());
            token.setRole(root.path("role").asText());
            token.setAccountType(root.path("account_type").asText());
            token.setPoints(root.path("points").asInt());
            token.setUserEmail(root.path("user_email").asText());
            token.setTeamTitle(root.path("team_title").asText());
            token.setSubscriptionValidity(root.path("subscription_validity").asInt());
            token.setSubscriptionDaysLeft(root.path("subscription_days_left").asInt());

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
