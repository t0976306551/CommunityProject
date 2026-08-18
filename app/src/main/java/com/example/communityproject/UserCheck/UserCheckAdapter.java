package com.example.communityproject.UserCheck;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.communityproject.LoginAndRegister.LoginActivity;
import com.example.communityproject.MainActivity;
import com.example.communityproject.R;
import com.example.communityproject.SessionManager;
import com.example.communityproject.UrlSetting;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserCheckAdapter extends RecyclerView.Adapter<UserCheckAdapter.ViewHolder> {
    private List<UsercheckCardViewData> list_data;
    private Context context;
    SessionManager sessionManager;
    private String URL_SUCCESS;
    private String URL_DELETE;
    UrlSetting urlSetting;
    String userID;
    UserCheckAdapter userCheckAdapter;
    public UserCheckAdapter(Context context, List<UsercheckCardViewData> list_data){
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


    public UserCheckAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_usercheckitem , parent, false);
        return new UserCheckAdapter.ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull UserCheckAdapter.ViewHolder holder, int position) {
        UsercheckCardViewData usercheckCardViewData = list_data.get(position);
        holder.userName.setText(usercheckCardViewData.getName());
        holder.btn_success.setTag(usercheckCardViewData.getId());
        holder.btn_delete.setTag(usercheckCardViewData.getId());
        if(usercheckCardViewData.getImage().equals("")){
            holder.userImage.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(context).load(usercheckCardViewData.getImage()).into( holder.userImage);
        }
        holder.btn_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog((String)view.getTag(),position,"1");
//                successCheck((String)view.getTag(),position);
            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog((String)view.getTag(),position,"2");
            }
        });
    }
    @Override
    public int getItemCount() {
        return list_data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;
        CircleImageView btn_success,btn_delete;
        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.userName);
            userImage = (CircleImageView) v.findViewById(R.id.userImage);
            btn_success = (CircleImageView) v.findViewById(R.id.btn_success);
            btn_delete = (CircleImageView) v.findViewById(R.id.btn_delete);




        }
    }

    private void deleteDialog(String m_id,int position,String type){
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        Button btn_check = dialog.findViewById(R.id.btn_check);
        TextView textView = dialog.findViewById(R.id.textView);
        TextView textView2 =  dialog.findViewById(R.id.textView2);
        if(type.equals("1")){
            textView.setText("通過審核");
            textView2.setText("確認是否通過審核");
        }else{
            textView.setText("刪除會員");
            textView2.setText("確認是否刪除會員");
        }

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
                if(type.equals("1")){
                    successCheck(m_id,position);
                    dialog.cancel();
                }else {
                    deleteCheck(m_id,position);
                    dialog.cancel();
                }

            }
        });
    }


    private void successCheck(String m_id,int position){
        urlSetting = new UrlSetting(context);
        URL_SUCCESS = urlSetting.getUrl()+"user/success"+"?m_id="+m_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_SUCCESS,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if(success.equals("1")) {
                        Toast.makeText(context, "審核成功", Toast.LENGTH_SHORT).show();
                        removeData(position);
                    }else{
                        Toast.makeText(context, "審核失敗", Toast.LENGTH_SHORT).show();
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

    private void deleteCheck(String m_id,int position){
        urlSetting = new UrlSetting(context);
        URL_DELETE = urlSetting.getUrl()+"user/delete"+"?m_id="+m_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_DELETE,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if(success.equals("1")) {
                        Toast.makeText(context, "刪除成功", Toast.LENGTH_SHORT).show();
                        removeData(position);
                    }else{
                        Toast.makeText(context, "刪除失敗", Toast.LENGTH_SHORT).show();
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



}
