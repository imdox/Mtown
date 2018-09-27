package com.mtown.app.dao;

public class AuditionReqDAO {
    private String audition_id,id,audition_images,model_id,mt_comment
            ,mt_conformation,notification,reason,result_status,audition_title,created_by_id,created_by_name,description
            ,note,role_type,total_model;

    public AuditionReqDAO(String audition_id, String id, String audition_images, String model_id, String mt_comment, String mt_conformation, String notification, String reason, String result_status, String audition_title, String created_by_id, String created_by_name, String description, String note, String role_type, String total_model) {
        this.audition_id = audition_id;
        this.id = id;
        this.audition_images = audition_images;
        this.model_id = model_id;
        this.mt_comment = mt_comment;
        this.mt_conformation = mt_conformation;
        this.notification = notification;
        this.reason = reason;
        this.result_status = result_status;
        this.audition_title = audition_title;
        this.created_by_id = created_by_id;
        this.created_by_name = created_by_name;
        this.description = description;
        this.note = note;
        this.role_type = role_type;
        this.total_model = total_model;
    }

    public String getAudition_id() {
        return audition_id;
    }

    public void setAudition_id(String audition_id) {
        this.audition_id = audition_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAudition_images() {
        return audition_images;
    }

    public void setAudition_images(String audition_images) {
        this.audition_images = audition_images;
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getMt_comment() {
        return mt_comment;
    }

    public void setMt_comment(String mt_comment) {
        this.mt_comment = mt_comment;
    }

    public String getMt_conformation() {
        return mt_conformation;
    }

    public void setMt_conformation(String mt_conformation) {
        this.mt_conformation = mt_conformation;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getResult_status() {
        return result_status;
    }

    public void setResult_status(String result_status) {
        this.result_status = result_status;
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
