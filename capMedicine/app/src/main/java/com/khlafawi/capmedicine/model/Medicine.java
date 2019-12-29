package com.khlafawi.capmedicine.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Medicine implements Parcelable {

    public static final String DOSE = "dose";
    public static final String DOSE_TYPE = "doseType";
    public static final String NAME = "name";
    public static final String TAKEN = "taken";
    public static final String IMAGE = "image";
    public static final String MEDICINE_ID = "medicineId";
    public static final String NURSE_ID = "nurseId";
    public static final String PATIENT_ID = "patientId";
    public static final String TIME = "time";

    private String dose;
    private String doseType;
    private String name;
    private String taken;
    private String image;
    private String medicineId;
    private String nurseId;
    private String patientId;
    private Long time;

    public Medicine() {
    }

    private Medicine(Parcel in) {
        name = in.readString();
        dose = in.readString();
        doseType = in.readString();
        taken = in.readString();
        image = in.readString();
        medicineId = in.readString();
        nurseId = in.readString();
        patientId = in.readString();
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
    }

    public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {
        @Override
        public Medicine createFromParcel(Parcel in) {
            return new Medicine(in);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };

    public String getDoseType() {
        return doseType;
    }

    public void setDoseType(String doseType) {
        this.doseType = doseType;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getTaken() {
        return taken;
    }

    public void setTaken(String taken) {
        this.taken = taken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(dose);
        parcel.writeString(doseType);
        parcel.writeString(taken);
        parcel.writeString(image);
        parcel.writeString(medicineId);
        parcel.writeString(nurseId);
        parcel.writeString(patientId);
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(time);
        }
    }
}
