package com.example.communityproject.Staple;

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
import com.example.communityproject.Attraction.attraction_page_Activity;
import com.example.communityproject.PasswordUpdate_Activity;
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

public class staple_page_Activity extends AppCompatActivity {
    TextView a_name,a_context,staple_other;
    ImageSlider a_img;
    ImageView editStaple;
    SessionManager sessionManager;
    private String URL_GETSTAPLEDATA;
    private String URL_UPDATE ;
    UrlSetting urlSetting;
    String s_id;
    private DisplayMetrics dm;
    String authority_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staple_page);
        a_img = findViewById(R.id.a_img);
        a_name = findViewById(R.id.a_name);
        a_context = findViewById(R.id.a_context);
        staple_other = findViewById(R.id.staple_other);
        editStaple = findViewById(R.id.editStaple);
        Bundle bundle = getIntent().getExtras();
        s_id = bundle.getString("s_id");
        LoadData(s_id);

        sessionManager = new SessionManager(staple_page_Activity.this);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        authority_id = sessionUserData.get(sessionManager.A_ID);

        if(authority_id.equals("3")){
            editStaple.setVisibility(View.GONE);
        }


        editStaple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(staple_page_Activity.this,R.anim.click_style);
                editStaple.startAnimation(animation);
                Dialog dialog;
                dialog = new Dialog(staple_page_Activity.this);
                dialog.setContentView(R.layout.staple_setting_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView updateStapleName = dialog.findViewById(R.id.settingName);
                TextView updateStapleContext = dialog.findViewById(R.id.settingContext);
                TextView updateStapleRoad = dialog.findViewById(R.id.settingRoad);
                Window dialogWindow = dialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
                layoutParams.y = 20;
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(layoutParams);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlide;
                updateStapleName.setText("修改名產名稱");
                updateStapleContext.setText("修改名產內容");
                dialog.show();

                updateStapleName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateStapleDataDialog(a_name.getText().toString() , "name");

                    }
                });

                updateStapleContext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateStapleDataDialog(a_context.getText().toString() , "context");
                    }
                });

                updateStapleRoad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateStapleDataDialog(staple_other.getText().toString() , "other");
                    }
                });

            }
        });

    }

    //Load activity data from database start
    private void LoadData(String s_id){
//        String GET_URL_GETSTAPLEDATA = URL_GETSTAPLEDATA+"?s_id="+s_id;
        urlSetting = new UrlSetting(staple_page_Activity.this);
        URL_GETSTAPLEDATA = urlSetting.getUrl()+"staple/load"+"?s_id="+s_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_GETSTAPLEDATA,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String  at_id="",at_name="",at_context="",other="";
                    JSONArray a_image = new JSONArray();
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        at_id = jsonObject.getString("s_id");
                        at_name = jsonObject.getString("s_name");
                        at_context = jsonObject.getString("s_context");
                        other = jsonObject.getString("s_info");
                        a_image = jsonObject.getJSONArray("images");

                    }
                    a_name.setText(at_name.trim());
                    a_context.setText(at_context.trim());
                    staple_other.setText(other.trim());
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(staple_page_Activity.this,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    //Load activity data from database end

    private void updateStapleDataDialog(String StapleData , String updateType){
        Dialog dialog;
        dialog = new Dialog(staple_page_Activity.this);
        dialog.setContentView(R.layout.update_username_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText updateData = dialog.findViewById(R.id.updateUserName);
        Button btn_update = dialog.findViewById(R.id.btn_updateName);
        dm = new DisplayMetrics();
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.y = 20;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(layoutParams);
        String old_data = StapleData.trim();
        updateData.setHint("輸入修改資料");
        updateData.setText(StapleData.trim());
        dialog.show();
        String new_data = StapleData.trim();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("s_id",s_id);
                    map.put("updateData",updateData.getText().toString());
                    map.put("type", updateType);
                    update(map);
                    dialog.cancel();
            }
        });
    }

    private void update(Map map){
        Log.v("789","789");
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(staple_page_Activity.this);
        URL_UPDATE = urlSetting.getUrl()+"staple/updateStapleData";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_UPDATE,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String success = response.getString("success");
                    if(success.equals("1")){
//                        Toast.makeText(staple_page_Activity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        LoadData(s_id);
                    }else{
                        Toast.makeText(staple_page_Activity.this, "修改失敗", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("響應錯誤1",e.toString());
                    Toast.makeText(staple_page_Activity.this, "發生例外錯誤，如還有此情況請向客服人員反應" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.toString());
                Toast.makeText(staple_page_Activity.this, "發生響應錯誤，如還有此情況請向客服人員反應" + error.toString(), Toast.LENGTH_SHORT).show();

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

}