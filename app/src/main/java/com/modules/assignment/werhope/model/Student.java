package com.modules.assignment.werhope.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class Student implements Serializable {

    @SerializedName(General.STUD_ID)
    private int stud_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.EMAIL)
    private String email;

    @SerializedName("send_invite_student")
    private int send_invite_student;

    @SerializedName("student_signup")
    private int student_signup;

    @SerializedName(General.DOB)
    private String dob;

    @SerializedName(General.ASSIGNMENT_DATE)
    private long assignment_date;

    @SerializedName(General.GRADE)
    private int grade;

    @SerializedName(General.SCHOOL_ID)
    private int school_id;

    @SerializedName(General.SCHOOL_NAME)
    private String school_name;

    @SerializedName(General.PARENT_ID)
    private int parent_id;

    @SerializedName(General.PARENT_NAME)
    private String parent_name;

    @SerializedName(General.PARENT_EMAIL)
    private String parent_email;

    @SerializedName(General.PARENT_DOB)
    private String parent_dob;

    @SerializedName("send_invite_parent")
    private int send_invite_parent;

    @SerializedName("parent_signup")
    private int parent_signup;

    @SerializedName(General.COACH_ID)
    private int coach_id;

    @SerializedName(General.COACH_NAME)
    private String coach_name;

    @SerializedName(General.ADDED_BY)
    private String added_by;

    @SerializedName(General.ADDED_BY_ROLE)
    private String added_by_role;

    @SerializedName(General.STATUS)
    private int status;

    private boolean selected = false;

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getStud_id() {
        return stud_id;
    }

    public void setStud_id(int stud_id) {
        this.stud_id = stud_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public long getAssignment_date() {
        return assignment_date;
    }

    public void setAssignment_date(long assignment_date) {
        this.assignment_date = assignment_date;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getParent_email() {
        return parent_email;
    }

    public void setParent_email(String parent_email) {
        this.parent_email = parent_email;
    }

    public String getParent_dob() {
        return parent_dob;
    }

    public void setParent_dob(String parent_dob) {
        this.parent_dob = parent_dob;
    }

    public int getCoach_id() {
        return coach_id;
    }

    public void setCoach_id(int coach_id) {
        this.coach_id = coach_id;
    }

    public String getCoach_name() {
        return coach_name;
    }

    public void setCoach_name(String coach_name) {
        this.coach_name = coach_name;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public String getAdded_by_role() {
        return added_by_role;
    }

    public void setAdded_by_role(String added_by_role) {
        this.added_by_role = added_by_role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSend_invite_student() {
        return send_invite_student;
    }

    public void setSend_invite_student(int send_invite_student) {
        this.send_invite_student = send_invite_student;
    }

    public int getStudent_signup() {
        return student_signup;
    }

    public void setStudent_signup(int student_signup) {
        this.student_signup = student_signup;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getSend_invite_parent() {
        return send_invite_parent;
    }

    public void setSend_invite_parent(int send_invite_parent) {
        this.send_invite_parent = send_invite_parent;
    }

    public int getParent_signup() {
        return parent_signup;
    }

    public void setParent_signup(int parent_signup) {
        this.parent_signup = parent_signup;
    }

    public boolean isSelected() {
        return selected;
    }
}
