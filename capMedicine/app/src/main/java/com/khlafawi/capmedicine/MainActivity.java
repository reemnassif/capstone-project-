package com.khlafawi.capmedicine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import pref.UserInfo;
import pref.UserSession;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.khlafawi.capmedicine.model.Medicine;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    //extras
    private UserInfo userInfo;
    private UserSession session;
    Intent mServiceIntent;

    //ui
    private RecyclerView medicines_recycler_view;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInfo = new UserInfo(this);
        session = new UserSession(this);

        ScheduledService medicineService = new ScheduledService();
        mServiceIntent = new Intent(this, medicineService.getClass());
        if (!isMyServiceRunning(medicineService.getClass())) {
            startService(mServiceIntent);
        }

        initView();
        initActions();
        getMedicines();
        unlockScreen();
    }

    private void unlockScreen() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void initView() {
        this.medicines_recycler_view = findViewById(R.id.medicines_recycler_view);
        this.refresh = findViewById(R.id.refresh);
    }

    private void initActions() {
        this.refresh.setOnRefreshListener(this);
    }

    private void getMedicines() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.MEDICINES)
                .orderByChild(Medicine.NURSE_ID)
                .equalTo(userInfo.getId())
                .limitToLast(50);

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Medicine, MedicineViewHolder>(Medicine.class, R.layout.item_medicine, MedicineViewHolder.class, query) {

            @SuppressLint("SetTextI18n")
            @Override
            protected void populateViewHolder(MedicineViewHolder viewHolder, Medicine model, int position) {
                Picasso.get().load(model.getImage()).into(viewHolder.medicine_image);
                viewHolder.medicineName.setText(model.getName());
                viewHolder.medicineDose.setText(model.getDose().toString());
            }
        };

        medicines_recycler_view.setHasFixedSize(true);
        medicines_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        medicines_recycler_view.setAdapter(adapter);
        refresh.setRefreshing(false);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    @Override
    public void onRefresh() {
        getMedicines();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        userInfo.clear();
        session.setLogged(false);
        ScheduledService medicineService = new ScheduledService();
        mServiceIntent = new Intent(this, medicineService.getClass());
        if (!isMyServiceRunning(medicineService.getClass())) {
            stopService(mServiceIntent);
        }
        startActivity(new Intent(this, LauncherActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}
