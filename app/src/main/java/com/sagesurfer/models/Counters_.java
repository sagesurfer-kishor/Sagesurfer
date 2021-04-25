package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class Counters_ {

    @SerializedName("mobile")
    private int mobile;

    @SerializedName("web")
    private int web;

    @SerializedName("announcement")
    private int announcement;

    @SerializedName("tasklist")
    private int task_list;

    @SerializedName("fms")
    private int fms;

    @SerializedName("fms_group")
    private int fms_group;

    @SerializedName("dashboard_msg")
    private int dashboard_message;

    @SerializedName("calendar")
    private int calendar;

    @SerializedName("future_events")
    private int future_events;

    @SerializedName("reminder")
    private int reminder;

    @SerializedName("team_talks")
    private int team_talks;

    @SerializedName("form")
    private int form;

    @SerializedName("messages")
    private int messages;

    @SerializedName(General.POSTCARD)
    private int postcard;

    @SerializedName(General.DRAFT)
    private int draft;

    @SerializedName("sent")
    private int sent;

    @SerializedName("trash")
    private int trash;

    @SerializedName(General.SELFCARE)
    private int selfcare;

    @SerializedName(General.SELFGOAL)
    private int selfgoal;

    @SerializedName(General.YOUTH_SELFCARE)
    private int youth_selfcare;

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public void setWeb(int web) {
        this.web = web;
    }

    public void setAnnouncement(int announcement) {
        this.announcement = announcement;
    }

    public void setTaskList(int task_list) {
        this.task_list = task_list;
    }

    public void setDashboardMessage(int dashboard_message) {
        this.dashboard_message = dashboard_message;
    }

    public void setCalendar(int calendar) {
        this.calendar = calendar;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public void setFutureEvents(int future_events) {
        this.future_events = future_events;
    }

    public void setTeamTalks(int team_talks) {
        this.team_talks = team_talks;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }

    public void setPostcard(int postcard) {
        this.postcard = postcard;
    }

    public void setDraft(int draft) {
        this.draft = draft;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public void setTrash(int trash) {
        this.trash = trash;
    }
    /*Getter Methods*/

    public int getSosUpdates() {
        return (this.mobile + this.web);
    }

    public int getAnnouncement() {
        return this.announcement;
    }

    public int getTaskList() {
        return this.task_list;
    }

    public int getTodayEvent() {
        return this.calendar;
    }

    public int getFms() {
        return this.fms;
    }

    public int getFmsGroup() {
        return this.fms_group;
    }

    public int getDashboardMessage() {
        return this.dashboard_message;
    }

    public int getFutureEvents() {
        return this.future_events;
    }

    public int getReminder() {
        return this.reminder;
    }

    public int getTeamTalks() {
        return this.team_talks;
    }

    public int getForm() {
        return this.form;
    }

    public int getMessages() {
        return this.messages;
    }

    public int getPostcard() {
        return this.postcard;
    }

    public int getSent() {
        return this.sent;
    }

    public int getTrash() {
        return this.trash;
    }

    public int getDraft() {
        return this.draft;
    }

    public int getAlerts() {
        return (this.announcement + this.calendar + this.reminder + this.future_events
                + this.task_list + this.team_talks + this.dashboard_message);
    }

    public int getCalendar() {
        return (this.calendar + this.reminder + this.future_events);
    }

    public int getSelfcare() {
        return selfcare;
    }

    public void setSelfcare(int selfcare) {
        this.selfcare = selfcare;
    }

    public int getSelfgoal() {
        return selfgoal;
    }

    public void setSelfgoal(int selfgoal) {
        this.selfgoal = selfgoal;
    }

    public int getYouth_selfcare() {
        return youth_selfcare;
    }

    public void setYouth_selfcare(int youth_selfcare) {
        this.youth_selfcare = youth_selfcare;
    }
}
