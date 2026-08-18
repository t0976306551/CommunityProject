package com.example.communityproject.Attraction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class AttractionActivity extends AppCompatActivity {
    private View view;
    Button btn_insert;
    String m_id,c_id,authority;
    SessionManager sessionManager;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static AttractionAdapter attractionAdapter;
    private List<AttractionCardviewData> list_data;
    UrlSetting urlSetting;
    private String URL_ATTRACTIONDATA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction);
        btn_insert = findViewById(R.id.btn_insert);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        m_id = sessionUserData.get(sessionManager.USERID);
        c_id = sessionUserData.get(sessionManager.C_ID);
        authority = sessionUserData.get(sessionManager.A_ID);
        if(authority.equals("3")){
            btn_insert.setVisibility(View.GONE);
        }else{
            btn_insert.setVisibility(View.VISIBLE);
        }
        //set recyclerView type
        list_data = new ArrayList<>();
        recyclerView = findViewById(R.id.attractionRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if(list_data.isEmpty()){
            loadAttractionData();
        }

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttractionActivity.this, insert_attraction_Activity.class);
                intent.putExtra("m_id",m_id);
                intent.putExtra("c_id",c_id);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = findViewById(R.id.gank_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list_data.clear();
                loadAttractionData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    //回到原頁面 重新刷新資料
    @Override
    protected void onResume() {
        super.onResume();
        if(list_data.size() != 0){
            list_data.clear();
            loadAttractionData();
        }else{
            return;
        }
    }

    private  void loadAttractionData(){
        urlSetting = new UrlSetting(AttractionActivity.this);
        URL_ATTRACTIONDATA = urlSetting.getUrl()+"attraction/"+"?c_id="+c_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_ATTRACTIONDATA,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("loadpost", String.valueOf(response));
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String a_id = jsonObject.getString("a_id");
                        String a_name = jsonObject.getString("a_name");
                        String a_context = jsonObject.getString("a_context");
                        String insert_time = jsonObject.getString("insert_time");
                        String m_id = jsonObject.getString("m_id");
                        String m_name = jsonObject.getString("m_name");
                        JSONArray a_image = jsonObject.getJSONArray("images");
                        AttractionCardviewData attractionCardviewData = new AttractionCardviewData(a_id, a_name, a_image);
                        list_data.add(attractionCardviewData);
                    }
                    attractionAdapter = new AttractionAdapter(AttractionActivity.this,list_data); // 將資料交給adapter
                    recyclerView.setAdapter(attractionAdapter);// 設置adapter給recyclerView

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AttractionActivity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }


}