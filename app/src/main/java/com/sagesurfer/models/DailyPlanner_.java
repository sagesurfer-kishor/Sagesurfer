package com.sagesurfer.models;


/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 31-03-2018
 *         Last Modified on
 */

public class DailyPlanner_ {

    private Announcement_ announcement_;
    private Task_ task_;
    private Event_ event_;
    private Goal_ goal_;

    private int status;
    private int type;

    /* Setter Methods */

    public void setStatus(int status) {
        this.status = status;
    }

    public void setAnnouncement(Announcement_ announcement_) {
        this.announcement_ = announcement_;
    }

    public void setTask(Task_ task_) {
        this.task_ = task_;
    }

    public void setEvent(Event_ event_) {
        this.event_ = event_;
    }

    public void setGoal(Goal_ goal_) {
        this.goal_ = goal_;
    }

    public void setType(int type) {
        this.type = type;
    }

    /* Getter Methods */

    public int getStatus() {
        return status;
    }

    public Announcement_ getAnnouncement() {
        return announcement_;
    }

    public Task_ getTask() {
        return task_;
    }

    public Event_ getEvent() {
        return event_;
    }

    public Goal_ getGoal() {
        return goal_;
    }

    public int getType() {
        return type;
    }
}
