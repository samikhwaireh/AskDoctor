package com.example.askdoctors.Activities.Model;

public class Questions {
    private String id, question,disease, image, profileImage,userName;

    public Questions(String id, String question, String disease, String image, String profileImage, String userName) {
        this.id = id;
        this.question = question;
        this.disease = disease;
        this.image = image;
        this.profileImage = profileImage;
        this.userName = userName;
    }

    public Questions(){
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
