package com.example.communityproject.Acyivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
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
import com.example.communityproject.Post.UpdatePostActivity;
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

public class Activity_record_Activity extends AppCompatActivity {
    private View view;
    private List<RecordCardViewData> list_Activity;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static RecordAdapter recordAdapter;
    UrlSetting urlSetting;
    private String URL_ACTIVITYDATA;
    SessionManager sessionManager;
    String userID,c_id ,authority;
    Button insert_activity;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        userID = sessionUserData.get(sessionManager.USERID);
        c_id = sessionUserData.get(sessionManager.C_ID);

        list_Activity = new ArrayList<>();
        recyclerView = findViewById(R.id.activityrecordRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if(list_Activity.isEmpty()){
            FirstLoadActivityData();
        }


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
    private void FirstLoadActivityData(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("c_id", c_id);
        map.put("type","2");
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(Activity_record_Activity.this);
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
                        RecordCardViewData recordCardViewData = new RecordCardViewData(activityUserId, activityId, activityName);
                        list_Activity.add(recordCardViewData);
                    }
                    recordAdapter = new RecordAdapter(Activity_record_Activity.this,list_Activity); // 將資料交給adapter
                    recordAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(recordAdapter);// 設置adapter給recyclerView

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Activity_record_Activity.this,
                        "onErrorResponse form FirstLoadActivityData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadActivityData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}