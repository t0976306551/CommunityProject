package com.example.communityproject.Acyivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;
import com.example.communityproject.UserCheck.UserCheckAdapter;
import com.example.communityproject.UserCheck.UsercheckCardViewData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectPeopleActivity extends AppCompatActivity {
    String ac_id;
    private RecyclerView recyclerView;
    private View view;
    private List<UsercheckCardViewData> list_data;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static SelectAdapter selectAdapter;
    UrlSetting urlSetting;
    private String URL_SELECTPEOPLE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_people);
        Intent intent = this.getIntent();
        ac_id = intent.getStringExtra("ac_id");

        list_data = new ArrayList<>();
        recyclerView = findViewById(R.id.userCheckRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        loadData();

        //往下滑動刷新資料
        swipeRefreshLayout = findViewById(R.id.gank_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list_data.clear();
                loadData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void loadData(){
        JSONObject datas = new JSONObject();
        try{
            datas.put("ac_id",ac_id);

        }catch (JSONException e){
            e.printStackTrace();
        }
        urlSetting = new UrlSetting(SelectPeopleActivity.this);
        URL_SELECTPEOPLE = urlSetting.getUrl()+"activity/select";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SELECTPEOPLE,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String UserId = jsonObject.getString("m_id");
                        String UserName = jsonObject.getString("m_name");
                        String UserImage = jsonObject.getString("image");
                        String a_name = jsonObject.getString("a_name");

                        UsercheckCardViewData usercheckCardViewData = new UsercheckCardViewData(UserId, UserName, UserImage,a_name);
                        list_data.add(usercheckCardViewData);
                    }
                    selectAdapter = new SelectAdapter(SelectPeopleActivity.this,list_data); // 將資料交給adapter
                    recyclerView.setAdapter(selectAdapter);// 設置adapter給recyclerView

                    selectAdapter = new SelectAdapter(SelectPeopleActivity.this,list_data); // 將資料交給adapter
                    recyclerView.setAdapter(selectAdapter);// 設置adapter給recyclerView

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SelectPeopleActivity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SelectPeopleActivity.this);
        requestQueue.add(jsonObjectRequest);
    }
}