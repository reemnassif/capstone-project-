package com.khlafawi.capmedicine.model;

public class Patient {

    public static String ADDED_DATE = "addedDate";
    public static String AGE = "age";
    public static String NAME = "name";
    public static String NURSE_ID = "nurseId";
    public static String PATIENT_ID = "patientId";
    public static String SECTION_NAME = "sectionName";

    private Long addedDate;
    private String age;
    private String name;
    private String nurseId;
    private String patientId;
    private String sectionName;

    public Patient() {
    }

    public Long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Long addedDate) {
        this.addedDate = addedDate;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
