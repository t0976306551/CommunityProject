package com.example.communityproject.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.communityproject.CreateCommunityActivity;
import com.example.communityproject.MainActivity;
import com.example.communityproject.R;
import com.example.communityproject.SessionManager;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView register,createCommunity;
    EditText account,password;
    Button btn_login;
    private ProgressBar loading;
    UrlSetting urlSetting;
//    private static String URL_LOGIN = "http://192.168.137.209/usr/public/user/login";
    String URL_LOGIN;
    SessionManager sessionManager;
    ManagerSessionLogin managerSessionLogin;
    //保持登入狀態
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loading = findViewById(R.id.loading);
        register = findViewById(R.id.register);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        createCommunity = findViewById(R.id.createCommunity);

        sessionManager = new SessionManager(this);
        managerSessionLogin = new ManagerSessionLogin(this);
        //保持登入狀態
        if(managerSessionLogin.isLoggin()){
            Intent i = new Intent(LoginActivity.this,ManagerCommunityActivity.class);
            startActivity(i);
            finish();
        }

        if(sessionManager.isLoggin()){
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mAccount = account.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                if(!mAccount.isEmpty() || !mPassword.isEmpty()){
                    Login(mAccount,mPassword);
                }else{
                    account.setError("請輸入帳號");
                    password.setError("請輸入密碼");
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        createCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, CreateCommunityActivity.class));
            }
        });


    }
    private void Login(String account,String password) {
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);
        Map<String, String> map = new HashMap<String, String>();
        map.put("account", account);
        map.put("password",password);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(LoginActivity.this);
        URL_LOGIN = urlSetting.getUrl()+"user/login";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOGIN, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String success = response.getString("success");
                    JSONArray jsonArray = response.getJSONArray("data");
                    if(success.equals("5")){
                        for(int i = 0; i<jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("m_id").trim();
                            String name = object.getString("m_name").trim();
                            String account = object.getString("m_acc").trim();
                            String image = object.getString("image").trim();
                            managerSessionLogin.createSession(id, account, name, image);
                            startActivity(new Intent(LoginActivity.this, ManagerCommunityActivity.class));
                            finish();
                        }
                        return;
                    }

                    if(success.equals("1")){
                        for(int i = 0; i<jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String c_id = object.getString("c_id").trim();
                            String c_name = object.getString("c_name").trim();
                            String id = object.getString("m_id").trim();
                            String name = object.getString("m_name").trim();
                            String account = object.getString("m_acc").trim();
                            String image = object.getString("image").trim();
                            String a_id = object.getString("id").trim();
                            String a_name = object.getString("a_name").trim();
                            String user_check = object.getString("user_check").trim();
                            sessionManager.createSession(name, account, id, c_id, image, c_name ,a_id ,a_name);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }else if(success.equals("2")){
                        error_dialog("帳號尚未通過審核");
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                    }else if(success.equals("3")){
                        error_dialog("此帳號審核失敗請重新申請");
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                    }else{
                        error_dialog("帳號或密碼輸入錯誤");
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    loading.setVisibility(View.GONE);
                    btn_login.setVisibility(View.VISIBLE);
                    Log.e("LoginActivity_JSONException",e.toString());
                    error_dialog("請檢查是否輸入錯誤");
                    loading.setVisibility(View.GONE);
                    btn_login.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
                btn_login.setVisibility(View.VISIBLE);
                Log.e("LoginActivity_onErrorResponse",error.toString());
                error_dialog("請檢查是否開啟網路");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.caveat_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView caveat_text = dialog.findViewById(R.id.caveat_text);
        caveat_text.setText("");
        caveat_text.setText(text);
        dialog.show();
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }
}