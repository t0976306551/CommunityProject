package com.example.communityproject.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
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
import com.example.communityproject.CreateCommunityActivity;
import com.example.communityproject.MainActivity;
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManagerCommunityActivity extends AppCompatActivity {
    Button btn_logout;
    ManagerSessionLogin managerSessionLogin;
    RecyclerView recyclerView;
    UrlSetting urlSetting;
    private List<CreateCommunityData> list_data;
    private static CreateCommunityAdapter createCommunityAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_community);
        btn_logout = findViewById(R.id.btn_logout);
        managerSessionLogin = new ManagerSessionLogin(this);
        recyclerView = findViewById(R.id.createRecyclerView);
        swipeRefreshLayout = findViewById(R.id.gank_swipe_refresh_layout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_dialog("是否要登出");
            }
        });

        list_data = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        loadData();

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
        urlSetting = new UrlSetting(ManagerCommunityActivity.this);
        String URL_GETDATA = urlSetting.getUrl()+"user/selectCreateCommunity";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_GETDATA,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String community_name = jsonObject.getString("community_name");
                        String community_address = jsonObject.getString("community_address");
                        String manager_account = jsonObject.getString("manager_account");
                        String manager_password = jsonObject.getString("manager_password");
                        String manager_phone = jsonObject.getString("manager_phone");
                        String manager_image = jsonObject.getString("manager_image");
                        String manager_name = jsonObject.getString("manager_name");
                        String manager_sex = jsonObject.getString("manager_sex");
                        CreateCommunityData createCommunityData = new CreateCommunityData(id,community_name,community_address,manager_account,manager_password,manager_phone,manager_image,manager_name,manager_sex);
                        list_data.add(createCommunityData);
                    }
                    createCommunityAdapter = new CreateCommunityAdapter(ManagerCommunityActivity.this,list_data); // 將資料交給adapter
                    recyclerView.setAdapter(createCommunityAdapter);// 設置adapter給recyclerView

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ManagerCommunityActivity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }



    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(ManagerCommunityActivity.this);
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
                managerSessionLogin.logout();
                dialog.cancel();
            }
        });
    }
}