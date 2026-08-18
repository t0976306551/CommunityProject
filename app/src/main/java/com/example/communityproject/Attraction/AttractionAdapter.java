package com.example.communityproject.Attraction;

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
import android.widget.EditText;
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

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder>{
    private List<AttractionCardviewData> list_data;
    private Context context;
    SessionManager sessionManager;
    String m_id,a_id;
    private String URL_DELETE ;
    UrlSetting urlSetting;
    public AttractionAdapter(Context context, List<AttractionCardviewData> list_data){
        this.list_data = list_data;
        this.context = context;
    }
    public void setData(List<AttractionCardviewData> list_data){
        this.list_data = list_data;
    }

    public void removeData(int position) {
        list_data.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    public AttractionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_attractionitem, parent, false);
        return new AttractionAdapter.ViewHolder(view);
    }
    @NonNull
    @Override
    public void onBindViewHolder(@NonNull AttractionAdapter.ViewHolder holder, int position) {
        AttractionCardviewData attractionCardviewData = list_data.get(position);
        holder.a_name.setText(attractionCardviewData.getName());
        holder.attractionCardView.setTag(attractionCardviewData.getId());
        holder.btn_delete.setTag(attractionCardviewData.getId());
        holder.gotoPage.setTag(attractionCardviewData.getId());

        sessionManager = new SessionManager(context);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        m_id = sessionUserData.get(sessionManager.A_ID);
        if(m_id.equals("3")){
            holder.btn_delete.setVisibility(View.GONE);
        }else{
            holder.btn_delete.setVisibility(View.VISIBLE);
        }

        holder.gotoPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a_id = (String)view.getTag();
                Intent intent = new Intent(context,attraction_page_Activity.class);
                intent.putExtra("a_id",a_id);
                context.startActivity(intent);
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a_id = (String)view.getTag();
                deleteDialog(a_id,position);
            }
        });

        List<SlideModel> slideModels = new ArrayList<>();
        try {
            JSONArray image_array = new JSONArray();
            image_array = attractionCardviewData.getImage();
            for(int i = 0;i<image_array.length();i++){
                slideModels.add(new SlideModel(String.valueOf(image_array.get(i)), null));
            }
            holder.attraction_img.setImageList(slideModels,true);
            holder.attraction_img.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemSelected(int i) {
                    String imageUrl = slideModels.get(i).getImageUrl();
                    bigImage(imageUrl);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    @Override
    public int getItemCount() {
        return list_data.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView btn_delete,gotoPage;
        ImageSlider attraction_img;
        TextView a_name;
        CardView attractionCardView;
        public ViewHolder(View v){
            super(v);
            attraction_img = (ImageSlider) v.findViewById(R.id.attraction_img);
            a_name = (TextView) v.findViewById(R.id.a_name);
            attractionCardView = (CardView) v.findViewById(R.id.attractionCardView);
            btn_delete = (CircleImageView) v.findViewById(R.id.btn_delete);
            gotoPage = (CircleImageView) v.findViewById(R.id.gotoPage);

        }
    }

    private void deleteDialog(String a_id,int position){
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        Button btn_check = dialog.findViewById(R.id.btn_check);
        TextView textView = dialog.findViewById(R.id.textView);
        TextView textView2 =  dialog.findViewById(R.id.textView2);
        textView.setText("刪除景點");
        textView2.setText("確認是否刪除景點");
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
                DeleteData(a_id , position);
                dialog.cancel();
            }
        });
    }

    
    private void DeleteData(String a_id ,int position){
        Map<String, String> map = new HashMap<String, String>();
        map.put("a_id", a_id);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(context);
        URL_DELETE = urlSetting.getUrl()+"attraction/delete";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_DELETE,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    String success = response.getString("success");
                    if(success.equals("1")){
                        Toast.makeText(context, "刪除成功", Toast.LENGTH_SHORT).show();
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
