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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.communityproject.LoginAndRegister.LoginActivity;
import com.example.communityproject.LoginAndRegister.RegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateCommunityActivity extends AppCompatActivity {
    CircleImageView user_image;
    EditText c_account,c_password,check_password,user_name,c_name,c_address,c_phone;
    Button btn_create,select_img;
    Spinner sex;
    final int CODE_GALLERY_REQUEST = 999;
    Bitmap bitmap;
    ProgressBar loading;
    UrlSetting urlSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

        user_image = findViewById(R.id.user_image);
        c_account = findViewById(R.id.c_account);
        c_password = findViewById(R.id.c_password);
        check_password = findViewById(R.id.check_password);
        user_name = findViewById(R.id.user_name);
        c_name = findViewById(R.id.c_name);
        c_address = findViewById(R.id.c_address);
        c_phone = findViewById(R.id.c_phone);
        btn_create = findViewById(R.id.btn_create);
        select_img = findViewById(R.id.select_img);
        sex = findViewById(R.id.sex);
        loading = findViewById(R.id.loading);

        ArrayList sexList = new ArrayList();
        sexList.add("請選擇性別");
        sexList.add("男");
        sexList.add("女");
        sex.setAdapter(new ArrayAdapter<String>(CreateCommunityActivity.this, android.R.layout.simple_spinner_dropdown_item,sexList));



        select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        CreateCommunityActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_GALLERY_REQUEST
                );
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCommunity();
            }
        });
    }


    // select img function start
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CODE_GALLERY_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"), CODE_GALLERY_REQUEST);
            }else{
                error_dialog("請開啟相機權限");
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
                user_image.setImageBitmap(bitmap);
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

    private void createCommunity() {
        loading.setVisibility(View.VISIBLE);
        btn_create.setVisibility(View.GONE);
        Dialog dialog;
        dialog = new Dialog(CreateCommunityActivity.this);
        final String manager_account = this.c_account.getText().toString().trim();
        final String manager_password = this.c_password.getText().toString().trim();
        final String check_password = this.check_password.getText().toString().trim();
        final String manager_name = this.user_name.getText().toString().trim();
        final String commutityName = this.c_name.getText().toString().trim();
        final String commutityAddress = this.c_address.getText().toString().trim();
        final String comminotyPhone = this.c_phone.getText().toString().trim();
        final String sex = this.sex.getSelectedItem().toString().trim();

        if (!manager_password.equals(check_password)) {
            error_dialog("密碼不相同，請再次輸入!!");
            loading.setVisibility(View.GONE);
            btn_create.setVisibility(View.VISIBLE);
            return;
        } else if (manager_account.equals("") || manager_password.equals("") || check_password.equals("") || manager_name.equals("") ||  commutityName.equals("") ||  commutityAddress.equals("") || comminotyPhone.equals("")) {
            loading.setVisibility(View.GONE);
            btn_create.setVisibility(View.VISIBLE);
            error_dialog("欄位不可為空!!");
            return;
        } else if (sex.equals("請選擇性別")) {
            loading.setVisibility(View.GONE);
            btn_create.setVisibility(View.VISIBLE);
            error_dialog("必須選擇性別!!");
            return;
        }else{
            String imageData = "";
            if(bitmap == null){
                imageData = "";
            }else{
                imageData = imageToString(bitmap);
            }
            String finalImageData = imageData.trim();
            Map<String, String> map = new HashMap<String, String>();
            map.put("manager_account",manager_account);
            map.put("manager_password",manager_password);
            map.put("manager_name",manager_name);
            map.put("commutityName",commutityName);
            map.put("commutityAddress",commutityAddress);
            map.put("comminotyPhone",comminotyPhone);
            map.put("user_image", finalImageData);
            map.put("sex",sex);
            JSONObject data = new JSONObject(map);
            urlSetting = new UrlSetting(CreateCommunityActivity.this);
            String URL_REQUEST = urlSetting.getUrl()+"user/createCommunity";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_REQUEST,data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        String success = response.getString("success");
                        String message = response.getString("message");
                        if(success.equals("1")){
                            loading.setVisibility(View.GONE);
                            btn_create.setVisibility(View.VISIBLE);
                            success_dialog("創建社區成功，請等待審核");

                        }else if(success.equals("2")){
                            loading.setVisibility(View.GONE);
                            btn_create.setVisibility(View.VISIBLE);
                            error_dialog(message.trim());
                            return;
                        }else if(success.equals("3")){
                            loading.setVisibility(View.GONE);
                            btn_create.setVisibility(View.VISIBLE);
                            error_dialog(message.trim());
                            return;
                        }else if(success.equals("0")){
                            loading.setVisibility(View.GONE);
                            btn_create.setVisibility(View.VISIBLE);
                            error_dialog(message.trim());
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("響應錯誤1",e.toString());
                        error_dialog("發生例外錯誤，如還有此情況請向客服人員反應");
                        loading.setVisibility(View.GONE);
                        btn_create.setVisibility(View.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponse",error.toString());
                    error_dialog("發生例外錯誤，如還有此情況請向客服人員反應");
                    loading.setVisibility(View.GONE);
                    btn_create.setVisibility(View.VISIBLE);
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }

    }


    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(CreateCommunityActivity.this);
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
        dialog = new Dialog(CreateCommunityActivity.this);
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
                startActivity(new Intent(CreateCommunityActivity.this , LoginActivity.class));
                finish();
            }
        });
    }

}