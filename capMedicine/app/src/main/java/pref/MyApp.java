package pref;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khlafawi.capmedicine.Common;
import com.khlafawi.capmedicine.InformingActivity;
import com.khlafawi.capmedicine.MainActivity;
import com.khlafawi.capmedicine.R;
import com.khlafawi.capmedicine.model.Medicine;
import com.khlafawi.capmedicine.model.Nurse;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class MyApp extends Application {

    public static final String TAG = MyApp.class.getSimpleName();

    private static int count = 0;
    private UserInfo userInfo;
    private static MyApp mInstance;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        userInfo = new UserInfo(this);
        createNotificationChannel(this);
    }

    public void createNotificationChannel(@NonNull Context context) {

        int NOTIFICATION_COLOR = context.getResources().getColor(R.color.colorAccent);
        //Uri NOTIFICATION_SOUND_URI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = context.getString(R.string.channel_id);
            String channelName = getResources().getString(R.string.app_name);

            AudioAttributes.Builder attr = new AudioAttributes.Builder();
            attr.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);

            NotificationChannel channel = new NotificationChannel(channelId, channelName, IMPORTANCE_HIGH);

            //channel.setSound(NOTIFICATION_SOUND_URI, attr.build());
            channel.setLightColor(NOTIFICATION_COLOR);
            channel.enableVibration(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    public void sendHeartBeat(final UserInfo userInfo) {


        FirebaseDatabase.getInstance().getReference().child(Common.MEDICINES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot medicineShot :
                        dataSnapshot.getChildren()) {

                    if (medicineShot.hasChild(Medicine.TIME)) {
                        long currentTime = System.currentTimeMillis() / 1000L;
                        long medicineTime = (Long) medicineShot.child(Medicine.TIME).getValue();

//                        Log.e(MyApp.class.getSimpleName(), "Medicine Time: " + medicineTime);
//                        Log.e(MyApp.class.getSimpleName(), "Current Time: " + currentTime);
//                        Log.e(MyApp.class.getSimpleName(), "Name" + medicineShot.child(Medicine.NAME).getValue());
                        int diffInMinutes = (int) (medicineTime - currentTime);

//                        if (diffInMinutes < 0)
//                            diffInMinutes = diffInMinutes * -1;

//                        Log.e(MyApp.class.getSimpleName(), "diffInMinutes: " + diffInMinutes);
//                        Log.e(MyApp.class.getSimpleName(), "===============================");

                        if (diffInMinutes <= 5000 && diffInMinutes > 0 && userInfo.getId().equals(medicineShot.child(Medicine.NURSE_ID).getValue()) && "no".equals(medicineShot.child(Medicine.TAKEN).getValue())) {
                            Medicine medicine = new Medicine();

                            medicine.setMedicineId((String) medicineShot.child(Medicine.MEDICINE_ID).getValue());
                            medicine.setNurseId((String) medicineShot.child(Medicine.NURSE_ID).getValue());
                            medicine.setPatientId((String) medicineShot.child(Medicine.PATIENT_ID).getValue());
                            medicine.setDose(String.valueOf(medicineShot.child(Medicine.DOSE).getValue()));
                            medicine.setDoseType(String.valueOf(medicineShot.child(Medicine.DOSE_TYPE).getValue()));
                            medicine.setName((String) medicineShot.child(Medicine.NAME).getValue());
                            medicine.setImage((String) medicineShot.child(Medicine.IMAGE).getValue());

                            informTheUser(medicine);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void informTheUser(Medicine medicine) {
        if (!InformingActivity.active) {
            Intent intent = new Intent(getApplicationContext(), InformingActivity.class);
            intent.putExtra(InformingActivity.MEDICINE, medicine);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void pushNotification(final String patientName) {
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://admin.wanas.fm/push_notification.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(MyApp.class.getSimpleName(), "Response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(MyApp.class.getSimpleName(), "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                UserInfo userInfo = new UserInfo(getApplicationContext());
                String GCM = userInfo.getGCM();

//                Log.e(TAG, "==============================");
//                Log.e(TAG, "iGCM" + GCM);
//                Log.e(TAG, "title" + "Cap Nurse");
//                Log.e(TAG, "body" + patientName + "Took the medicine.");
//                Log.e(TAG, "key" + "AIzaSyAEhOT5ilKeCgTluVvsY92X-cQYR3pwGIg");
//                Log.e(TAG, "==============================");

                params.put("iGCM", GCM);
                params.put("title", "Cap Nurse");
                params.put("body", patientName + " took the medicine.");
                params.put("key", "AIzaSyAEhOT5ilKeCgTluVvsY92X-cQYR3pwGIg");
                return params;
            }
        };

        // Adding request to request queue
        MyApp.getInstance().addToRequestQueue(strReq, "REQUEST");
    }

    public static void sendPushNotification(Context mContext, String message, String title) {

        Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //For Android Version Orio and greater than orio.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(mContext.getResources().getString(R.string.channel_id), mContext.getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(message);
            mChannel.enableLights(true);
            mChannel.setLightColor(mContext.getColor(R.color.colorAccent));
            mChannel.enableVibration(true);

            assert mNotifyManager != null;
            mNotifyManager.createNotificationChannel(mChannel);
        }

        //For Android Version lower than orio.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, mContext.getResources().getString(R.string.channel_id));
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_foreground))
                .setAutoCancel(true)
                .setColor(mContext.getResources().getColor(R.color.colorAccent))
                .setContentIntent(pendingIntent)
                .setChannelId(mContext.getResources().getString(R.string.channel_id))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        assert mNotifyManager != null;
        mNotifyManager.notify(count, mBuilder.build());
        count++;
    }
}
