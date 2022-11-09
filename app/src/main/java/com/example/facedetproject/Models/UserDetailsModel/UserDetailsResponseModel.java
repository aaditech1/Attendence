package com.example.facedetproject.Models.UserDetailsModel;

import java.util.ArrayList;

public class UserDetailsResponseModel {
    private ArrayList<UserAcknowledgeModel> Acknowledge;
    private ArrayList<String> ids;

    public ArrayList<UserAcknowledgeModel> getAcknowledge() {
        return Acknowledge;
    }

    public void setAcknowledge(ArrayList<UserAcknowledgeModel> acknowledge) {
        Acknowledge = acknowledge;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }
}
