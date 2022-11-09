package com.example.facedetproject.Models.AttendanceModels;

public class AttendanceRequestModel {
    private String image_base64;
    private String device;

    public String getImage_base64() {
        return image_base64;
    }

    public void setImage_base64(String image_base64) {
        this.image_base64 = image_base64;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
