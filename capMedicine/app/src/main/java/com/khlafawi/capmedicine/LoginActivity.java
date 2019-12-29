package com.khlafawi.capmedicine;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khlafawi.capmedicine.model.Nurse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;
import pref.UserInfo;
import pref.UserSession;

public class LoginActivity extends AppCompatActivity {

    //extras
    private UserInfo userInfo;
    private UserSession session;

    //ui
    private TextView code;
    private Button submit;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userInfo = new UserInfo(this);
        session = new UserSession(this);

        initView();
        initAction();
    }

    private void initView() {
        this.code = findViewById(R.id.code);
        this.submit = findViewById(R.id.submit);
        this.progressDialog = new ProgressDialog(LoginActivity.this);
        this.progressDialog.setMessage(getResources().getString(R.string.registering));
    }

    private void initAction() {
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    private void submit() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (TextUtils.isEmpty(code.getText().toString())) {
            Toast.makeText(LoginActivity.this, "Please enter your code", Toast.LENGTH_LONG).show();
            code.setHintTextColor(getResources().getColor(R.color.red));
            return;
        }

        if (progressDialog != null)
            progressDialog.show();

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot nurseSnapshot :
                        dataSnapshot.child(Common.NURSES).getChildren()) {

                    if (nurseSnapshot.hasChild(Nurse.CODE)) {
                        if (nurseSnapshot.child(Nurse.CODE).getValue().equals(code.getText().toString())) {
                            if (nurseSnapshot.hasChild(Nurse.NURSE_ID)) {
                                Toasty.success(getApplicationContext(), getResources().getString(R.string.authenticated_successfully), Toast.LENGTH_SHORT, true).show();
                                validate((String) nurseSnapshot.child(Nurse.NURSE_ID).getValue(), (String) nurseSnapshot.child(Nurse.GCM_ID).getValue());
                                return;
                            }
                        }
                    }
                }

                if (progressDialog != null)
                    progressDialog.dismiss();

                Toasty.error(getApplicationContext(), getResources().getString(R.string.no_ids_found), Toast.LENGTH_LONG, true).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getApplicationContext(), getResources().getString(R.string.check_your_internet), Toast.LENGTH_LONG, true).show();
            }
        });
    }

    private void validate(String UID, String GCM_ID) {
        userInfo.setId(UID);
        userInfo.setGCMId(GCM_ID);
        session.setLogged(true);

        if (progressDialog != null)
            progressDialog.dismiss();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
