package com.example.communityproject.Post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.communityproject.Attraction.ImageAdapter;
import com.example.communityproject.Attraction.ImageData;
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatePostActivity extends AppCompatActivity {
    EditText postTitle,postContext;
    ImageView setImage;
    ImageSlider post_img;
    Button sendUpdate;
    String p_id;
    UrlSetting urlSetting;
    private static String URL_LOADDATA;
    private static String URL_SENDDATA ;
    Bitmap bitmap;
    final int CODE_GALLERY_REQUEST = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);
        postTitle = findViewById(R.id.postTitle);
        postContext = findViewById(R.id.postContext);
        sendUpdate = findViewById(R.id.sendUpdate);
        post_img = findViewById(R.id.post_img);
        Bundle bundle = getIntent().getExtras();
        p_id = bundle.getString("p_id");


        loadData();


        sendUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });

    }

    private void loadData(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("p_id", p_id);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(UpdatePostActivity.this);
        URL_LOADDATA = urlSetting.getUrl()+"post/load";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOADDATA, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String  id="",title="",context="";
                    JSONArray image_array = new JSONArray();
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        id = jsonObject.getString("p_id");
                        title = jsonObject.getString("p_title");
                        context = jsonObject.getString("p_context");
                        image_array = jsonObject.getJSONArray("images");
                    }
                    postTitle.setText(title);
                    postContext.setText(context);
                    List<SlideModel> slideModels = new ArrayList<>();
                    try {

                        for(int i = 0;i<image_array.length();i++){
                            slideModels.add(new SlideModel(String.valueOf(image_array.get(i)), null));
                        }
                        post_img.setImageList(slideModels,true);
                        post_img.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onItemSelected(int i) {
                                String imageUrl = slideModels.get(i).getImageUrl();
                                bigImage(imageUrl);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e("insertActivity()_JSONException Error", e.toString());
                    Toast.makeText(UpdatePostActivity.this, "失敗", Toast.LENGTH_SHORT).show();
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


    // 設定貼文資料 start
    private void sendData() {
        String PostTile = postTitle.getText().toString();
        String PostContext = postContext.getText().toString();

        Map<String, String> map = new HashMap<String, String>();
        map.put("p_title",PostTile);
        map.put("p_context",PostContext);
        map.put("p_id",p_id);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(UpdatePostActivity.this);
        URL_SENDDATA = urlSetting.getUrl()+"post/edit";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SENDDATA, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String success = response.getString("success");
                    if(success.equals("1")) {
                        Toast.makeText(UpdatePostActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(UpdatePostActivity.this, "修改失敗", Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e("insertActivity()_JSONException Error", e.toString());
                    Toast.makeText(UpdatePostActivity.this, "出現錯誤問題", Toast.LENGTH_SHORT).show();
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
    // 設定貼文資料 end




    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);

        return  encodeImage;

    }


    //image url to bimap
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private void bigImage(String imageUrl){
        Dialog dialog;
        dialog = new Dialog(UpdatePostActivity.this);
        dialog.setContentView(R.layout.image_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imageView = dialog.findViewById(R.id.dialogimageView);
        Glide.with(UpdatePostActivity.this).load(imageUrl).into(imageView);
        dialog.show();



    }
}