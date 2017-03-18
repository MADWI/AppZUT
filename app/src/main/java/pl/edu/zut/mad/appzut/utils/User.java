package pl.edu.zut.mad.appzut.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class User {

    public static final String LOGIN_KEY = "login";
    public static final String PASSWORD_KEY = "password";
    private static final String PREFERENCES_FILE_KEY = "pl.edu.zut.mad.appzut.PREFERENCES_FILE_KEY";
    private static User instance;
    private final SharedPreferences preferences;

    private User(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public static User getInstance(Context context) {
        if (instance == null) {
            SharedPreferences preferences =
                    context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
            instance = new User(preferences);
        }
        return instance;
    }

    public void save(String login, String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_KEY, login);
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
    }

    public void remove() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
    }

    public String getSavedLogin() {
        return preferences.getString(LOGIN_KEY, "");
    }

    public String getSavedPassword() {
        return preferences.getString(PASSWORD_KEY, "");
    }

    public boolean isSaved() {
        return preferences.contains(LOGIN_KEY) && preferences.contains(PASSWORD_KEY);
    }
}
