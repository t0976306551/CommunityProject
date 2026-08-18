package com.example.communityproject.LoginAndRegister;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateCommunityAdapter extends RecyclerView.Adapter<CreateCommunityAdapter.ViewHolder>{
    private List<CreateCommunityData> list_data;
    UrlSetting urlSetting;
    Context context;

    public CreateCommunityAdapter(Context context, List<CreateCommunityData> list_data){
        this.list_data = list_data;
        this.context = context;
        notifyDataSetChanged();
    }

    public void setData(List<CreateCommunityData> list_data){
        this.list_data = list_data;
    }

    public void removeData(int position) {
        list_data.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CreateCommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_community_item , parent, false);
        return new CreateCommunityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateCommunityAdapter.ViewHolder holder, int position) {
        CreateCommunityData createCommunityData = list_data.get(position);
        holder.communityName.setText(createCommunityData.getCommunity_name());
        holder.communityCardView.setTag(createCommunityData.getId());

    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView communityName;
        CardView communityCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            communityCardView = itemView.findViewById(R.id.communityCardView);
            communityName = itemView.findViewById(R.id.communityName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoadData(itemView.getTag().toString());
                }
            });
        }
    }

    private void LoadData(String id){
        JSONObject datas = new JSONObject();
        try {
            datas.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlSetting = new UrlSetting(context);
        String URL_LOADDATA = urlSetting.getUrl()+"user/selectCreateCommunity";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOADDATA,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray a_image = new JSONArray();
                    String  create_id = "", userName="",useeSex="",communityName="",communityAddress="",communityPhone="",image = "";
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        create_id = jsonObject.getString("id");
                        userName = jsonObject.getString("manager_name");
                        useeSex = jsonObject.getString("manager_sex");
                        communityName = jsonObject.getString("community_name");
                        communityAddress = jsonObject.getString("community_address");
                        communityPhone = jsonObject.getString("manager_phone");
                        image = jsonObject.getString("manager_image");
                    }
                    detail_dialog(create_id,userName,useeSex,communityName,communityAddress,communityPhone,image);

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



    private void detail_dialog(String create_id,String userName,String sex,String communityName,String communityAddress,String communityPhone,String image ) {
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.create_community_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        CircleImageView userImage = dialog.findViewById(R.id.userImage);
        TextView m_name = dialog.findViewById(R.id.m_name);
        TextView m_sex = dialog.findViewById(R.id.m_sex);
        TextView c_name = dialog.findViewById(R.id.c_name);
        TextView c_address = dialog.findViewById(R.id.c_address);
        TextView c_phone = dialog.findViewById(R.id.c_phone);
        Button btn_passCreate = dialog.findViewById(R.id.btn_passCreate);

        if(image.equals("")){
            userImage.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(context).load(image).into(userImage);
        }
        m_name.setText("管理員名稱："+userName);
        m_sex.setText("管理員性別："+sex);
        c_name.setText("社區名稱："+communityName);
        c_address.setText("社區名稱："+communityAddress);
        c_phone.setText("聯絡電話："+communityPhone);

        btn_passCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successCreate(create_id);
                dialog.cancel();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }

    private void successCreate(String create_id){
        JSONObject datas = new JSONObject();
        try {
            datas.put("id",create_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlSetting = new UrlSetting(context);
        String URL_LOADDATA = urlSetting.getUrl()+"user/successCreateCommunity";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOADDATA,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray a_image = new JSONArray();
                    String success = response.getString("success");
                    String message = response.getString("message");
                    if(success.equals("1")){
                        success_dialog(message);
                    }else if(success.equals("0")){
                        error_dialog("審核失敗");
                    }

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

}
