package com.example.communityproject.Post;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.communityproject.SessionManager;
import com.example.communityproject.UrlSetting;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private List<ReplyCardViewDate> list_reply;
    private Context context;
    SessionManager sessionManager;
    String userID;
    private String URL_DELETE = "http://10.0.2.2/usr/public/reply/delete";
    UrlSetting urlSetting;
    public ReplyAdapter(Context context, List<ReplyCardViewDate> list_reply){
        this.list_reply = list_reply;
        this.context = context;
    }
    public void setData(List<ReplyCardViewDate> list_reply){
        this.list_reply = list_reply;
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        list_reply.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_replyitem , parent, false);
        return new ReplyAdapter.ViewHolder(view);

    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReplyCardViewDate replyCardViewDate = list_reply.get(position);
        holder.uesrname.setText(replyCardViewDate.getName());
        holder.replycontext.setText(replyCardViewDate.getContext());
        holder.inserdate.setText(replyCardViewDate.getInsertDate());
        holder.cancel.setTag(replyCardViewDate.getId());
        holder.reply_cardview.setTag(replyCardViewDate.getM_id());
        holder.replyMessage.setTag(replyCardViewDate.getId());

        if(replyCardViewDate.getReplyType().equals("reply")){
            holder.replyMessage.setVisibility(View.VISIBLE);
            URL_DELETE = "http://10.0.2.2/usr/public/reply/delete";
        }else if(replyCardViewDate.getReplyType().equals("replyMessage")){
            holder.replyMessage.setVisibility(View.GONE);
            URL_DELETE = "http://10.0.2.2/usr/public/reply/deleteReplyMessage";
        }

        if(!replyCardViewDate.getUserImage().equals("")){
            Glide.with(context).load(replyCardViewDate.getUserImage()).into(holder.otherImage);
        }

        sessionManager = new SessionManager(context);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        userID = sessionUserData.get(sessionManager.USERID);
        String reply_userID;
        reply_userID = replyCardViewDate.getM_id().toString();

        if(userID.equals(reply_userID)){
            holder.cancel.setVisibility(View.VISIBLE);
        }else{
            holder.cancel.setVisibility(View.GONE);
        }

        //刪除貼文
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog((String)view.getTag(),position);
            }
        });
        //回覆留言按鈕
        holder.replyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ReplyMessageActivity.class);
                intent.putExtra("reply_id",(String)view.getTag());
                intent.putExtra("userName",replyCardViewDate.getName());
                intent.putExtra("userImage",replyCardViewDate.getUserImage());
                intent.putExtra("replyContext",replyCardViewDate.getContext());
                intent.putExtra("insertDate",replyCardViewDate.getInsertDate());
                intent.putExtra("post_id",replyCardViewDate.getP_id());
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return list_reply.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView otherImage,cancel;
        TextView uesrname,inserdate,replycontext,replyMessage;
        CardView reply_cardview;
        Dialog dialog;
        public ViewHolder(View v){
            super(v);
            uesrname = (TextView) v.findViewById(R.id.uesrname);
            inserdate = (TextView) v.findViewById(R.id.inserdate);
            replycontext = (TextView) v.findViewById(R.id.replycontext);
            otherImage = (CircleImageView) v.findViewById(R.id.otherImage);
            cancel = (CircleImageView) v.findViewById(R.id.cancel);
            reply_cardview = (CardView) v.findViewById(R.id.reply_cardview);
            replyMessage = (TextView) v.findViewById(R.id.replyMessage);

        }

    }
    private void deleteDialog(String r_id,int position){
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        Button btn_check = dialog.findViewById(R.id.btn_check);
        TextView textView = dialog.findViewById(R.id.textView);
        TextView textView2 = dialog.findViewById(R.id.textView2);
        textView.setText("刪除留言");
        textView2.setText("是否要刪除留言");
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
                DeleteData(r_id,position);
                dialog.cancel();
            }
        });
    }

    private void DeleteData(String r_id,int position){
        Map<String, String> map = new HashMap<String, String>();
        map.put("r_id", r_id);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(context);
        URL_DELETE = urlSetting.getUrl()+"reply/delete";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_DELETE,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    String success = response.getString("success");
                    if(success.equals("1")){
                        removeData(position);
                    }else{
                        Toast.makeText(context, "刪除失敗", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("響應錯誤1",e.toString());
                    Toast.makeText(context, "發生例外錯誤，如還有此情況請向客服人員反應" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.toString());
                Toast.makeText(context, "發生響應錯誤，如還有此情況請向客服人員反應" + error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);

    }




}
