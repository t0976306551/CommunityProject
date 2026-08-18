package com.example.communityproject.Staple;

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

public class StapleActivity extends AppCompatActivity {
    Button btn_insert;
    SessionManager sessionManager;
    String m_id,c_id,authority;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static StapleAdapter stapleAdapter;
    private List<StapleCardViewData> list_staple;
    UrlSetting urlSetting;
    private String URL_STAPLEDATA ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staple);
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
        list_staple = new ArrayList<>();
        recyclerView = findViewById(R.id.stapleRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);



        if(list_staple.isEmpty()){
            loadStapleData();
        }

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StapleActivity.this, insert_staple_Activity.class);
                intent.putExtra("m_id",m_id);
                intent.putExtra("c_id",c_id);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = findViewById(R.id.gank_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list_staple.clear();
                loadStapleData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(list_staple.size() != 0){
            list_staple.clear();
            loadStapleData();
        }else{
            return;
        }
    }

    private void loadStapleData(){
        urlSetting = new UrlSetting(StapleActivity.this);
        URL_STAPLEDATA = urlSetting.getUrl()+"staple/"+"?c_id="+c_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_STAPLEDATA,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String s_id = jsonObject.getString("s_id");
                        String s_name = jsonObject.getString("s_name");
                        JSONArray s_image = jsonObject.getJSONArray("images");
                        StapleCardViewData stapleCardViewData = new StapleCardViewData(s_id, s_name, s_image);
                        list_staple.add(stapleCardViewData);
                    }
                    stapleAdapter = new StapleAdapter(StapleActivity.this,list_staple); // 將資料交給adapter
                    stapleAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(stapleAdapter);// 設置adapter給recyclerView

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StapleActivity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}