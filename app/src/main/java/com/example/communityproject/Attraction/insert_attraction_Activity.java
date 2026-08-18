package com.example.communityproject.Attraction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.example.communityproject.Acyivity.insert_activity_Activity;
import com.example.communityproject.LoginAndRegister.RegisterActivity;
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class insert_attraction_Activity extends AppCompatActivity {
    EditText name,context;
    Button btn_insertImage,btn_insert;
    ImageView setImage;
    String c_id,m_id;
    Bitmap bitmap;
    final int CODE_GALLERY_REQUEST = 999;
    private static String URL_ATTRACTION ;
    UrlSetting urlSetting;
    int PICK_IMAGE_MULTIPLE = 1;
    RecyclerView recyclerView;
    List<ImageData> list_data;
    private static ImageAdapter imageAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_attraction);
        name = findViewById(R.id.name);
        context = findViewById(R.id.context);
        btn_insertImage = findViewById(R.id.btn_insertImage);
        btn_insert = findViewById(R.id.btn_insert);

        Intent intent = this.getIntent();
        m_id = intent.getStringExtra("m_id");
        c_id = intent.getStringExtra("c_id");

        list_data = new ArrayList<ImageData>();
        recyclerView = findViewById(R.id.imageRecyclerView);
        final LinearLayoutManager layoutManager = new GridLayoutManager(this,2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        imageAdapter = new ImageAdapter(insert_attraction_Activity.this,list_data); // 將資料交給adapter
        recyclerView.setAdapter(imageAdapter);// 設置adapter給recyclerView


        btn_insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list_data.size()>=5){
                    error_dialog("最多只能選取 5 張照片");
                    return;
                }
                // initialising intent
                Intent intent = new Intent();
                // setting type to select to be image
                intent.setType("image/*");
                // allowing multiple image to be selected
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertAttraction();
            }
        });

    }

    private void insertAttraction() {
        final String name = this.name.getText().toString().trim();
        final String context = this.context.getText().toString().trim();

        if(name.equals("") || context.equals("")){
            error_dialog("欄位不可空白");
            return;
        }
        if(list_data.size() == 0){
            error_dialog("請選擇圖片");
            return;
        }

        JSONArray image_array = new JSONArray();
        ImageData imageData;
        for (int i = 0; i < list_data.size(); i++) {
            imageData = list_data.get(i);
            Bitmap bitmap = imageData.getBitmap();
            image_array.put(imageToString(bitmap));
        }

        JSONObject datas = new JSONObject();
        try{
            datas.put("a_name",name);
            datas.put("a_context",context);
            datas.put("m_id",m_id);
            datas.put("c_id",c_id);
            datas.put("image",image_array);
        }catch (JSONException e){
            e.printStackTrace();
        }

        urlSetting = new UrlSetting(insert_attraction_Activity.this);
        URL_ATTRACTION = urlSetting.getUrl()+"attraction/create";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_ATTRACTION, datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    String success = response.getString("success");
                    if(success.equals("1")){
                        success_dialog("新增成功");
//                        finish();
                    }else{
                        error_dialog("新增失敗");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    error_dialog("發生例外錯誤");
                    Log.e("insertActivity()_JSONException Error", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error_dialog("響應錯誤，請檢查網路");
                error.printStackTrace();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    // image to String
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return  encodeImage;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
//                    if(mArrayUri.size()>=5 && list_data.size()>=5){
//                        return;
//                    }
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageurl);
                        ImageData imageData = new ImageData(bitmap);
                        list_data.add(imageData);
                        imageData.setBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Uri imageurl = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageurl);
                    ImageData imageData = new ImageData(bitmap);
                    list_data.add(imageData);
                    imageData.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(list_data.size()>5){
                for(int s = 5;s<list_data.size();s++){
                    list_data.remove(s);
                }
            }

            imageAdapter.setData(list_data);
            recyclerView.setAdapter(imageAdapter);// 設置adapter給recyclerView

        } else {
            // show this if no image is selected
            Log.v("notImages","You haven't picked Image");
//            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(insert_attraction_Activity.this);
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
        dialog = new Dialog(insert_attraction_Activity.this);
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
                finish();
            }
        });
    }
}