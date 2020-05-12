package com.example.askdoctors.Activities.Model;

public class ChatList {
    private String receiver;

    public ChatList(String receiver) {
        this.receiver = receiver;
    }

    public ChatList(){

    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
