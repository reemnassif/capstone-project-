package com.khlafawi.capmedicine.model;

public class Nurse {

    public static final String CODE = "code";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String NURSE_ID = "nurseId";
    public static final String GCM_ID = "GDM_ID";


    private String code;
    private String email;
    private String name;
    private String nurseId;

    public Nurse() {
    }

    public Nurse(String code, String email, String name, String nurseId) {
        this.code = code;
        this.email = email;
        this.name = name;
        this.nurseId = nurseId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNurseId() {
        return nurseId;
    }

    public void setNurseId(String nurseId) {
        this.nurseId = nurseId;
    }
}
