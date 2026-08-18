package com.example.communityproject.Acyivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder>{
    private List<RecordCardViewData> list_Activity;
    private Context context;
    private String URL_LOADDATA;
    private String URL_DELETE;
    SessionManager sessionManager;
    String m_id,a_id,c_id,user_id;
    UrlSetting urlSetting;
    public RecordAdapter(Context context, List<RecordCardViewData> list_Activity) {
        this.list_Activity = list_Activity;
        this.context=context;
    }

    public RecordAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
    }

    public void setData(List<RecordCardViewData> list_Activity){
        this.list_Activity = list_Activity;
        notifyDataSetChanged();
    }

    public RecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_activityitem, parent, false);
        return new RecordAdapter.ViewHolder(view);

    }
    @NonNull
    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, int position) {
        RecordCardViewData recordCardViewData = list_Activity.get(position);
        holder.activityName.setText(recordCardViewData.getA_name());
        holder.activityCardView.setTag(recordCardViewData.getA_id());
        holder.btn_delete.setTag(recordCardViewData.getA_id());

        sessionManager = new SessionManager(context);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        m_id = sessionUserData.get(sessionManager.A_ID);
        user_id = sessionUserData.get(sessionManager.USERID);
        if(m_id.equals("3")){
            holder.btn_delete.setVisibility(View.GONE);
        }else{
            holder.btn_delete.setVisibility(View.VISIBLE);
        }
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
            activityName = (TextView) v.findViewById(R.id.activity_name);
            activityCardView = (CardView) v.findViewById(R.id.activityCardView);
            btn_delete = (CircleImageView) v.findViewById(R.id.btn_delete);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    a_id = (String)view.getTag();
                    LoadData(a_id);
                }
            });
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    a_id = (String)view.getTag();
                    deleteDialog(a_id);
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
        urlSetting = new UrlSetting(context);
        URL_LOADDATA = urlSetting.getUrl()+"activity/load";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOADDATA,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("loadpost", String.valueOf(response));
                try {
                    JSONArray a_image = new JSONArray();
                    String  ac_id="",ac_name="",ac_context="",start_date="",end_date="",m_name="",total_people = "",apply_people="",start_time ="",end_time="",check = "";
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ac_id = jsonObject.getString("ac_id");
                        ac_name = jsonObject.getString("ac_name");
                        ac_context = jsonObject.getString("ac_context");
                        start_date = jsonObject.getString("start_date");
                        end_date = jsonObject.getString("end_date");
                        m_name = jsonObject.getString("m_name");
                        start_time = jsonObject.getString("start_time");
                        end_time = jsonObject.getString("end_time");
                        total_people = jsonObject.getString("total_people");
                        apply_people = jsonObject.getString("apply_people");
                        a_image = jsonObject.getJSONArray("images");
                        check = jsonObject.getString("check");
                    }
                    detail_dialog(ac_id,ac_name,ac_context,start_date,end_date,m_name,total_people,apply_people,start_time,end_time,a_image,check);
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
    //Load activity data from database end

    private void detail_dialog(String ac_id,String ac_name, String ac_context, String start_date, String end_date, String m_name, String total_people,String apply_people, String start_time,String end_time ,JSONArray a_image,String check) {
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_activity_page);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView activity_name = dialog.findViewById(R.id.activity_name);
        TextView member_name = dialog.findViewById(R.id.m_name);
        TextView starttime = dialog.findViewById(R.id.starttime);
        TextView endtime = dialog.findViewById(R.id.endtime);
        TextView context = dialog.findViewById(R.id.context);
        TextView total = dialog.findViewById(R.id.total);
        TextView apply = dialog.findViewById(R.id.apply);
        TextView applyTitle = dialog.findViewById(R.id.applyTitle);
        Button add = dialog.findViewById(R.id.add);
        Button selectPeople = dialog.findViewById(R.id.selectPeople);
        ProgressBar loading = dialog.findViewById(R.id.loading);
        ProgressBar peopleLoad = dialog.findViewById(R.id.peopleLoad);
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        ImageView editActivity = dialog.findViewById(R.id.editActivity);
        ImageSlider activity_img = dialog.findViewById(R.id.activity_img);
        Button loadpeople = dialog.findViewById(R.id.loadpeople);
        add.setVisibility(View.GONE);
        loadpeople.setVisibility(View.GONE);
        applyTitle.setText("參與人數：");
        List<SlideModel> slideModels = new ArrayList<>();
        if(a_image.length()>0){
            activity_img.setVisibility(View.VISIBLE);
            for(int i = 0;i<a_image.length();i++){
                try {
                    slideModels.add(new SlideModel(String.valueOf(a_image.get(i)), null));
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

        editActivity.setVisibility(View.GONE);

        selectPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(apply.getText().equals("0"+" 人")){
                    error_dialog("無參與人員");
                }else{
                    Intent intent = new Intent(dialog.getContext(), SelectPeopleActivity.class);
                    intent.putExtra("ac_id",ac_id);
                    dialog.getContext().startActivity(intent);
                }
            }
        });


        if(start_date.equals(end_date)){
            starttime.setText(start_date);
        }else{
            starttime.setText(start_date+" ~ "+end_date);
        }
        activity_name.setText(ac_name);
        member_name.setText(m_name);
        endtime.setText(start_time+" ~ "+end_time);
        total.setText(total_people+" 人");
        apply.setText(apply_people+" 人");
        context.setText(ac_context);

        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

    }

    private void deleteDialog(String ac_id){
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
                DeleteData(ac_id);
                dialog.cancel();
            }
        });
    }


    private void DeleteData(String ac_id){
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

                    }else{
                        error_dialog("刪除失敗");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("響應錯誤1",e.toString());
                    error_dialog("發生例外錯誤，請重新操作");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.toString());
//                Toast.makeText(context, "發生響應錯誤，如還有此情況請向客服人員反應" + error.toString(), Toast.LENGTH_SHORT).show();
                error_dialog("發生響應錯誤，如還有此情況請向客服人員反應");

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


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

}