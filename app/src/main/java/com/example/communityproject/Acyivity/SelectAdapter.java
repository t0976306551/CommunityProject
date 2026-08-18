package com.example.communityproject.Acyivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.communityproject.MainActivity;
import com.example.communityproject.R;
import com.example.communityproject.SessionManager;
import com.example.communityproject.UrlSetting;
import com.example.communityproject.UserCheck.UserCheckAdapter;
import com.example.communityproject.UserCheck.UsercheckCardViewData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {
    private List<UsercheckCardViewData> list_data;
    private Context context;
    private String URL_LOADDATA;
    SessionManager sessionManager;
    UrlSetting urlSetting;
    public SelectAdapter(Context context, List<UsercheckCardViewData> list_data){
        this.list_data = list_data;
        this.context = context;

    }

    public void setData(List<UsercheckCardViewData> list_data){
        this.list_data = list_data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectAdapter.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_usercheckitem , parent, false);
        return new SelectAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectAdapter.ViewHolder holder, int position) {
        UsercheckCardViewData usercheckCardViewData = list_data.get(position);
        holder.userName.setText(usercheckCardViewData.getName());
        holder.btn_success.setTag(usercheckCardViewData.getId());
        holder.btn_delete.setTag(usercheckCardViewData.getId());
        holder.userCheckCardView.setTag(usercheckCardViewData.getId());
        if(usercheckCardViewData.getImage().equals("")){
            holder.userImage.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(context).load(usercheckCardViewData.getImage()).into( holder.userImage);
        }
        holder.btn_success.setVisibility(View.GONE);
        holder.btn_delete.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;
        CircleImageView btn_success,btn_delete;
        CardView userCheckCardView;
        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.userName);
            userImage = (CircleImageView) v.findViewById(R.id.userImage);
            btn_success = (CircleImageView) v.findViewById(R.id.btn_success);
            btn_delete = (CircleImageView) v.findViewById(R.id.btn_delete);
            userCheckCardView = v.findViewById(R.id.userCheckCardView);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoadData(String.valueOf(view.getTag()));
                }
            });

        }
    }


    private void LoadData(String m_id){
        JSONObject datas = new JSONObject();
        try {
            datas.put("m_id",m_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlSetting = new UrlSetting(context);
        URL_LOADDATA = urlSetting.getUrl()+"user/load";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOADDATA,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray a_image = new JSONArray();
                    String  m_id="",user_name="",authority="",image="",a_name="",sex = "";
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        m_id = jsonObject.getString("m_id");
                        user_name = jsonObject.getString("m_name");
                        authority = jsonObject.getString("authority");
                        image = jsonObject.getString("image");
                        a_name = jsonObject.getString("a_name");
                        sex = jsonObject.getString("m_sex");
                    }
                    detail_dialog(m_id,user_name,authority,image,a_name,sex);

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
    private void detail_dialog(String m_id,String user_name,String authority,String image , String a_name,String sex) {
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_people_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        CircleImageView userImage = dialog.findViewById(R.id.userImage);
        TextView m_name = dialog.findViewById(R.id.m_name);
        TextView m_sex = dialog.findViewById(R.id.m_sex);
        TextView autiorityName = dialog.findViewById(R.id.autiorityName);
        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        m_name.setText(user_name);
        m_sex.setText("會員性別："+sex);
        autiorityName.setText("會員權限："+a_name);
        if(image.equals("")){
            userImage.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(context).load(image).into(userImage);
        }
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
    }

}
