package com.khlafawi.capmedicine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khlafawi.capmedicine.model.Feedback;
import com.khlafawi.capmedicine.model.Medicine;
import com.khlafawi.capmedicine.model.Patient;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import pref.MyApp;
import pref.UserInfo;

public class InformingActivity extends AppCompatActivity {

    public static final String MEDICINE = "medicine";

    public static boolean active = false;

    private UserInfo userInfo;

    private TextView medicine_name, medicine_dose, patient_name;
    private ImageView medicine_image;
    private Button finish_medicine;

    private Medicine medicine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informing);

        if (getIntent().hasExtra(MEDICINE)) {
            medicine = getIntent().getParcelableExtra(MEDICINE);
        }

        userInfo = new UserInfo(this);

        initView();
        initMedicine();
        initActions();
        unlockScreen();
        initSound();
    }

    private void initSound() {
        if (SoundService.getInstance() == null) {
            startService(new Intent(this, SoundService.class));
        } else {
            if (SoundService.getInstance().getMediaPlayer() != null) {
                if (!SoundService.getInstance().getMediaPlayer().isPlaying()) {
                    SoundService.getInstance().getMediaPlayer().start();
                }
            }
        }
    }

    private void unlockScreen() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void initView() {
        this.medicine_name = findViewById(R.id.medicine_name);
        this.medicine_dose = findViewById(R.id.medicine_dose);
        this.medicine_image = findViewById(R.id.medicine_image);
        this.patient_name = findViewById(R.id.patient_name);
        this.finish_medicine = findViewById(R.id.finish_medicine);
    }

    @SuppressLint("SetTextI18n")
    private void initMedicine() {
        this.medicine_name.setText(medicine.getName());
        this.medicine_dose.setText(medicine.getDoseType() + " " + medicine.getDose());
        if (medicine.getPatientId() != null)
            FirebaseDatabase.getInstance().getReference().child(Common.PATIENTS).child(medicine.getPatientId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(Patient.NAME))
                        patient_name.setText((String) dataSnapshot.child(Patient.NAME).getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        Picasso.get().load(medicine.getImage()).into(this.medicine_image);
    }

    private void initActions() {
        this.finish_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference feedRef = FirebaseDatabase.getInstance().getReference().child(Common.FEEDBACK);
                final String feedKey = feedRef.push().getKey();

                feedRef.child(feedKey).child(Feedback.ID).setValue(feedKey);
                feedRef.child(feedKey).child(Feedback.NURSE_ID).setValue(medicine.getNurseId());
                feedRef.child(feedKey).child(Feedback.PATIENT_ID).setValue(medicine.getPatientId());
                feedRef.child(feedKey).child(Feedback.TOOK).setValue(true);

                if (medicine.getPatientId() != null) {

                    FirebaseDatabase.getInstance().getReference().child(Common.PATIENTS).child(medicine.getPatientId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(Patient.NAME)) {
                                feedRef.child(feedKey).child(Feedback.PATIENT_NAME).setValue(dataSnapshot.child(Patient.NAME).getValue());
                                MyApp.getInstance().pushNotification((String) dataSnapshot.child(Patient.NAME).getValue());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


                FirebaseDatabase.getInstance().getReference().child(Common.MEDICINES).child(medicine.getMedicineId()).child(Medicine.TAKEN).setValue("yes");
                startActivity(new Intent(InformingActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                if (SoundService.getInstance() != null) {
                    SoundService.getInstance().onDestroy();
                    SoundService.getInstance().getMediaPlayer().stop();
                    SoundService.getInstance().getMediaPlayer().release();
                }
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}
