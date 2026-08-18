package com.example.communityproject.Staple;

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

public class StapleAdapter extends RecyclerView.Adapter<StapleAdapter.ViewHolder>{
    private List<StapleCardViewData> list_staple;
    private Context context;
    SessionManager sessionManager;
    String m_id,s_id;
    UrlSetting urlSetting;
    private String URL_DELETE;
    public StapleAdapter(Context context, List<StapleCardViewData> list_staple){
        this.list_staple = list_staple;
        this.context = context;
    }
    public void setData(List<StapleCardViewData> list_staple){
        this.list_staple = list_staple;

    }


    public void removeData(int position) {
        list_staple.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }


    public StapleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_stapleitem, parent, false);
        return new StapleAdapter.ViewHolder(view);
    }
    @NonNull
    @Override
    public void onBindViewHolder(@NonNull StapleAdapter.ViewHolder holder, int position) {
        StapleCardViewData stapleCardViewData = list_staple.get(position);
        holder.staple_img_name.setText(stapleCardViewData.getName());
        holder.stapleCardView.setTag(stapleCardViewData.getId());
        holder.btn_delete.setTag(stapleCardViewData.getId());
        holder.gotoPage.setTag(stapleCardViewData.getId());
        sessionManager = new SessionManager(context);
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        m_id = sessionUserData.get(sessionManager.A_ID);
        if(m_id.equals("3")){
            holder.btn_delete.setVisibility(View.GONE);
        }else{
            holder.btn_delete.setVisibility(View.VISIBLE);
        }
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_id = (String)view.getTag();
                deleteDialog(s_id,position);
            }
        });

        holder.gotoPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_id = (String)view.getTag();
                Intent intent = new Intent(context,staple_page_Activity.class);
                intent.putExtra("s_id",s_id);
                context.startActivity(intent);
            }
        });

        List<SlideModel> slideModels = new ArrayList<>();
        try {
            JSONArray image_array = new JSONArray();
            image_array = stapleCardViewData.getImage();
            for(int i = 0;i<image_array.length();i++){
                slideModels.add(new SlideModel(String.valueOf(image_array.get(i)), null));
            }
            holder.staple_img.setImageList(slideModels,true);
            holder.staple_img.setItemClickListener(new ItemClickListener() {
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
        return list_staple.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageSlider staple_img;
        TextView staple_img_name;
        CardView stapleCardView;
        CircleImageView btn_delete,gotoPage;
        public ViewHolder(View v){
            super(v);
            staple_img = (ImageSlider) v.findViewById(R.id.staple_img);
            staple_img_name = (TextView) v.findViewById(R.id.staple_img_name);
            stapleCardView = (CardView) v.findViewById(R.id.stapleCardView);
            btn_delete = (CircleImageView) v.findViewById(R.id.btn_delete);
            gotoPage = (CircleImageView) v.findViewById(R.id.gotoPage);
        }
    }

    private void deleteDialog(String s_id,int position){
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close_imageView = dialog.findViewById(R.id.close_imageView);
        Button btn_check = dialog.findViewById(R.id.btn_check);
        TextView textView = dialog.findViewById(R.id.textView);
        TextView textView2 =  dialog.findViewById(R.id.textView2);
        textView.setText("刪除名產");
        textView2.setText("確認是否刪除名產");
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
                DeleteData(s_id,position);
                dialog.cancel();
            }
        });
    }

    private void DeleteData(String s_id , int position){
        Map<String, String> map = new HashMap<String, String>();
        map.put("s_id", s_id);
        JSONObject data = new JSONObject(map);
        urlSetting = new UrlSetting(context);
        URL_DELETE = urlSetting.getUrl()+"staple/delete";
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
