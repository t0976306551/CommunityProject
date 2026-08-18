package com.example.communityproject.Acyivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.communityproject.R;
import com.example.communityproject.SessionManager;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Acyivity_Activity extends AppCompatActivity {

    private View view;
    private List<ActivityCardviewData> list_Activity;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static ActivityAdapter activityAdapter;
    private String URL_ACTIVITYDATA ;
    UrlSetting urlSetting;
    SessionManager sessionManager;
    String userID,c_id ,authority;
    Button insert_activity;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acyivity);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        userID = sessionUserData.get(sessionManager.USERID);
        c_id = sessionUserData.get(sessionManager.C_ID);
        authority = sessionUserData.get(sessionManager.A_ID);
        insert_activity = findViewById(R.id.insert_activity);
//        insert_activity.setVisibility(View.GONE);

        loading = findViewById(R.id.loading);
        if(authority.equals("3")){
            insert_activity.setVisibility(View.GONE);
        }

        list_Activity = new ArrayList<>();
        recyclerView = findViewById(R.id.activityRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if(list_Activity.isEmpty()){
            FirstLoadActivityData();
        }

        insert_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Acyivity_Activity.this, insert_activity_Activity.class);
                intent.putExtra("m_id",userID);
                intent.putExtra("c_id",c_id);
                startActivity(intent);
            }
        });
        swipeRefreshLayout = findViewById(R.id.gank_swipe_refresh_layout);
       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               list_Activity.clear();
               FirstLoadActivityData();
               swipeRefreshLayout.setRefreshing(false);
           }
       });

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(list_Activity.size() != 0){
            list_Activity.clear();
            FirstLoadActivityData();
        }else{
            return;
        }
    }

    private void FirstLoadActivityData(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("c_id", c_id);
        map.put("type","1");
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(Acyivity_Activity.this);
        URL_ACTIVITYDATA = urlSetting.getUrl()+"activity/get";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_ACTIVITYDATA,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String activityUserId = jsonObject.getString("m_id");
                        String activityId = jsonObject.getString("ac_id");
                        String activityName = jsonObject.getString("ac_name");

                        ActivityCardviewData activityCardviewData = new ActivityCardviewData(activityUserId, activityId, activityName);
                        list_Activity.add(activityCardviewData);
                    }
                    activityAdapter = new ActivityAdapter(Acyivity_Activity.this,list_Activity); // 將資料交給adapter
                    activityAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(activityAdapter);// 設置adapter給recyclerView


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Acyivity_Activity.this,
                        "onErrorResponse form FirstLoadActivityData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadActivityData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    public void loadingOn(){
        loading.setVisibility(View.VISIBLE);
    }
    public void loadingOff(){
        loading.setVisibility(View.GONE);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.example_menu,menu);
//        return true;
//    }
}