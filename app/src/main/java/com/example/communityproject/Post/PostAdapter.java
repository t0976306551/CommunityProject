package com.example.communityproject.Post;

import android.annotation.SuppressLint;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.communityproject.Attraction.attraction_page_Activity;
import com.example.communityproject.PasswordUpdate_Activity;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostCardviewData> list_post;
    private String URLDELETEPOST;
    private Context context;
    UrlSetting urlSetting;
    SessionManager sessionManager;
    String userID;

    public PostAdapter(Context context, List<PostCardviewData> list_post){
        this.list_post = list_post;
        this.context = context;
        notifyDataSetChanged();
    }
    public void setData(List<PostCardviewData> list_post){
        this.list_post = list_post;
    }
    public void removeData(int position) {
        list_post.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activtiy_postitem , parent, false);
        return new PostAdapter.ViewHolder(view);
    }
    @NonNull
    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PostCardviewData postCardviewData = list_post.get(position);
        holder.post_username.setText(postCardviewData.getName());
        holder.post_title.setText(postCardviewData.getTitle());
        holder.post_context.setText(postCardviewData.getContext().trim());
        holder.post_datetime.setText(postCardviewData.getInsertTime());
        holder.post_id.setText(postCardviewData.getP_id());
        holder.insertReply.setTag(postCardviewData.getP_id());
        holder.btn_setAndDelete.setTag(postCardviewData.getP_id());

        sessionManager = new SessionManager(context);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        userID = sessionUserData.get(sessionManager.USERID);

        if(!postCardviewData.getId().equals(userID)){
            holder.btn_setAndDelete.setVisibility(View.GONE);
        }

        if(postCardviewData.getReply_check().equals("1")){
            holder.insertReply.setVisibility(View.VISIBLE);
            if(!postCardviewData.getReply_count().equals("0")){
                holder.insertReply.setText(postCardviewData.getReply_count()+"則留言");
            }
        }else{
            holder.insertReply.setVisibility(View.GONE);
        }
        if(postCardviewData.getM_image().equals("")){
            holder.user_image.setImageResource(R.drawable.user_preset);
        }else{
            Glide.with(context).load(postCardviewData.getM_image()).into(holder.user_image);
        }


        holder.btn_setAndDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.click_style);
                holder.btn_setAndDelete.startAnimation(animation);
                String post_id = (String)view.getTag();
                Dialog dialog;
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.post_setting_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView settingPost = dialog.findViewById(R.id.settingPost);
                TextView deletePost = dialog.findViewById(R.id.deletePost);
                TextView updateStapleRoad = dialog.findViewById(R.id.updateStapleRoad);
                View view_ui = dialog.findViewById(R.id.view_ui);
                updateStapleRoad.setVisibility(View.GONE);
                view_ui.setVisibility(View.GONE);
                Window dialogWindow = dialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
                layoutParams.y = 20;
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(layoutParams);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlide;
                dialog.show();


                settingPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation animation = AnimationUtils.loadAnimation(context,R.anim.click_style);
                        settingPost.startAnimation(animation);
                        Intent intent = new Intent(context, UpdatePostActivity.class);
                        intent.putExtra("p_id",post_id);
                        context.startActivity(intent);
                        dialog.cancel();
                    }
                });

                deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation animation = AnimationUtils.loadAnimation(context,R.anim.click_style);
                        deletePost.startAnimation(animation);
                        deleteDialog(post_id,position);
                        dialog.cancel();
                    }
                });

            }
        });

        List<SlideModel> slideModels = new ArrayList<>();
        try {
            JSONArray image_array = new JSONArray();
            image_array = postCardviewData.getPost_img();
            if(image_array.length() == 0){
                holder.post_img.setVisibility(View.GONE);
            }else{
                holder.post_img.setVisibility(View.VISIBLE);
                for(int i = 0;i<image_array.length();i++){

                    slideModels.add(new SlideModel(String.valueOf(image_array.get(i)), null));

                }
                holder.post_img.setImageList(slideModels,true);
                holder.post_img.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onItemSelected(int i) {
                        String imageUrl = slideModels.get(i).getImageUrl();
                        bigImage(imageUrl);
                    }
                });
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }






    }
    @Override
    public int getItemCount() {
        return list_post.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView post_username,post_context,post_title,post_datetime,post_id,insertReply;
        CircleImageView user_image , btn_setAndDelete;
        ImageSlider post_img;


        public ViewHolder(View v){
            super(v);
            post_id = (TextView) v.findViewById(R.id.post_id);
            post_username = (TextView) v.findViewById(R.id.post_username);
            post_title = (TextView) v.findViewById(R.id.post_title);
            post_context = (TextView) v.findViewById(R.id.post_context);
            post_datetime = (TextView) v.findViewById(R.id.post_datetime);
            insertReply = (TextView) v.findViewById(R.id.insertReply);
            user_image = (CircleImageView) v.findViewById(R.id.user_image);
            post_img = (ImageSlider) v.findViewById(R.id.post_img);
            btn_setAndDelete = (CircleImageView) v.findViewById(R.id.btn_setAndDelete);


            insertReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(context,R.anim.click_style);
                    insertReply.startAnimation(animation);
                    sessionManager = new SessionManager(context);
                    HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
                    userID = sessionUserData.get(sessionManager.USERID);
                    Intent intent = new Intent(context,ReplyActivity.class);
                    intent.putExtra("p_id",(String)view.getTag());
                    intent.putExtra("m_id",userID);
                    context.startActivity(intent);
                }
            });

        }

    }

    private void deleteDialog(String p_id,int position){
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        Button btn_check = dialog.findViewById(R.id.btn_check);
        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost(p_id , position);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void deletePost(String p_id , int position){
        Map<String, String> map = new HashMap<String, String>();
        map.put("p_id", p_id);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(context);
        URLDELETEPOST = urlSetting.getUrl()+"post/delete";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLDELETEPOST,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if(success.equals("1")) {
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

}
