package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 15/03/2018
 *         Last Modified on
 */

public class Goal_ implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.TYPE)
    private int type;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("color")
    private String color;

    @SerializedName("text")
    private String text;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("goal_type")
    private int goal_type;

    @SerializedName(General.START_DATE)
    private String start_date;

    @SerializedName(General.END_DATE)
    private String end_date;

    @SerializedName(General.START_TIME)
    private String start_time;

    @SerializedName("occurrences")
    private int occurrences;

    @SerializedName("notify_frequency")
    private String notify_frequency;

    @SerializedName("goal_status")
    private int goal_status;

    @SerializedName("is_dashboard")
    private int is_dashboard;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("units")
    private String units;

    @SerializedName("frequency")
    private String frequency;

    @SerializedName("impact")
    private String impact;

    @SerializedName("frequency_unit")
    private String frequency_unit;

    @SerializedName("frequency_sub_unit_days")
    private String frequency_sub_unit_days;

    @SerializedName("notify")
    private int notify;

    @SerializedName("notify_at")
    private String notify_at;

    @SerializedName("progress")
    private int progress;

    @SerializedName(General.PRIORITY)
    private int priority;

    @SerializedName(General.TODAY_STATUS)
    private int today_status;

    @SerializedName("goal_current_status")
    private int goal_current_status;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName("image_id")
    private String image_id;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName("c_date")
    private String c_date;

    @SerializedName("last_input")
    private String last_input;

    @SerializedName("error")
    private String error;

    @SerializedName(General.MAIN_GOAL_ID)
    private String main_goal_id;

    @SerializedName(General.LAST_UPDATED)
    private long last_updated;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.YES)
    private String yes;

    @SerializedName(General.NO)
    private String no;

    @SerializedName(General.AM_MSG)
    private String am_msg;

    @SerializedName(General.PM_MSG)
    private String pm_msg;


    //for self goal reports
    @SerializedName(General.GOAL_ID)
    private long goal_id;

    @SerializedName(General.IS_READ)
    private int is_read;

    /* Setter Methods */

    public void setId(long id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setC_date(String c_date) {
        this.c_date = c_date;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setGoal_type(int goal_type) {
        this.goal_type = goal_type;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public void setNotify_at(String notify_at) {
        this.notify_at = notify_at;
    }

    public void setNotify_frequency(String notify_frequency) {
        this.notify_frequency = notify_frequency;
    }

    public void setGoal_status(int goal_status) {
        this.goal_status = goal_status;
    }

    public void setIs_dashboard(int is_dashboard) {
        this.is_dashboard = is_dashboard;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setFrequency_sub_unit_days(String frequency_sub_unit_days) {
        this.frequency_sub_unit_days = frequency_sub_unit_days;
    }

    public void setFrequency_unit(String frequency_unit) {
        this.frequency_unit = frequency_unit;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    /* Getter Methods */

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getC_date() {
        return c_date;
    }

    public long getLast_updated() {
        return last_updated;
    }

    public String getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public int getPriority() {
        return priority;
    }

    public String getImage() {
        return image;
    }

    public String getThumb() {
        return thumb;
    }

    public int getGoal_status() {
        return goal_status;
    }

    public int getGoal_type() {
        return goal_type;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public String getNotify_frequency() {
        return notify_frequency;
    }

    public int getNotify() {
        return notify;
    }

    public String getNotify_at() {
        return notify_at;
    }

    public String getImage_id() {
        return image_id;
    }

    public int getIs_dashboard() {
        return is_dashboard;
    }

    public String getUnits() {
        return units;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getFrequency_sub_unit_days() {
        return frequency_sub_unit_days;
    }

    public String getFrequency_unit() {
        return frequency_unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getProgress() {
        return progress;
    }

    public String getImpact() {
        return impact;
    }

    public int getStatus() {
        return status;
    }

    public long getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(long goal_id) {
        this.goal_id = goal_id;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public String getLast_input() {
        return last_input;
    }

    public void setLast_input(String last_input) {
        this.last_input = last_input;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getToday_status() {
        return today_status;
    }

    public void setToday_status(int today_status) {
        this.today_status = today_status;
    }

    public int getGoal_current_status() {
        return goal_current_status;
    }

    public void setGoal_current_status(int goal_current_status) {
        this.goal_current_status = goal_current_status;
    }

    public String getMain_goal_id() {
        return main_goal_id;
    }

    public void setMain_goal_id(String main_goal_id) {
        this.main_goal_id = main_goal_id;
    }

    public String getYes() {
        return yes;
    }

    public void setYes(String yes) {
        this.yes = yes;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getAm_msg() {
        return am_msg;
    }

    public void setAm_msg(String am_msg) {
        this.am_msg = am_msg;
    }

    public String getPm_msg() {
        return pm_msg;
    }

    public void setPm_msg(String pm_msg) {
        this.pm_msg = pm_msg;
    }
}
