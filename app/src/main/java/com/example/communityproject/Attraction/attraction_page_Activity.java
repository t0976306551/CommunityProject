package com.example.communityproject.Attraction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.communityproject.R;
import com.example.communityproject.SessionManager;
import com.example.communityproject.Staple.staple_page_Activity;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class attraction_page_Activity extends AppCompatActivity {
    TextView a_name,a_context;
    ImageSlider a_img;
    ImageView editAttraction;
    String a_id;
    private String URL_GETATTRACTIONDATA;
    private String URL_UPDATE;
    UrlSetting urlSetting;
    SessionManager sessionManager;
    String authority_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_page);
        a_img = findViewById(R.id.a_img);
        a_name = findViewById(R.id.a_name);
        a_context = findViewById(R.id.a_context);
        editAttraction = findViewById(R.id.editAttraction);
        Bundle bundle = getIntent().getExtras();
        a_id = bundle.getString("a_id");
        LoadData(a_id);
        sessionManager = new SessionManager(attraction_page_Activity.this);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        authority_id = sessionUserData.get(sessionManager.A_ID);

        if(authority_id.equals("3")){
            editAttraction.setVisibility(View.GONE);
        }

        editAttraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(attraction_page_Activity.this,R.anim.click_style);
                editAttraction.startAnimation(animation);
                Dialog dialog;
                dialog = new Dialog(attraction_page_Activity.this);
                dialog.setContentView(R.layout.staple_setting_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView updateArrractionName = dialog.findViewById(R.id.settingName);
                TextView updateArrractionContext = dialog.findViewById(R.id.settingContext);
                TextView updateStapleRoad = dialog.findViewById(R.id.settingRoad);
                View view_ui_two = dialog.findViewById(R.id.view_ui_two);
                view_ui_two.setVisibility(View.GONE);
                updateStapleRoad.setVisibility(View.GONE);
                Window dialogWindow = dialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
                layoutParams.y = 20;
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(layoutParams);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlide;
                updateArrractionName.setText("修改風景名稱");
                updateArrractionContext.setText("修改風景內容");
                dialog.show();

                updateArrractionName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateAttractionDataDialog(a_name.getText().toString().trim(),"name");
                    }
                });

                updateArrractionContext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateAttractionDataDialog(a_context.getText().toString().trim(),"context");
                    }
                });

            }
        });

    }


    private void updateAttractionDataDialog(String StapleData , String updateType){
        Dialog dialog;
        dialog = new Dialog(attraction_page_Activity.this);
        dialog.setContentView(R.layout.update_username_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText updateData = dialog.findViewById(R.id.updateUserName);
        Button btn_update = dialog.findViewById(R.id.btn_updateName);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.y = 20;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(layoutParams);
        updateData.setHint("輸入修改資料");
        updateData.setText(StapleData.trim());
        dialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, String> map = new HashMap<String, String>();
                map.put("a_id",a_id);
                map.put("updateData",updateData.getText().toString());
                map.put("type", updateType);
                update(map);
                dialog.cancel();
            }
        });
    }



    //Load activity data from database start
    private void LoadData(String a_id){
        urlSetting = new UrlSetting(attraction_page_Activity.this);
        URL_GETATTRACTIONDATA = urlSetting.getUrl()+"attraction/load"+"?a_id="+a_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_GETATTRACTIONDATA,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("loadpost", String.valueOf(response));
                JSONArray a_image = new JSONArray();
                try {
                    String  at_id="",at_name="",at_context="";
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        at_id = jsonObject.getString("a_id");
                        at_name = jsonObject.getString("a_name");
                        at_context = jsonObject.getString("a_context");
                        a_image = jsonObject.getJSONArray("images");

                    }
                    List<SlideModel> slideModels = new ArrayList<>();
                    for(int j = 0;j<a_image.length();j++){
                        slideModels.add(new SlideModel(String.valueOf(a_image.get(j)), null));
                    }
                    a_img.setImageList(slideModels,true);
                    a_img.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                            String imageUrl = slideModels.get(i).getImageUrl();
                            bigImage(imageUrl);
                        }
                    });

                    a_name.setText(at_name);
                    a_context.setText(at_context);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(attraction_page_Activity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    //Load activity data from database end

    private void update(Map map){
        Log.v("789","789");
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(attraction_page_Activity.this);
        URL_UPDATE = urlSetting.getUrl()+"attraction/updateAttractionData";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_UPDATE,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String success = response.getString("success");
                    if(success.equals("1")){
//                        Toast.makeText(staple_page_Activity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        LoadData(a_id);
                    }else{
//                        Toast.makeText(attraction_page_Activity.this, "修改失敗", Toast.LENGTH_SHORT).show();
                        error_dialog("修改失敗");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("響應錯誤",e.toString());
//                    Toast.makeText(attraction_page_Activity.this, "發生例外錯誤，如還有此情況請向客服人員反應" + e.toString(), Toast.LENGTH_SHORT).show();
                    error_dialog("發生例外錯誤");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.toString());
//                Toast.makeText(attraction_page_Activity.this, "發生響應錯誤，如還有此情況請向客服人員反應" + error.toString(), Toast.LENGTH_SHORT).show();
                error_dialog("響應錯誤，請檢查網路");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void bigImage(String imageUrl){
        Dialog dialog;
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.image_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imageView = dialog.findViewById(R.id.dialogimageView);
        Glide.with(this).load(imageUrl).into(imageView);
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
    }

    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(attraction_page_Activity.this);
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