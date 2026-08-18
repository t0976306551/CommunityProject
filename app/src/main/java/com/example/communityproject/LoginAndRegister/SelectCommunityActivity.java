package com.example.communityproject.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SelectCommunityActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    UrlSetting urlSetting;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static CommunityAdapter communityAdapter;
    private List<CommunityData> list_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_community);
        recyclerView = findViewById(R.id.communityRecyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("請選擇社區");
        setSupportActionBar(toolbar);

        list_data = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        loadData();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.example_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("利用關鍵字搜尋");

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<CommunityData> newList = new ArrayList<>();
                for(CommunityData communityData:list_data){
                    String name = communityData.getName();
                    if(name.contains(newText)){
                        newList.add(communityData);
                    }
                }
                communityAdapter.setFilter(newList);
                return true;
            }
        };
       searchView.setOnQueryTextListener(queryTextListener);
       return true;
    }


    private void loadData(){
        urlSetting = new UrlSetting(SelectCommunityActivity.this);
        String URL_GETDATA = urlSetting.getUrl()+"user/getCommunity";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_GETDATA,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String c_id = jsonObject.getString("c_id");
                        String c_name = jsonObject.getString("name");
                        String c_address = jsonObject.getString("address");
                        Log.v("123123", c_name);
                        CommunityData communityData = new CommunityData(c_id,c_name,c_address);
                        list_data.add(communityData);
                    }
                    communityAdapter = new CommunityAdapter(SelectCommunityActivity.this,list_data); // 將資料交給adapter
                    recyclerView.setAdapter(communityAdapter);// 設置adapter給recyclerView

                    

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SelectCommunityActivity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }


}