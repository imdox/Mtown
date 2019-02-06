package com.mtown.app.dao;

public class ModelDAO {
    private  String id,model_code,about_you,age,designation, experience,eye_color,firstname,gender,height,known_languages,lastname,mobile,profile_image,skin_color,weight,email,status;
    private String model_images[];

    public ModelDAO(String id, String model_code, String about_you, String age, String designation, String experience, String eye_color, String firstname, String gender, String height, String known_languages, String lastname, String mobile, String profile_image,
                    String skin_color, String weight, String[] model_images,String email,String status) {
        this.id = id;
        this.model_code = model_code;
        this.about_you = about_you;
        this.age = age;
        this.designation = designation;
        this.experience = experience;
        this.eye_color = eye_color;
        this.firstname = firstname;
        this.gender = gender;
        this.height = height;
        this.known_languages = known_languages;
        this.lastname = lastname;
        this.mobile = mobile;
        this.profile_image = profile_image;
        this.skin_color = skin_color;
        this.weight = weight;
        this.model_images = model_images;
        this.status = status;
        this.email = email;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModel_code() {
        return model_code;
    }

    public void setModel_code(String model_code) {
        this.model_code = model_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getAbout_you() {
        return about_you;
    }

    public void setAbout_you(String about_you) {
        this.about_you = about_you;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEye_color() {
        return eye_color;
    }

    public void setEye_color(String eye_color) {
        this.eye_color = eye_color;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getKnown_languages() {
        return known_languages;
    }

    public void setKnown_languages(String known_languages) {
        this.known_languages = known_languages;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getSkin_color() {
        return skin_color;
    }

    public void setSkin_color(String skin_color) {
        this.skin_color = skin_color;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String[] getModel_images() {
        return model_images;
    }

    public void setModel_images(String[] model_images) {
        this.model_images = model_images;
    }
}
