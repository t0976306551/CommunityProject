package com.example.communityproject.Post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.communityproject.LoginAndRegister.LoginActivity;
import com.example.communityproject.LoginAndRegister.RegisterActivity;
import com.example.communityproject.R;
import com.example.communityproject.SessionManager;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private View view;
    private List<PostCardviewData> list_post;
    private SwipeRefreshLayout swipeRefreshLayout;
    UrlSetting urlSetting;
    private String URL_POSTDATA ;
    private String URL_SEARCH;
    private static PostAdapter postAdapter;
    SessionManager sessionManager;
    String c_id,m_id;
    Button insert_post,btn_search;
    EditText search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        insert_post = findViewById(R.id.insert_post);
        btn_search = findViewById(R.id.btn_search);
        search = findViewById(R.id.search);

        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        m_id = sessionUserData.get(sessionManager.USERID);
        c_id = sessionUserData.get(sessionManager.C_ID);

        list_post = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.postRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // click insert post
        insert_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(PostActivity.this,R.anim.click_style);
                insert_post.startAnimation(animation);
                Intent intent = new Intent(PostActivity.this, insert_post_Activity.class);
                intent.putExtra("m_id",m_id);
                intent.putExtra("c_id",c_id);
                startActivity(intent);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyWord = search.getText().toString();
                if(keyWord.equals("")){
                    error_dialog("未輸入貼文標題");
                }else{
                    list_post.clear();
                    searchPost(keyWord);
                }
            }
        });

        swipeRefreshLayout = findViewById(R.id.gank_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list_post.clear();
                FirstLoadPostData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        if(list_post.isEmpty()){
            FirstLoadPostData();
        }



//        else if(!list_Post.isEmpty()){
//            insertLoadPostData();
//        }
    }
    //Load Post data from database start

    //回到原頁面 重新刷新資料
    @Override
    protected void onResume() {
        super.onResume();
        if(list_post.size() != 0){
//            list_post.clear();
//            FirstLoadPostData();
        }else{
            return;
        }
    }


    private void FirstLoadPostData(){
        urlSetting = new UrlSetting(PostActivity.this);
        URL_POSTDATA = urlSetting.getUrl()+"post/"+"?c_id="+c_id;
//        String newURL_POSTDATA = URL_POSTDATA+"?c_id="+c_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_POSTDATA,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String postUserId = jsonObject.getString("m_id");
                        String postUserName = jsonObject.getString("m_name");
                        String postTitle = jsonObject.getString("p_title");
                        String postContext = jsonObject.getString("p_context");
                        String postInsertTime = jsonObject.getString("insert_date");
                        String postId = jsonObject.getString("p_id");
                        String r_check = jsonObject.getString("reply_check");
                        String postImage = jsonObject.getString("m_image");
                        String reply_count = jsonObject.getString("reply_count");
                        JSONArray p_images = jsonObject.getJSONArray("images");
                        PostCardviewData postCardviewData = new PostCardviewData(postUserId, postUserName, postTitle, postContext, postInsertTime, postId,r_check,postImage,p_images,reply_count);
                        list_post.add(postCardviewData);
                    }
                    postAdapter = new PostAdapter(PostActivity.this,list_post); // 將資料交給adapter
                    recyclerView.setAdapter(postAdapter);// 設置adapter給recyclerView

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostActivity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    //Load Post data from database end

    //search post
    private void searchPost(String keyword){
        urlSetting = new UrlSetting(PostActivity.this);
        URL_SEARCH = urlSetting.getUrl()+"post/search";
        list_post.clear();
        JSONObject datas = new JSONObject();
        try{
            datas.put("c_id",c_id);
            datas.put("keyword",keyword);
        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SEARCH,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("loadpost", String.valueOf(response));
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String postUserId = jsonObject.getString("m_id");
                        String postUserName = jsonObject.getString("m_name");
                        String postTitle = jsonObject.getString("p_title");
                        String postContext = jsonObject.getString("p_context");
                        String postInsertTime = jsonObject.getString("insert_date");
                        String postId = jsonObject.getString("p_id");
                        String r_check = jsonObject.getString("reply_check");
                        String postImage = jsonObject.getString("m_image");
                        String reply_count = jsonObject.getString("reply_count");
                        JSONArray p_images = jsonObject.getJSONArray("images");
                        PostCardviewData postCardviewData = new PostCardviewData(postUserId, postUserName, postTitle, postContext, postInsertTime, postId,r_check,postImage,p_images,reply_count);
                        list_post.add(postCardviewData);
                    }
                    postAdapter = new PostAdapter(PostActivity.this,list_post); // 將資料交給adapter
                    recyclerView.setAdapter(postAdapter);// 設置adapter給recyclerView

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostActivity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(PostActivity.this);
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