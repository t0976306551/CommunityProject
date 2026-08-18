package com.example.communityproject.Acyivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.communityproject.Attraction.ImageAdapter;
import com.example.communityproject.Attraction.ImageData;
import com.example.communityproject.Attraction.insert_attraction_Activity;
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class insert_activity_Activity extends AppCompatActivity {
    private Button sendActivity,btn_startDate,btn_endDate,btn_insertImage;
    private EditText activityName,activityContext,total_people;
    private TextView startDate,endDate,s_time,e_time;
    private static String URL_INSERTACTIVITY ;
    UrlSetting urlSetting;
    String m_id, c_id;
    int s_hour , s_minute , e_hour,e_minute ;
    int s_year, s_month, s_day;
    int e_year, e_month, e_day;

    int PICK_IMAGE_MULTIPLE = 1;
    RecyclerView recyclerView;
    List<ActivityImageData> list_data;
    private static ActitityImageAdapter actitityImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_activity);
        btn_insertImage = (Button) findViewById(R.id.btn_insertImage);
        sendActivity = (Button) findViewById(R.id.sendActivity);
        btn_startDate = (Button) findViewById(R.id.btn_startDate);
        btn_endDate = (Button) findViewById(R.id.btn_endDate);
        activityName = (EditText) findViewById(R.id.activityName);
        activityContext = (EditText) findViewById(R.id.activityContext);
        total_people = (EditText) findViewById(R.id.total_people);
        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);
        s_time = (TextView) findViewById(R.id.s_time);
        e_time = (TextView) findViewById(R.id.e_time);
        Intent intent = this.getIntent();
        m_id = intent.getStringExtra("m_id");
        c_id = intent.getStringExtra("c_id");
        Date nowDate = new Date(System.currentTimeMillis());

        //設定多圖片recycleview
        list_data = new ArrayList<ActivityImageData>();
        recyclerView = findViewById(R.id.imageRecyclerView);
        final LinearLayoutManager layoutManager = new GridLayoutManager(this,2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        actitityImageAdapter = new ActitityImageAdapter(insert_activity_Activity.this,list_data); // 將資料交給adapter
        recyclerView.setAdapter(actitityImageAdapter);// 設置adapter給recyclerView

        // 選擇活動開始日期
        btn_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                s_year = calendar.get(Calendar.YEAR);
                s_month = calendar.get(Calendar.MONTH);
                s_day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(insert_activity_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String strMonth = Integer.toString(month);
                        String format = setDateFormat(year,month,day);
                        startDate.setText(format);
                    }
                }, s_year,s_month, s_day).show();
            }
        });
        //選擇活動結束日期
        btn_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                e_year = calendar.get(Calendar.YEAR);
                e_month = calendar.get(Calendar.MONTH);
                e_day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(insert_activity_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String format = setDateFormat(year,month,day);
                        if(!dateParse(startDate.getText().toString() , format)){
                            error_dialog("結束日期不可少於開始日期");
                            return;
                        }
                        endDate.setText(format);
                    }
                }, e_year,e_month, e_day).show();
            }
        });
        //選擇開始時間
        s_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               TimePickerDialog timePickerDialog = new TimePickerDialog(insert_activity_Activity.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker timePicker, int i, int i1) {
                       s_hour = i;
                       s_minute = i1;
                       Calendar calendar = Calendar.getInstance();
                       calendar.set(0,0,0,s_hour,s_minute);
                       String ans = (String) DateFormat.format("aa hh:mm",calendar);
                       String time1 = (String) DateFormat.format("hh:mm",calendar);
                       s_time.setText(ans);
                   }
               },12,0,false);
                   timePickerDialog.updateTime(s_hour,s_minute);
               timePickerDialog.show();
            }
        });
        //選擇結束時間
        e_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(insert_activity_Activity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        e_hour = i;
                        e_minute = i1;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,e_hour,e_minute);
                        String ans = (String) DateFormat.format("aa hh:mm",calendar);
                        e_time.setText(ans);
                    }
                },12,0,false);
                timePickerDialog.updateTime(e_hour,e_minute);
                timePickerDialog.show();
            }
        });

        //選擇活動圖片
        btn_insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list_data.size()>=5){
                    error_dialog("最多只能選取 5 張照片");
                    return;
                }
                Intent intent = new Intent();
                // setting type to select to be image
                intent.setType("image/*");
                // allowing multiple image to be selected
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        //傳送最終資料給後端
        sendActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertActivity();
            }
        });

    }
    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        String strMonth = Integer.toString(monthOfYear+1);
        String strDay = Integer.toString(dayOfMonth);
        if(strMonth.length() == 1){
            strMonth = "0"+strMonth;
        }
        if(strDay.length() == 1){
            strDay = "0"+strDay;
        }
        return String.valueOf(year) + "-"
                + strMonth + "-"
                + strDay;
    }

    private void insertActivity() {
        final String activityName = this.activityName.getText().toString().trim();
        final String startDate = this.startDate.getText().toString().trim();
        final String endDate = this.endDate.getText().toString().trim();
        final String activityContext = this.activityContext.getText().toString().trim();
        final String start_time = this.s_time.getText().toString();
        final String end_time = this.e_time.getText().toString();
        final String total_people = this.total_people.getText().toString().trim();

        if(activityName.equals("") || startDate.equals("") || endDate.equals("") || activityContext.equals("") || start_time.equals("開始時間") || end_time.equals("結束時間") || total_people.equals("")){
            String text = "欄位不可為空!!";
            error_dialog(text);
            return;
        }

        JSONArray image_array = new JSONArray();
        ActivityImageData imageData;
        for (int i = 0; i < list_data.size(); i++) {
            imageData = list_data.get(i);
            Bitmap bitmap = imageData.getBitmap();
            image_array.put(imageToString(bitmap));
        }

        JSONObject datas = new JSONObject();
        try{
            datas.put("ac_name",activityName);
            datas.put("ac_context",activityContext);
            datas.put("start_date",startDate);
            datas.put("end_date",endDate);
            datas.put("start_time",start_time);
            datas.put("end_time",end_time);
            datas.put("total_people",total_people);
            datas.put("apply_people","0");
            datas.put("m_id",m_id);
            datas.put("c_id",c_id);
            datas.put("image",image_array);
        }catch (JSONException e){
            e.printStackTrace();
        }

        urlSetting = new UrlSetting(insert_activity_Activity.this);
        URL_INSERTACTIVITY = urlSetting.getUrl()+"activity/create";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_INSERTACTIVITY, datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String success = response.getString("success");
                    if(success.equals("1")){
//                        getAlertdialog("新增活動成功");
                        success_dialog("新增活動成功");
                    }else{
                        error_dialog("新增活動失敗");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e("insertActivity()_JSONException Error", e.toString());
                    error_dialog("新增活動失敗");
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

    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(insert_activity_Activity.this);
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
        dialog = new Dialog(insert_activity_Activity.this);
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


    //選擇多圖片動作
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
                        ActivityImageData imageData = new ActivityImageData(bitmap);
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
                    ActivityImageData imageData = new ActivityImageData(bitmap);
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

            actitityImageAdapter.setData(list_data);
            recyclerView.setAdapter(actitityImageAdapter);// 設置adapter給recyclerView

        } else {
            // show this if no image is selected
            return;

        }
    }

    //bimap 轉換 String
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);

        return  encodeImage;
    }

    private void getAlertdialog(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(insert_activity_Activity.this);
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

    private static boolean dateParse(String date1 , String date2){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = simpleDateFormat.parse(date1);
            d2 = simpleDateFormat.parse(date2);
            Log.v("", String.valueOf(d1.getTime()));
            Log.v("", String.valueOf(d2.getTime()));
            if (d1.getTime()<=d2.getTime()){
                return true;
            }else{
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }




}