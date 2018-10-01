package com.mtown.app.dao;

public class AuditionDAO {
    private String audition_title,created_by_id,created_by_name,description
    ,id,note,role_type,total_model,mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public AuditionDAO(String audition_title, String created_by_id, String created_by_name, String description, String id, String note,
                       String role_type, String total_model, String mobile) {
        this.audition_title = audition_title;
        this.created_by_id = created_by_id;
        this.created_by_name = created_by_name;
        this.description = description;
        this.id = id;
        this.note = note;
        this.role_type = role_type;
        this.total_model = total_model;
        this.mobile = mobile;
    }

    public String getAudition_title() {
        return audition_title;
    }

    public void setAudition_title(String audition_title) {
        this.audition_title = audition_title;
    }

    public String getCreated_by_id() {
        return created_by_id;
    }

    public void setCreated_by_id(String created_by_id) {
        this.created_by_id = created_by_id;
    }

    public String getCreated_by_name() {
        return created_by_name;
    }

    public void setCreated_by_name(String created_by_name) {
        this.created_by_name = created_by_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRole_type() {
        return role_type;
    }

    public void setRole_type(String role_type) {
        this.role_type = role_type;
    }

    public String getTotal_model() {
        return total_model;
    }

    public void setTotal_model(String total_model) {
        this.total_model = total_model;
    }
}
