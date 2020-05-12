package com.example.askdoctors.Activities.Model;

public class Message {

    private String message;
    private String sender;
    private String receiver;
    private String isSeen;

    public Message(String message, String sender, String receiver, String isSeen){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.isSeen = isSeen;
    }

    public Message(){
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getSender(){
        return sender;
    }
    public void setSender(String sender){
        this.sender = sender;
    }

    public String getReceiver(){
        return receiver;
    }

    public void setReceiver(String receiver){
        this.receiver = receiver;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }
}
