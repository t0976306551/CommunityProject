package com.example.communityproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.communityproject.Acyivity.ActivityAdapter;
import com.example.communityproject.Acyivity.ActivityCardviewData;
import com.example.communityproject.Acyivity.Activity_record_Activity;
import com.example.communityproject.Acyivity.Acyivity_Activity;
import com.example.communityproject.Attraction.AttractionActivity;
import com.example.communityproject.LoginAndRegister.LoginActivity;
import com.example.communityproject.Post.PostActivity;
import com.example.communityproject.Staple.StapleActivity;
import com.example.communityproject.UserCheck.userCheckActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    TextView m_name,m_account,c_name;
    CardView cardview_post,cardview_password,cardview_activity,cardview_activityRecord,cardview_activityattraction,cardview_activitystaple,cardview_userCheck,cardview_logout,cardview_introduce;
    SessionManager sessionManager;
    CircleImageView userImage;
     SwipeRefreshLayout swipeRefreshLayout;
    Context context;
//    CallingDialog callingDialog;
    public String sessionUserID, sessionCommutityId, sessionUserName, sessionCommutityName, sessionUserImage, sessionUserAccount,sessionAuthority;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
//        callingDialog = new CallingDialog(this);
//        callingDialog.success_dialog("登入成功",MainActivity.this);


        sessionUserID = sessionUserData.get(sessionManager.USERID);
        sessionUserAccount = sessionUserData.get(sessionManager.ACCOUNT);
        sessionUserName = sessionUserData.get(sessionManager.NAME);
        sessionUserImage = sessionUserData.get(sessionManager.IMAGE);
        sessionCommutityId = sessionUserData.get(sessionManager.C_ID);
        sessionCommutityName = sessionUserData.get(sessionManager.C_NAME);
        sessionAuthority = sessionUserData.get(sessionManager.AUTHORITY);

        swipeRefreshLayout = findViewById(R.id.gank_swipe_refresh_layout);

        m_name = findViewById(R.id.m_name);
        m_account = findViewById(R.id.m_account);
        c_name = findViewById(R.id.c_name);
        cardview_post = findViewById(R.id.cardview_post);
        cardview_password = findViewById(R.id.cardview_password);
        cardview_activity = findViewById(R.id.cardview_activity);
        cardview_activityRecord = findViewById(R.id.cardview_activityRecord);
        cardview_userCheck = findViewById(R.id.cardview_userCheck);
        cardview_logout = findViewById(R.id.cardview_logout);
        userImage = findViewById(R.id.userImage);
        cardview_activityattraction = findViewById(R.id.cardview_activityattraction);
        cardview_activitystaple = findViewById(R.id.cardview_activitystaple);
        cardview_introduce = findViewById(R.id.cardview_introduce);

        cardview_introduce.setVisibility(View.GONE);

        if(sessionUserImage.equals("")){
            userImage.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(MainActivity.this).load(sessionUserImage).into(userImage);
        }

        m_name.setText(sessionUserName);
        m_account.setText(sessionAuthority);
        c_name.setText(sessionCommutityName);
//        getBasicData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBasicData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if(sessionAuthority.equals("一般會員")){
            cardview_userCheck.setVisibility(View.GONE);
        }

        cardview_introduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, IntroduceActivity.class));
            }
        });

        cardview_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });

        cardview_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Acyivity_Activity.class));
            }
        });

        cardview_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,PasswordUpdate_Activity.class));
            }
        });

        cardview_activityattraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AttractionActivity.class));
            }
        });

        cardview_activitystaple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StapleActivity.class));
            }
        });

        cardview_activityRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Activity_record_Activity.class));
            }
        });

        cardview_userCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, userCheckActivity.class));
            }
        });
        cardview_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_dialog("是否要登出");
            }
        });
    }

    @Override
    protected void onResume() {
        getBasicData();
        super.onResume();
    }

    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(MainActivity.this);
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
                sessionManager.logout();
                dialog.cancel();
            }
        });
    }

    private void getBasicData(){
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        sessionUserID = sessionUserData.get(sessionManager.USERID);
        sessionUserAccount = sessionUserData.get(sessionManager.ACCOUNT);
        sessionUserName = sessionUserData.get(sessionManager.NAME);
        sessionUserImage = sessionUserData.get(sessionManager.IMAGE);
        sessionCommutityId = sessionUserData.get(sessionManager.C_ID);
        sessionCommutityName = sessionUserData.get(sessionManager.C_NAME);
        sessionAuthority = sessionUserData.get(sessionManager.AUTHORITY);
        m_name.setText(sessionUserName);
        m_account.setText(sessionAuthority);
        c_name.setText(sessionCommutityName);
        if(sessionUserImage.equals("")){
            userImage.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(MainActivity.this).load(sessionUserImage).into(userImage);
        }
    }
}