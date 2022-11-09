package com.example.facedetproject.Models.UserDetailsModel;

public class UserAcknowledgeModel {
    private String attendee_name;
    private String attendee_id;
    private String registration_device;
    private String department;
    private String created_on;

    public String getAttendee_name() {
        return attendee_name;
    }

    public void setAttendee_name(String attendee_name) {
        this.attendee_name = attendee_name;
    }

    public String getAttendee_id() {
        return attendee_id;
    }

    public void setAttendee_id(String attendee_id) {
        this.attendee_id = attendee_id;
    }

    public String getRegistration_device() {
        return registration_device;
    }

    public void setRegistration_device(String registration_device) {
        this.registration_device = registration_device;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
