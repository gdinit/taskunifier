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

            token.setLocale(root.path("locale").textValue());
            token.setTeamMemberId(root.path("team_member_id").intValue());
            token.setUserId(root.path("user_id").intValue());
            token.setTeamId(root.path("team_id").intValue());
            token.setTeamMain(root.path("team_main").booleanValue());
            token.setOfferId(root.path("offer_id").intValue());
            token.setRole(root.path("role").textValue());
            token.setAccountType(root.path("account_type").textValue());
            token.setPoints(root.path("points").intValue());
            token.setUserEmail(root.path("user_email").textValue());
            token.setTeamTitle(root.path("team_title").textValue());
            token.setSubscriptionValidity(root.path("subscription_validity").intValue());
            token.setSubscriptionDaysLeft(root.path("subscription_days_left").intValue());

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
