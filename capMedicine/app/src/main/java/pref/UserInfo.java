package pref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo {

    private static final String PREF_NAME = "com.khlafawi.capmedicine.userInfo";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static final String USER_ID = "id";
    private static final String GCM_ID = "iGCM";

    @SuppressLint("CommitPrefEdits")
    public UserInfo(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setId(String id) {
        editor.putString(USER_ID, id);
        editor.apply();
    }


    public String getId() {
        return prefs.getString(USER_ID, "");
    }

    public void setGCMId(String gcm) {
        editor.putString(GCM_ID, gcm);
        editor.apply();
    }


    public String getGCM() {
        return prefs.getString(GCM_ID, "");
    }


    public void clear() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
