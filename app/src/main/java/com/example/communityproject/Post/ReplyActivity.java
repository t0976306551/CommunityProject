package com.example.communityproject.Post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.communityproject.PasswordUpdate_Activity;
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyActivity extends AppCompatActivity {
    private static String URL_LOADREPLY;
    private static String URL_INSERTREPLY;
    UrlSetting urlSetting;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static ReplyAdapter replyAdapter;

    String postId,userId;
    CircleImageView btn_return;
    Button btn_repelys;
    private RecyclerView recyclerView;
    private View view;
    private List<ReplyCardViewDate> list_reply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        btn_return = findViewById(R.id.btn_return);
        btn_repelys = findViewById(R.id.btn_repelys);

        Bundle bundle = getIntent().getExtras();
        postId = bundle.getString("p_id");
        userId = bundle.getString("m_id");

        list_reply = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.replyRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        FirstLoadReply();


        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_repelys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog;
                dialog = new Dialog(ReplyActivity.this);
                dialog.setContentView(R.layout.reply_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                EditText reply_text = dialog.findViewById(R.id.reply_text);
                Button btn_reply = dialog.findViewById(R.id.btn_reply);

                Window dialogWindow = dialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
                layoutParams.y = 20;
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(layoutParams);
                dialogWindow.setAttributes(layoutParams);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlide;
                dialog.show();

                btn_reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(reply_text.getText().toString().equals("")){
                            Toast.makeText(ReplyActivity.this,"欄位不可空白",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        insertReply(reply_text.getText().toString());
                        dialog.cancel();
                    }
                });

            }
        });

        swipeRefreshLayout = findViewById(R.id.gank_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list_reply.clear();
                FirstLoadReply();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(list_reply.size() != 0){
            list_reply.clear();
            FirstLoadReply();
        }else{
            return;
        }
    }

    // load 留言 start
    private void FirstLoadReply(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("p_id",postId);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(ReplyActivity.this);
        URL_LOADREPLY = urlSetting.getUrl()+"reply/load";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOADREPLY,data
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String r_id = jsonObject.getString("r_id");
                        String username = jsonObject.getString("m_name");
                        String reply = jsonObject.getString("reply");
                        String insertDate = jsonObject.getString("insert_date");
                        String imageUrl = jsonObject.getString("image");
                        String p_id = jsonObject.getString("p_id");
                        String m_id = jsonObject.getString("m_id");
                         ReplyCardViewDate replyCardViewDate = new ReplyCardViewDate(r_id, username, reply,insertDate,imageUrl,p_id,m_id,"reply");
                         list_reply.add(replyCardViewDate);
                    }
                    replyAdapter = new ReplyAdapter(ReplyActivity.this,list_reply);
                    recyclerView.setAdapter(replyAdapter);

                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e("postReply_FirstLaodReply_JSONException Error!",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ReplyActivity.this, "FirstLaodReply_onErrorResponse Error!"+error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("FirstLaodReply_JSONException Error!",error.toString());
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }



    private void insertReply(String reply) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("reply", reply);
        map.put("m_id", userId);
        map.put("p_id",postId);
        JSONObject data = new JSONObject(map);

        urlSetting = new UrlSetting(ReplyActivity.this);
        URL_INSERTREPLY = urlSetting.getUrl()+"reply/create";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_INSERTREPLY, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String success = response.getString("success");
                    if(success.equals("1")){
                        list_reply.clear();
                        FirstLoadReply();
                    }else{
                        Toast.makeText(ReplyActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e("insertActivity()_JSONException Error", e.toString());
                    Toast.makeText(ReplyActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

}