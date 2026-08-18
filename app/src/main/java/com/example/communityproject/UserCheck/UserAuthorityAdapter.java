package com.example.communityproject.UserCheck;

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
import android.widget.ArrayAdapter;
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
import com.example.communityproject.LoginAndRegister.RegisterActivity;
import com.example.communityproject.MainActivity;
import com.example.communityproject.R;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAuthorityAdapter extends  RecyclerView.Adapter<UserAuthorityAdapter.ViewHolder>{
    private List<UsercheckCardViewData> list_data;
    private Context context;
    private String URL_LOADDATA ;
    private String URL_AUTHORITY ;
    private String URL_OTHERAUTHORITY ;
    private String URL_UpdateAUTHORITY ;
    UrlSetting urlSetting;
    String author_id;
    ArrayList authorityList = new ArrayList<>();
    public UserAuthorityAdapter(Context context, List<UsercheckCardViewData> list_data){
        this.list_data = list_data;
        this.context = context;

    }

    public void setData(List<UsercheckCardViewData> list_data){
        this.list_data = list_data;
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        list_data.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public UserAuthorityAdapter.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_authority , parent, false);
        return new UserAuthorityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAuthorityAdapter.ViewHolder holder, int position) {
        UsercheckCardViewData usercheckCardViewData = list_data.get(position);
        holder.userName.setText(usercheckCardViewData.getName());
        holder.authorityName.setText(usercheckCardViewData.getAuthorityName());
        holder.userCardView.setTag(usercheckCardViewData.getId());
        if(usercheckCardViewData.getImage().equals("")){
            holder.userImage.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(context).load(usercheckCardViewData.getImage()).into( holder.userImage);
        }
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName,authorityName;
        CircleImageView userImage;
        CardView userCardView ;
        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.userName);
            userImage = (CircleImageView) v.findViewById(R.id.userImage);
            authorityName = (TextView) v.findViewById(R.id.authorityName);
            userCardView = v.findViewById(R.id.userCardView);
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


    private void detail_dialog(String m_id,String user_name,String authority,String image , String a_name,String sex){
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.user_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        CircleImageView userImage = dialog.findViewById(R.id.userImage);
        TextView m_name = dialog.findViewById(R.id.m_name);
        TextView m_sex = dialog.findViewById(R.id.m_sex);
        TextView autiorityName = dialog.findViewById(R.id.autiorityName);
        Button btn_update = dialog.findViewById(R.id.btn_update);
        Spinner autioritySpinner = dialog.findViewById(R.id.autioritySpinner);
        authorityList.clear();
        if(image.equals("")){
            userImage.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(context).load(image).into(userImage);
        }

        m_name.setText(user_name);
        m_sex.setText("會員性別："+sex);
        if(authorityList.size()==0){
            authorityList.add(a_name);
            getAuthority(m_id);
        }
        autioritySpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item,authorityList));
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

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String A_name = autioritySpinner.getSelectedItem().toString().trim();
                JSONObject datas = new JSONObject();
                try {
                    datas.put("m_id",m_id);
                    datas.put("a_name",A_name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                urlSetting = new UrlSetting(context);
                URL_UpdateAUTHORITY = urlSetting.getUrl()+"user/updateAuthority";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_UpdateAUTHORITY,datas, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success = response.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(dialog.getContext(),"修改成功",Toast.LENGTH_SHORT).show();
                                String newAuthorty = autioritySpinner.getSelectedItem().toString().trim();
                                authorityList.clear();
                                authorityList.add(newAuthorty);
                                getAuthority(m_id);
                                autioritySpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item,authorityList));
                            }else{
                                Toast.makeText(dialog.getContext(),"修改失敗",Toast.LENGTH_SHORT).show();
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
        });


        //GET AUTHORITY



    }
    private void getAuthority(String m_id){
        JSONObject datas = new JSONObject();
        try {
            datas.put("m_id",m_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlSetting = new UrlSetting(context);
        URL_AUTHORITY = urlSetting.getUrl()+"user/getAuthority";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_AUTHORITY,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray a_image = new JSONArray();
                    String  m_id="",authority="",a_name="";
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        m_id = jsonObject.getString("m_id");
                        authority = jsonObject.getString("authority");
                        a_name = jsonObject.getString("a_name");



                        getOtherAuthority(authority);
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

    private void getOtherAuthority(String a_id){
        JSONObject datas = new JSONObject();
        try {
            datas.put("id",a_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlSetting = new UrlSetting(context);
        URL_OTHERAUTHORITY = urlSetting.getUrl()+"user/getOtherAuthority";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_OTHERAUTHORITY,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray a_image = new JSONArray();
                    String  authority="",a_name="";
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        authority = jsonObject.getString("id");
                        a_name = jsonObject.getString("a_name");
                        authorityList.add(a_name);
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

    private void updateAuthority(String m_id ,String A_name){

    }





}
