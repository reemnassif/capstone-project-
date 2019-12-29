package pref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static final String PREF_NAME = "com.khlafawi.capmedicine.pref";
    private static final String KEY_IS_LOGGED_IN = "com.khlafawi.capmedicine.logged.in";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public UserSession(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLogged(boolean isLogged) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLogged);
        editor.apply();
    }

    public boolean isUserLogged() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}
