/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

public final class OrganiTaskAuthInfo {

    private String locale;
    private int teamMemberId;
    private int userId;
    private int teamId;
    private boolean teamMain;
    private int offerId;
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

    public int getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(int teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public boolean isTeamMain() {
        return teamMain;
    }

    public void setTeamMain(boolean teamMain) {
        this.teamMain = teamMain;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
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
