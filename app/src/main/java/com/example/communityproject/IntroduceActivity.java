package com.example.communityproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.example.communityproject.Post.PostAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntroduceActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    UrlSetting urlSetting;
    SessionManager sessionManager;
    String requestUrl ;
    String c_context , c_develop, c_vision;
    CardView commutity_introduceCardView,commutity_developCardView,commutity_visionCardView;
    private static IntorduceAdapter intorduceAdapter;
    String c_id;
    private List<IntorduceCardViewData> list_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        commutity_introduceCardView = findViewById(R.id.commutity_introduceCardView);
        commutity_developCardView = findViewById(R.id.commutity_developCardView);
        commutity_visionCardView = findViewById(R.id.commutity_visionCardView);
        loadotherData();
        list_data = new ArrayList<>();
        recyclerView = findViewById(R.id.peopleRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        loaduserData();

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        c_id = sessionUserData.get(sessionManager.C_ID);

        //社區簡述
        commutity_introduceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c_context = "金山社區位於燕巢鄉東北方,東與旗山鎮,南與深水村及東燕村,西與尖山村,北與田寮為界。人口分佈在金山、番田二部落，167戶人口約660餘人,農業250人、工商業120人、家管學生、自由業約230人。60歲以上長者100人。佔全村16.6％。大專青佔8％。於83年2月1日組織社區發展協會（理監事會20人會員會員82人） 及義工媽媽20人、祥和志工39人，金山擊鼓隊10人。";
                detail_dialog(c_context);
            }
        });
        // 發展重點
        commutity_developCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c_develop= "產業發展、社福醫療、社區治安、人文教育、環境景觀、環保生態 ";
                detail_dialog(c_develop);
            }
        });

        //未來願景
        commutity_visionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c_vision = "1.強化老人照顧火鶴傳情60歲以上老人營養午餐 　\n" +
                        "2.兒童課後輔導一補救教學\n" +
                        "3.落實社區健康營造";
                detail_dialog(c_vision);
            }
        });
    }

    private  void loaduserData(){
        urlSetting = new UrlSetting(IntroduceActivity.this);
        requestUrl = urlSetting.getUrl()+"user/getCommunityMessager";
       JSONObject data = new JSONObject();
        try {
            data.put("c_id",c_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestUrl,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("qweqwe", String.valueOf(response));
                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String m_id = jsonObject.getString("m_id");
                        String m_name = jsonObject.getString("m_name");
                        String a_name = jsonObject.getString("a_name");
                        String image = jsonObject.getString("image");
                        String authority_id = jsonObject.getString("authority_id");

                        IntorduceCardViewData intorduceCardViewData = new IntorduceCardViewData(m_id,m_name, a_name, authority_id,image);
                        list_data.add(intorduceCardViewData);
                    }
                    intorduceAdapter = new IntorduceAdapter(IntroduceActivity.this,list_data); // 將資料交給adapter
                    recyclerView.setAdapter(intorduceAdapter);// 設置adapter給recyclerView

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(IntroduceActivity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private  void loadotherData(){
        urlSetting = new UrlSetting(IntroduceActivity.this);
        requestUrl = urlSetting.getUrl()+"user/getCommunityintorduce";
        JSONObject data = new JSONObject();
        try {
            data.put("c_id",c_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestUrl,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("qweqwe", String.valueOf(response));
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
//                    String c_context , c_develop, c_vision;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        c_context= jsonObject.getString("context");
                        c_develop = jsonObject.getString("develop");
                        c_vision = jsonObject.getString("vision");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(IntroduceActivity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }


    private void detail_dialog(String ans){
        Dialog dialog;
        dialog = new Dialog(IntroduceActivity.this);
        dialog.setContentView(R.layout.intorduce_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        TextView context = dialog.findViewById(R.id.context);
        Button btn_update = dialog.findViewById(R.id.btn_update);
        context.setText(ans);

        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });




    }


}