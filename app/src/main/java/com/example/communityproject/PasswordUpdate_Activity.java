package com.example.communityproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.example.communityproject.Post.PostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PasswordUpdate_Activity extends AppCompatActivity {

    CircleImageView user_image;
    TextView update_account,updateName,authorityName,commutityName,userSex;
    Button select_img,btn_updateName,btn_updatePassword;
    SessionManager sessionManager;
    String m_id;
    Bitmap bitmap;
    UrlSetting urlSetting;

    final int CODE_GALLERY_REQUEST = 999;
    private static String URL_SETDATA;
    private static String URL_UPDATE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        m_id = sessionUserData.get(sessionManager.USERID);

        user_image = findViewById(R.id.user_image);
        update_account = findViewById(R.id.update_account);
        updateName = findViewById(R.id.updateName);
        authorityName = findViewById(R.id.authorityName);
        commutityName = findViewById(R.id.commutityName);
        btn_updatePassword = findViewById(R.id.btn_updatePassword);
        userSex = findViewById(R.id.userSex);
        btn_updateName = findViewById(R.id.btn_updateName);
        select_img = findViewById(R.id.select_img);
        setData();

        select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog;
                dialog = new Dialog(PasswordUpdate_Activity.this);
                dialog.setContentView(R.layout.image_select_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView useSetImage = dialog.findViewById(R.id.useSetImage);
                TextView choosePhoto = dialog.findViewById(R.id.choosePhoto);
                Window dialogWindow = dialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
                layoutParams.y = 20;
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(layoutParams);
                dialog.show();

                useSetImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user_image.setImageResource(R.drawable.user_preset);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("m_id", m_id);
                        map.put("updataImage", "");
                        map.put("type", "updateImage");
                        updateData(map);
                        dialog.cancel();

                    }
                });
                choosePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(
                                PasswordUpdate_Activity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                CODE_GALLERY_REQUEST
                        );
                        dialog.cancel();
                    }
                });

            }
        });

        //更改會員姓名
        btn_updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog;
                dialog = new Dialog(PasswordUpdate_Activity.this);
                dialog.setContentView(R.layout.update_username_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                EditText updateUserName = dialog.findViewById(R.id.updateUserName);
                Button btn_updateName = dialog.findViewById(R.id.btn_updateName);
                Window dialogWindow = dialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
                layoutParams.y = 20;
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(layoutParams);
                dialog.show();

                btn_updateName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(updateUserName.getText().toString().equals("")){
                            error_dialog("欄位不可為空");
                            return;
                        }
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("m_id", m_id);
                        map.put("updateName", updateUserName.getText().toString().trim());
                        map.put("type", "updateName");
                        updateData(map);
                        dialog.cancel();
                    }
                });

            }
        });

        btn_updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog;
                dialog = new Dialog(PasswordUpdate_Activity.this);
                dialog.setContentView(R.layout.update_password_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                EditText update_password = dialog.findViewById(R.id.update_password);
                EditText new_password = dialog.findViewById(R.id.new_password);
                Button btn_update = dialog.findViewById(R.id.btn_update);
                Window dialogWindow = dialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
                layoutParams.y = 20;
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(layoutParams);
                dialog.show();

                btn_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("m_id", m_id);
                        map.put("old_password", update_password.getText().toString().trim());
                        map.put("new_password", new_password.getText().toString().trim());
                        map.put("type", "updatePassword");
                        updateData(map);
                        dialog.cancel();
                    }
                });
            }
        });
    }

    //設置使用者初始資料
    private void setData(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("m_id", m_id);
        JSONObject data = new JSONObject(map);

        urlSetting = new UrlSetting(PasswordUpdate_Activity.this);
        URL_SETDATA = urlSetting.getUrl()+"user/setUpdateData";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SETDATA,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String member_id = "",member_account="",member_image="",member_name="",authority_name ="" , commutity_name = "", member_sex = "";
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        member_id = object.getString("m_id").trim();
                        member_account = object.getString("m_acc").trim();
                        member_name = object.getString("m_name").trim();
                        member_image = object.getString("image").trim();
                        authority_name = object.getString("a_name").trim();
                        commutity_name = object.getString("c_name").trim();
                        member_sex = object.getString("m_sex").trim();
                    }
                    update_account.setText("會員帳戶："+member_account);
                    updateName.setText("會員姓名："+member_name);
                    authorityName.setText("會員權限："+authority_name);
                    commutityName.setText("社區名稱："+commutity_name);
                    userSex.setText("會員性別："+member_sex);

                    if(!member_image.equals("")){
                        Glide.with(PasswordUpdate_Activity.this).load(member_image).into(user_image);
                    }else{
                        user_image.setImageResource(R.drawable.user_preset);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("響應錯誤1",e.toString());
                    error_dialog("例外錯誤，請在試一次");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.toString());
                error_dialog("響應錯誤，請檢查網路");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    //修改資料
    private void updateData(Map map){
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(PasswordUpdate_Activity.this);
        URL_UPDATE = urlSetting.getUrl()+"user/updateData";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_UPDATE,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String success = response.getString("success");
                    String message = response.getString("message");
                    if(success.equals("1")){
                        if(message.equals("updateName")){
                            String name = map.get("updateName").toString();
                            success_dialog("名稱修改成功");
                            updateName.setText("會員名稱："+name);
                            sessionManager.update("NAME",name);

                        }else if(message.equals("updateImage")){

                            if(map.get("updataImage").toString().equals("")){
                                user_image.setImageResource(R.drawable.user_preset);
                            }else{
                                user_image.setImageBitmap(bitmap);
                            }

                            String user_image = "";
                            JSONArray jsonArray = response.getJSONArray("data");
                            for(int i = 0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                user_image = object.getString("image");
                            }
                            sessionManager.update("IMAGE",user_image);
                            success_dialog("圖片修改成功");
                        }else if(message.equals("updatePassword")){
                            success_dialog("密碼修改成功");

                        }
                    }else if(success.equals("3")){
                        error_dialog("原密碼輸入錯誤");
                    }else{
                        error_dialog("修改失敗");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("響應錯誤1",e.toString());
                    error_dialog("例外錯誤，請再試一次");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.toString());
                error_dialog("響應錯誤，請檢查網路");

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    // select img function start
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CODE_GALLERY_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"), CODE_GALLERY_REQUEST);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null ){
            Uri filePath = data.getData();
            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);

                Map<String, String> map = new HashMap<String, String>();
                map.put("m_id", m_id);
                map.put("updataImage", imageToString(bitmap));
                map.put("type", "updateImage");
                updateData(map);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return  encodeImage;
    }
    // select img function end

    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(PasswordUpdate_Activity.this);
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
    private void success_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(PasswordUpdate_Activity.this);
        dialog.setContentView(R.layout.success_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView success_text = dialog.findViewById(R.id.success_text);
        success_text.setText("");
        success_text.setText(text);
        dialog.show();
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }
}