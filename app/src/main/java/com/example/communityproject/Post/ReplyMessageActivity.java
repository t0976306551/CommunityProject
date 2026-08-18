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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyMessageActivity extends AppCompatActivity {
    CircleImageView userImage;
    TextView uesrname,inserdate,replycontext;
    String replyId,userID,post_id;
    Button btn_replyMessage;
    SessionManager sessionManager;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static ReplyAdapter replyAdapter;
    private View view;
    private List<ReplyCardViewDate> list_reply;
    UrlSetting urlSetting;
    private static String URL_INSERTREPLY ;
    private static String URL_LOADREPLY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_message);
        userImage = findViewById(R.id.userImage);
        uesrname = findViewById(R.id.uesrname);
        inserdate = findViewById(R.id.inserdate);
        replycontext = findViewById(R.id.replycontext);
        btn_replyMessage = findViewById(R.id.btn_replyMessage);

        sessionManager = new SessionManager(ReplyMessageActivity.this);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        userID = sessionUserData.get(sessionManager.USERID);

        Bundle bundle = getIntent().getExtras();
        replyId = bundle.getString("reply_id");
        uesrname.setText(bundle.getString("userName"));
        inserdate.setText(bundle.getString("insertDate"));
        replycontext.setText(bundle.getString("replyContext"));
        post_id = bundle.getString("post_id");
        if(bundle.getString("userImage").equals("")){
            userImage.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(ReplyMessageActivity.this).load(bundle.getString("userImage")).into(userImage);
        }

        list_reply = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.replyRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        FirstLoadReply();

        swipeRefreshLayout = findViewById(R.id.gank_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list_reply.clear();
                FirstLoadReply();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        btn_replyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog;
                dialog = new Dialog(ReplyMessageActivity.this);
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
                dialog.show();

                btn_reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(reply_text.getText().toString().equals("")){
                            Toast.makeText(ReplyMessageActivity.this,"欄位不可空白",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        insertReply(reply_text.getText().toString());
                        dialog.cancel();
                    }
                });

            }
        });
    }

    private void FirstLoadReply(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("r_id",replyId);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(ReplyMessageActivity.this);
        URL_LOADREPLY = urlSetting.getUrl()+"reply/loadReplyMessage";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOADREPLY,data
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String username = jsonObject.getString("m_name");
                        String reply = jsonObject.getString("reply");
                        String insertDate = jsonObject.getString("insert_date");
                        String imageUrl = jsonObject.getString("image");
                        String r_id = jsonObject.getString("r_id");
                        String m_id = jsonObject.getString("m_id");
                        ReplyCardViewDate replyCardViewDate = new ReplyCardViewDate(id, username, reply,insertDate,imageUrl,r_id,m_id,"replyMessage");
                        list_reply.add(replyCardViewDate);
                    }
                    replyAdapter = new ReplyAdapter(ReplyMessageActivity.this,list_reply);
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
                Toast.makeText(ReplyMessageActivity.this, "FirstLaodReply_onErrorResponse Error!"+error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("FirstLaodReply_JSONException Error!",error.toString());
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void insertReply(String reply) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("reply", reply);
        map.put("r_id", replyId);
        map.put("m_id",userID);
        map.put("post_id",post_id);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(ReplyMessageActivity.this);
        URL_INSERTREPLY = urlSetting.getUrl()+"reply/insertReplyMessage";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_INSERTREPLY, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String success = response.getString("success");
                    if(success.equals("1")){
                        list_reply.clear();
                        FirstLoadReply();
                    }else{
                        Toast.makeText(ReplyMessageActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e("insertActivity()_JSONException Error", e.toString());
                    Toast.makeText(ReplyMessageActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
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