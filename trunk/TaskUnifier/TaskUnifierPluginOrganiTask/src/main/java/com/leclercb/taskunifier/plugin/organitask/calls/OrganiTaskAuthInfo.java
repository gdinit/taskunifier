/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

public final class OrganiTaskAuthInfo {

    private String locale;
    private long teamMemberId;
    private long userId;
    private long teamId;
    private boolean teamMain;
    private long offerId;
    private String role;
    private String accountType;
    private int points;
    private String userEmail;
    private String teamTitle;
    private long subscriptionValidity;
    private int subscriptionDaysLeft;

    protected OrganiTaskAuthInfo() {

    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public long getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(long teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public boolean isTeamMain() {
        return teamMain;
    }

    public void setTeamMain(boolean teamMain) {
        this.teamMain = teamMain;
    }

    public long getOfferId() {
        return offerId;
    }

    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTeamTitle() {
        return teamTitle;
    }

    public void setTeamTitle(String teamTitle) {
        this.teamTitle = teamTitle;
    }

    public long getSubscriptionValidity() {
        return subscriptionValidity;
    }

    public void setSubscriptionValidity(long subscriptionValidity) {
        this.subscriptionValidity = subscriptionValidity;
    }

    public int getSubscriptionDaysLeft() {
        return subscriptionDaysLeft;
    }

    public void setSubscriptionDaysLeft(int subscriptionDaysLeft) {
        this.subscriptionDaysLeft = subscriptionDaysLeft;
    }

}
