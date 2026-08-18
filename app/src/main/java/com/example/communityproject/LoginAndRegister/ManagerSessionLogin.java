package com.example.communityproject.LoginAndRegister;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.communityproject.MainActivity;

import java.util.HashMap;

public class ManagerSessionLogin {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "MANAGER_LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String ACCOUNT = "ACCOUNT";
    public static final String USERID = "USERID";
    public static final String IMAGE = "IMAGE";

    HashMap<String, String> user = new HashMap<>();

    public ManagerSessionLogin(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id, String account, String name,  String image) {
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(ACCOUNT, account);
        editor.putString(USERID, id);
        editor.putString(IMAGE, image);
        editor.apply();
    }

    public boolean isLoggin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin() {
        if (!this.isLoggin()) {
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((ManagerCommunityActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail() {
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(ACCOUNT, sharedPreferences.getString(ACCOUNT, null));
        user.put(USERID, sharedPreferences.getString(USERID, null));
        user.put(IMAGE, sharedPreferences.getString(IMAGE, null));
        return user;
    }

    public void logout() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((ManagerCommunityActivity) context).finish();
    }

}
