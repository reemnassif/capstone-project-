package com.khlafawi.capmedicine.model;

public class Feedback {

    public static final String ID = "id";
    public static final String PATIENT_ID = "patientId";
    public static final String PATIENT_NAME = "patientName";
    public static final String NURSE_ID = "nurseId";
    public static final String TOOK = "took";

    private String id;
    private String patientId;
    private String patientName;
    private String NurseId;
    private boolean took;

    public Feedback() {
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getNurseId() {
        return NurseId;
    }

    public void setNurseId(String nurseId) {
        NurseId = nurseId;
    }

    public boolean isTook() {
        return took;
    }

    public void setTook(boolean took) {
        this.took = took;
    }
}
