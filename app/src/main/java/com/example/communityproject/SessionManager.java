package com.example.communityproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.communityproject.LoginAndRegister.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String ACCOUNT = "ACCOUNT";
    public static final String USERID = "USERID";
    public static final String C_ID = "C_ID";
    public static final String C_NAME = "C_NAME";
    public static final String IMAGE = "IMAGE";
    public static final String A_ID = "A_ID";
    public static final String AUTHORITY = "AUTHORITY";
    HashMap<String, String> user = new HashMap<>();
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String name, String account, String id, String c_id, String image, String c_name , String a_id ,String a_name) {
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(ACCOUNT, account);
        editor.putString(USERID, id);
        editor.putString(C_ID, c_id);
        editor.putString(C_NAME, c_name);
        editor.putString(IMAGE, image);
        editor.putString(A_ID, a_id);
        editor.putString(AUTHORITY,a_name);
        editor.apply();
    }

    public void update(String Key , String Value){
        editor.remove(Key);
        editor.putString(Key,Value);
        editor.apply();
    }

    public boolean isLoggin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin() {
        if (!this.isLoggin()) {
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail() {
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(ACCOUNT, sharedPreferences.getString(ACCOUNT, null));
        user.put(USERID, sharedPreferences.getString(USERID, null));
        user.put(C_ID, sharedPreferences.getString(C_ID, null));
        user.put(C_NAME, sharedPreferences.getString(C_NAME, null));
        user.put(IMAGE, sharedPreferences.getString(IMAGE, null));
        user.put(A_ID, sharedPreferences.getString(A_ID, null));
        user.put(AUTHORITY, sharedPreferences.getString(AUTHORITY, null));
        return user;
    }


    public void logout() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();
    }

}
