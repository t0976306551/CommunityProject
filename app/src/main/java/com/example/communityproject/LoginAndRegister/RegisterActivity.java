package com.example.communityproject.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    CircleImageView user_image;
    Button select_img,btn_register,btn_select;
    EditText register_account,register_password,register_cpassword,register_name;
    Spinner sex;
    Bitmap bitmap;
    TextView communityName;
    ProgressBar loading;
    final int CODE_GALLERY_REQUEST = 999;
    private static String URL_REGIST ;
    private static  String URL_SPINNER ;
    UrlSetting urlSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_account = findViewById(R.id.register_account);
        register_password = findViewById(R.id.register_password);
        register_cpassword = findViewById(R.id.register_cpassword);
        register_name = findViewById(R.id.register_name);
        btn_register = findViewById(R.id.btn_register);
        user_image = findViewById(R.id.user_image);
        select_img = findViewById(R.id.select_img);
        loading = findViewById(R.id.loading);
        btn_select = findViewById(R.id.btn_select);
        communityName = findViewById(R.id.communityName);


        sex = findViewById(R.id.sex); //性別選單
        select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_GALLERY_REQUEST
                );
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,SelectCommunityActivity.class));
                finish();
            }
        });


        ArrayList sexList = new ArrayList();
        sexList.add("請選擇性別");
        sexList.add("男");
        sexList.add("女");
        sex.setAdapter(new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item,sexList));


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
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

    private void Register(){
        loading.setVisibility(View.VISIBLE);
        btn_register.setVisibility(View.GONE);
        Dialog dialog;
        dialog = new Dialog(RegisterActivity.this);
        final String account = this.register_account.getText().toString().trim();
        final String password = this.register_password.getText().toString().trim();
        final String c_password = this.register_cpassword.getText().toString().trim();
        final String name = this.register_name.getText().toString().trim();
        final String commutity = this.communityName.getText().toString().trim();
        final String sex = this.sex.getSelectedItem().toString().trim();

        if(!password.equals(c_password)){
            error_dialog("密碼不相同，請再次輸入!!");
            loading.setVisibility(View.GONE);
            btn_register.setVisibility(View.VISIBLE);
            return;
        }else if(account.equals("") || password.equals("") || c_password.equals("") || name.equals("")){
            loading.setVisibility(View.GONE);
            btn_register.setVisibility(View.VISIBLE);
            error_dialog("欄位不可為空!!");
            return;
        }else if(commutity.equals("請選擇社區")){
            loading.setVisibility(View.GONE);
            btn_register.setVisibility(View.VISIBLE);
            error_dialog("必須選擇社區!!");
            return;
        }else if(sex.equals("請選擇性別")){
            loading.setVisibility(View.GONE);
            btn_register.setVisibility(View.VISIBLE);
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
                map.put("account",account);
                map.put("password",password);
                map.put("name",name);
                map.put("community",commutity);
                map.put("image", finalImageData);
                map.put("sex",sex);
            JSONObject data = new JSONObject(map);
            urlSetting = new UrlSetting(RegisterActivity.this);
            URL_REGIST = urlSetting.getUrl()+"user/register";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_REGIST,data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        Log.e("onResponse","Register response");
                        String success = response.getString("success");
                        String message = response.getString("message");
                        if(success.equals("1")){
                            loading.setVisibility(View.GONE);
                            btn_register.setVisibility(View.VISIBLE);
                            success_dialog("註冊成功");

                        }else if(success.equals("0")){
                            loading.setVisibility(View.GONE);
                            btn_register.setVisibility(View.VISIBLE);
                            error_dialog("此帳號已被用過!!");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("響應錯誤1",e.toString());
                        getAlertdialog("發生例外錯誤，如還有此情況請向客服人員反應");
                        loading.setVisibility(View.GONE);
                        btn_register.setVisibility(View.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponse",error.toString());
                    getAlertdialog("發生例外錯誤，如還有此情況請向客服人員反應");
                    loading.setVisibility(View.GONE);
                    btn_register.setVisibility(View.VISIBLE);
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }

    }

    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(RegisterActivity.this);
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

    private void getAlertdialog(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("提示");
        builder.setMessage(Message);
        builder.setNegativeButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void success_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(RegisterActivity.this);
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
                startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        if(intent.hasExtra("c_id") && intent.hasExtra("c_name")){
            String c_id = intent.getStringExtra("c_id");
            String c_name = intent.getStringExtra("c_name");
            communityName.setText(c_name);

        }
    }
}