package com.example.askdoctors.Activities.Model;

public class User {
    public String firstName;
    public String lastName;
    private String password;
    public String id;
    public String gender;
    private String email;
    public String birthday;
    public String accType;
    private String university;
    private String status;
    private String graduate;
    private String diploma;
    public String profileImage;
    private String online;

    public User(String firstName, String lastName, String password, String id, String gender, String email, String birthday, String accType, String university, String status, String graduate, String diploma, String profileImage, String online) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.id = id;
        this.gender = gender;
        this.email = email;
        this.birthday = birthday;
        this.accType = accType;
        this.university = university;
        this.status = status;
        this.graduate = graduate;
        this.diploma = diploma;
        this.profileImage = profileImage;
        this.online = online;
    }

    public User(){
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGraduate() {
        return graduate;
    }

    public void setGraduate(String graduate) {
        this.graduate = graduate;
    }

    public String getDiploma() {
        return diploma;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
