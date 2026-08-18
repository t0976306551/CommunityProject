package com.example.communityproject.Acyivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder>{
    private List<ActivityCardviewData> list_Activity;
    private final Context context;

    SessionManager sessionManager;
    UrlSetting urlSetting;
    String m_id,a_id,c_id,user_id,message,authority;
    private String URL_LOADDATA;
    private String URL_DELETE;
    private String URL_JOIN ;
    private String URL_ClEAR;
    private String URL_GETTOTALPEOPLE;
    List<joinActivityData> list_join;
    Dialog activity_dialog;
    int s_hour , s_minute;
    public ActivityAdapter(Context context, List<ActivityCardviewData> list_Activity) {
        this.list_Activity = list_Activity;
        this.context=context;
    }

    public void setData(List<ActivityCardviewData> list_Activity){
        this.list_Activity = list_Activity;
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        list_Activity.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_activityitem, parent, false);
        return new ActivityAdapter.ViewHolder(view);

    }
    @NonNull
    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ActivityCardviewData activityCardviewData = list_Activity.get(position);
        holder.activityName.setText(activityCardviewData.getA_name());
        holder.activityCardView.setTag(activityCardviewData.getA_id());
        holder.btn_delete.setTag(activityCardviewData.getA_id());
        sessionManager = new SessionManager(context);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        m_id = sessionUserData.get(SessionManager.A_ID);
        user_id = sessionUserData.get(SessionManager.USERID);
        authority = sessionUserData.get(SessionManager.A_ID);
        if(m_id.equals("3")){
            holder.btn_delete.setVisibility(View.GONE);
        }else{
            holder.btn_delete.setVisibility(View.VISIBLE);
        }
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a_id = (String)view.getTag();
                deleteDialog(a_id,position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return list_Activity.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView btn_delete;
        TextView activityName,activityID;
        CardView activityCardView;
        public ViewHolder(View v){
            super(v);
            activityName = v.findViewById(R.id.activity_name);
            activityCardView = v.findViewById(R.id.activityCardView);
            btn_delete = v.findViewById(R.id.btn_delete);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    a_id = (String)view.getTag();
                    LoadData(a_id);
                }
            });
        }
    }
    //Load activity data from database start
    private void LoadData(String ac_id){
        JSONObject datas = new JSONObject();
        try {
            datas.put("ac_id",ac_id);
            datas.put("m_id",user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject sendDatas = new JSONObject();
        urlSetting = new UrlSetting(context);
        URL_LOADDATA = urlSetting.getUrl()+"activity/load";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOADDATA,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray a_image = new JSONArray();
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                         sendDatas.put("ac_id", jsonObject.getString("ac_id"));
                         sendDatas.put("ac_name", jsonObject.getString("ac_name"));
                         sendDatas.put("ac_context", jsonObject.getString("ac_context"));
                         sendDatas.put("start_date", jsonObject.getString("start_date"));
                         sendDatas.put("end_date", jsonObject.getString("end_date"));
                         sendDatas.put("m_name", jsonObject.getString("m_name"));
                         sendDatas.put("start_time", jsonObject.getString("start_time"));
                         sendDatas.put("end_time", jsonObject.getString("end_time"));
                         sendDatas.put("total_people", jsonObject.getString("total_people"));
                         sendDatas.put("apply_people", jsonObject.getString("apply_people"));
                         sendDatas.put("a_image", jsonObject.getJSONArray("images"));
                         sendDatas.put("check", jsonObject.getString("check"));
                    }
                    detail_dialog(sendDatas);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    private void deleteDialog(String ac_id,int position){
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        Button btn_check = dialog.findViewById(R.id.btn_check);
        TextView textView = dialog.findViewById(R.id.textView);
        TextView textView2 =  dialog.findViewById(R.id.textView2);
        textView.setText("刪除活動");
        textView2.setText("確認是否刪除活動");
        dialog.show();

        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteData(ac_id ,position);
                dialog.cancel();
            }
        });
    }


    private void DeleteData(String ac_id,int position){
        Map<String, String> map = new HashMap<String, String>();
        map.put("ac_id", ac_id);
        JSONObject data = new JSONObject(map);

        urlSetting = new UrlSetting(context);
        URL_DELETE = urlSetting.getUrl()+"activity/delete";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_DELETE,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    String success = response.getString("success");
                    if(success.equals("1")){
                        success_dialog("刪除成功");
                        removeData(position);
                    }else{
                        error_dialog("刪除失敗");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("響應錯誤1",e.toString());
                    error_dialog("發生例外錯誤，請檢查網路連線");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.toString());
                error_dialog("發生例外錯誤，請檢查網路連線");

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);

    }

    //Load activity data from database end
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void detail_dialog(JSONObject sendDatas) throws JSONException {

        activity_dialog = new Dialog(context);
        activity_dialog.setContentView(R.layout.activity_activity_page);
        activity_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView activity_name = activity_dialog.findViewById(R.id.activity_name);
        TextView member_name = activity_dialog.findViewById(R.id.m_name);
        TextView starttime = activity_dialog.findViewById(R.id.starttime);
        TextView endtime = activity_dialog.findViewById(R.id.endtime);
        TextView context = activity_dialog.findViewById(R.id.context);
        TextView total = activity_dialog.findViewById(R.id.total);
        TextView apply = activity_dialog.findViewById(R.id.apply);
        Button add = activity_dialog.findViewById(R.id.add);
        Button selectPeople = activity_dialog.findViewById(R.id.selectPeople);
        ProgressBar loading = activity_dialog.findViewById(R.id.loading);
        ProgressBar peopleLoad = activity_dialog.findViewById(R.id.peopleLoad);
        ImageView close_imageView = activity_dialog.findViewById(R.id.close_imageView);
        ImageView editActivity = activity_dialog.findViewById(R.id.editActivity);
        ImageSlider activity_img = activity_dialog.findViewById(R.id.activity_img);
        Button loadpeople = activity_dialog.findViewById(R.id.loadpeople);

        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        if(sessionUserData.get(SessionManager.A_ID).equals("3")){
            editActivity.setVisibility(View.GONE);
        }

        if(m_id.equals("1") || m_id.equals("2")){
            add.setVisibility(View.GONE);
        }

        //編輯活動按鈕
        editActivity.setOnClickListener(new View.OnClickListener() {
            String update_acid;
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(activity_dialog.getContext(),R.anim.click_style);
                editActivity.startAnimation(animation);
                String post_id = (String)view.getTag();
                Dialog dialog_setting;
                dialog_setting = new Dialog(activity_dialog.getContext());
                dialog_setting.setContentView(R.layout.activity_setting_dialog);
                dialog_setting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView settingActivityName = dialog_setting.findViewById(R.id.settingActivityName);
                TextView settingActivityDate = dialog_setting.findViewById(R.id.settingActivityDate);
                TextView settingActivityPeople = dialog_setting.findViewById(R.id.settingActivityPeople);
                TextView settingActivityContext = dialog_setting.findViewById(R.id.settingActivityContext);
                Window dialogWindow = dialog_setting.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
                layoutParams.y = 20;
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(layoutParams);
                dialog_setting.getWindow().getAttributes().windowAnimations = R.style.DialogSlide;
                dialog_setting.show();
                try {
                    update_acid = sendDatas.getString("ac_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //修改活動名稱
                settingActivityName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activityUpdate(update_acid,activity_name.getText().toString() , "name");
                        dialog_setting.cancel();
                    }
                });
                //修改活動日期and時間
                settingActivityDate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        JSONObject dateDatas = new JSONObject();
                        try {

                            dateDatas.put("ac_id",update_acid);
                            dateDatas.put("startDate",sendDatas.getString("start_date"));
                            dateDatas.put("endDate",sendDatas.getString("end_date"));
                            dateDatas.put("startTime",sendDatas.getString("start_time"));
                            dateDatas.put("endTime",sendDatas.getString("end_time"));
                            dialog_setting.cancel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        updateActivityDateAndTime(dateDatas,"date");
                    }
                });
                //修改活動總人數
                settingActivityPeople.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activityUpdate(update_acid,total.getText().toString(),"people");
                        dialog_setting.cancel();
                    }
                });
                //修改活動內容
                settingActivityContext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activityUpdate(update_acid,context.getText().toString(),"context");
                        dialog_setting.cancel();
                    }
                });
            }
        });

        //如以參加活動，就將按鈕顏色改變
        add.setTag(sendDatas.getString("check"));
        if(add.getTag().equals("1")){
            add.setText("取消參與");
            add.setBackground(activity_dialog.getContext().getDrawable(R.drawable.activity_button_end));
        }

        selectPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(apply.getText().equals("0"+" 人")){
                    error_dialog("目前無參與人員");
                }else{
                    Intent intent = new Intent(activity_dialog.getContext(), SelectPeopleActivity.class);
                    try {
                        intent.putExtra("ac_id",sendDatas.getString("ac_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    activity_dialog.getContext().startActivity(intent);
                }
            }
        });



        JSONArray activity_image = sendDatas.getJSONArray("a_image");
        List<SlideModel> slideModels = new ArrayList<>();
        if(activity_image.length()>0){
            activity_img.setVisibility(View.VISIBLE);
            for(int i = 0;i<activity_image.length();i++){
                try {
                    slideModels.add(new SlideModel(String.valueOf(activity_image.get(i)), null));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            activity_img.setImageList(slideModels,true);
            activity_img.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemSelected(int i) {
                    String imageUrl = slideModels.get(i).getImageUrl();
                    bigImage(imageUrl);
                }
            });
        }
        if(sendDatas.getString("start_date").equals(sendDatas.getString("end_date"))){
            starttime.setText(sendDatas.getString("start_date"));
        }else{
            starttime.setText(sendDatas.getString("start_date")+" ~ "+sendDatas.getString("end_date"));
        }
        activity_name.setText(sendDatas.getString("ac_name"));
        member_name.setText(sendDatas.getString("m_name"));
        endtime.setText(sendDatas.getString("start_time")+" ~ "+sendDatas.getString("end_time"));
        if(sendDatas.getString("total_people").equals("無")){
            total.setText(sendDatas.getString("total_people"));
        }else{
            total.setText(sendDatas.getString("total_people")+" 人");
        }
        apply.setText(sendDatas.getString("apply_people")+" 人");
        context.setText(sendDatas.getString("ac_context"));
        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity_dialog.cancel();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                add.setVisibility(View.GONE);
                String URLACTIVITY = "";
                if(add.getTag().equals("1")){
                    urlSetting = new UrlSetting(activity_dialog.getContext());
                    URL_ClEAR = urlSetting.getUrl()+"activity/cancel";
                    URLACTIVITY = URL_ClEAR;
                }else{
                    if(total.getText().equals(apply.getText()) && !total.equals("無")){
                        error_dialog("目前活動人員已滿");
                        return;
                    }
                    urlSetting = new UrlSetting(activity_dialog.getContext());
                    URL_JOIN = urlSetting.getUrl()+"activity/join";
                    URLACTIVITY = URL_JOIN;
                }
                JSONObject datas = new JSONObject();
                try{
                    datas.put("ac_id",sendDatas.getString("ac_id"));
                    datas.put("m_id",user_id);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLACTIVITY, datas, new Response.Listener<JSONObject>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String success = response.getString("success");
                            if(success.equals("1")){
                                success_dialog("參加成功");
                                add.setText("取消參與");
                                add.setBackground(activity_dialog.getContext().getDrawable(R.drawable.activity_button_end));
                                add.setTag("1"); // 1為參加過後的代碼
                            }else if(success.equals("0")){
                                error_dialog("失敗");
                            }else if(success.equals("2")){
                                success_dialog("取消成功");
                                add.setText("參加活動");
                                add.setBackground(activity_dialog.getContext().getDrawable(R.drawable.activity_button));
                                add.setTag("0"); // 0為未參加活動的代碼
                            }else if(success.equals("3")){
                                error_dialog("取消失敗");
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            error_dialog("出現錯誤");
                            Log.e("insertActivity()_JSONException Error", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(activity_dialog.getContext());
                requestQueue.add(jsonObjectRequest);
                loading.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
            }

        });
        //get total people count
        loadpeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peopleLoad.setVisibility(View.VISIBLE);
                loadpeople.setVisibility(View.GONE);
                JSONObject datas = new JSONObject();
                try{
                    datas.put("ac_id",sendDatas.getString("ac_id"));
                }catch (JSONException e){
                    e.printStackTrace();
                }

                urlSetting = new UrlSetting(activity_dialog.getContext());
                URL_GETTOTALPEOPLE = urlSetting.getUrl()+"activity/getTotal";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_GETTOTALPEOPLE, datas, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            apply.setText(response.getString("data")+" 人");
                            peopleLoad.setVisibility(View.GONE);
                            loadpeople.setVisibility(View.VISIBLE);
                        }catch (JSONException e){
                            e.printStackTrace();
                            error_dialog("出現錯誤，請重新操作");
                            Log.e("insertActivity()_JSONException Error", e.toString());
                            peopleLoad.setVisibility(View.GONE);
                            loadpeople.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error_dialog("響應錯誤，請重新操作");
                        error.printStackTrace();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(activity_dialog.getContext());
                requestQueue.add(jsonObjectRequest);
            }
        });
        activity_dialog.show();
        Window window = activity_dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
    }

    //放大圖片的方法
    private void bigImage(String imageUrl){
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.image_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imageView = dialog.findViewById(R.id.dialogimageView);
        Glide.with(context).load(imageUrl).into(imageView);
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
    }

    private void error_dialog(String text){
        Dialog dialog;
        dialog = new Dialog(context);
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
        dialog = new Dialog(context);
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

    private void getAlertdialog(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    private void activityUpdate(String ac_id,String data ,String Type){
        Dialog dialog_update;
        dialog_update = new Dialog(context);
        dialog_update.setContentView(R.layout.update_username_dialog);
        dialog_update.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText updateDataEditText = dialog_update.findViewById(R.id.updateUserName);
        Button btn_update = dialog_update.findViewById(R.id.btn_updateName);
        Window dialogWindow = dialog_update.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.y = 20;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(layoutParams);
        updateDataEditText.setHint("輸入修改資料");
        updateDataEditText.setText(data.trim());
        dialog_update.show();


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("123123","123123");
                JSONObject updateDatas = new JSONObject();
                try {
                    updateDatas.put("ac_id",ac_id);
                    updateDatas.put("updateType",Type);
                    updateDatas.put("updateData",updateDataEditText.getText().toString());
                    updateDataInDatabase(updateDatas);
                    dialog_update.cancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void updateActivityDateAndTime(JSONObject datas ,String type){
        Dialog dialog_update;
        dialog_update = new Dialog(context);
        dialog_update.setContentView(R.layout.update_activity_datetime);
        dialog_update.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView updateStartDate = dialog_update.findViewById(R.id.updateStartDate);
        TextView updateEndDate = dialog_update.findViewById(R.id.updateEndDate);
        TextView updateStartTime = dialog_update.findViewById(R.id.updateStartTime);
        TextView updateEndTime = dialog_update.findViewById(R.id.updateEndTime);
        Button btn_startDate = dialog_update.findViewById(R.id.btn_startDate);
        Button btn_endDate = dialog_update.findViewById(R.id.btn_endDate);
        Button btn_startTime = dialog_update.findViewById(R.id.btn_startTime);
        Button btn_endTime = dialog_update.findViewById(R.id.btn_endTime);
        Button btn_send = dialog_update.findViewById(R.id.btn_send);

        Window dialogWindow = dialog_update.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.y = 20;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(layoutParams);
        try {
            updateStartDate.setText(datas.getString("startDate"));
            updateEndDate.setText(datas.getString("endDate"));
            updateStartTime.setText(datas.getString("startTime"));
            updateEndTime.setText(datas.getString("endTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog_update.show();
        btn_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int s_year = calendar.get(Calendar.YEAR);
                int s_month = calendar.get(Calendar.MONTH);
                int s_day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String strMonth = Integer.toString(month);
                        String format = setDateFormat(year,month,day);
                        updateStartDate.setText(format);
                    }
                }, s_year,s_month, s_day).show();
            }
        });

        btn_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int s_year = calendar.get(Calendar.YEAR);
                int s_month = calendar.get(Calendar.MONTH);
                int s_day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String format = setDateFormat(year,month,day);
                        if(!dateParse(updateStartDate.getText().toString() , format)){
                            error_dialog("結束日期不可少於開始日期");
                            return;
                        }
                        updateEndDate.setText(format);
                    }
                }, s_year,s_month, s_day).show();
            }
        });

        btn_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        s_hour = hour;
                        s_minute = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,s_hour,s_minute);
                        String ans = (String) DateFormat.format("aa hh:mm",calendar);
                        updateStartTime.setText(ans);
                    }
                },12,0,false);

                timePickerDialog.updateTime(s_hour,s_minute);
                timePickerDialog.show();
            }
        });
        btn_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        s_hour = hour;
                        s_minute = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,s_hour,s_minute);
                        String ans = (String) DateFormat.format("aa hh:mm",calendar);
                        updateEndTime.setText(ans);
                    }
                },12,0,false);

                timePickerDialog.updateTime(s_hour,s_minute);
                timePickerDialog.show();
            }
        });

        // send data in database
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject dateData = new JSONObject();
                try {
                    dateData.put("ac_id",datas.getString("ac_id"));
                    dateData.put("updateStartDate",updateStartDate.getText());
                    dateData.put("updateEndDate",updateEndDate.getText());
                    dateData.put("updateStartTime",updateStartTime.getText());
                    dateData.put("updateEndTime",updateEndTime.getText());
                    dateData.put("updateType",type);
                    updateDataInDatabase(dateData);
                    dialog_update.cancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private static boolean dateParse(String date1 , String date2){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = simpleDateFormat.parse(date1);
            d2 = simpleDateFormat.parse(date2);
            return d1.getTime() <= d2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    // send 欲修改data 至 database 並 return 結果
    private void updateDataInDatabase(JSONObject datas){
        urlSetting = new UrlSetting(activity_dialog.getContext());
        String url = urlSetting.getUrl()+"activity/edit";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String success = response.getString("success");
                    String message = response.getString("message");
//                    JSONArray jsonArray = response.getJSONArray("data");
                    if(success.equals("1")) {
                        activity_dialog.cancel();
                        LoadData(message);

                    }else {
                        error_dialog("修改失敗"+success);
                    }
                }catch (JSONException e){
                    error_dialog("例外錯誤，請重新操作");
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error_dialog("響應錯誤，請檢查網路");
                error.printStackTrace();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }



}


