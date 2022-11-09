package com.example.facedetproject.Models.AttendanceModels;

import java.util.ArrayList;

public class AttendanceResponseModel {
    private ArrayList<AcknowledgeClass> Acknowledge;


    public ArrayList<AcknowledgeClass> getAcknowledge() {
        return Acknowledge;
    }

    public void setAcknowledge(ArrayList<AcknowledgeClass> acknowledge) {
        Acknowledge = acknowledge;
    }

}
