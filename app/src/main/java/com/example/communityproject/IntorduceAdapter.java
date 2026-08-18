package com.example.communityproject;

import android.annotation.SuppressLint;
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
import android.widget.ImageButton;
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
import com.example.communityproject.Acyivity.RecordCardViewData;
import com.example.communityproject.Attraction.ImageAdapter;
import com.example.communityproject.Attraction.ImageData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class IntorduceAdapter  extends RecyclerView.Adapter<IntorduceAdapter.ViewHolder> {
    private List<IntorduceCardViewData> list_data;
    private Context context;

    public IntorduceAdapter(Context context, List<IntorduceCardViewData> list_data){
        this.list_data = list_data;
        this.context = context;
        notifyDataSetChanged();
    }

    public void setData(List<IntorduceCardViewData> list_staple){
        this.list_data= list_staple;
    }

    public void removeData(int position) {
        list_data.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @NonNull

    public IntorduceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.intorduce_messager_item, parent, false);
        return new IntorduceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  IntorduceAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        IntorduceCardViewData intorduceCardViewData = list_data.get(position);
        holder.user_cardView.setTag(intorduceCardViewData.getM_id());
        holder.user_name.setText(intorduceCardViewData.getM_name());
        holder.user_authority.setText(intorduceCardViewData.getA_name());
        if(!intorduceCardViewData.getM_image().equals("")){
            Glide.with(context).load(intorduceCardViewData.getM_image()).into(holder.user_image);
        }else{
            holder.user_image.setImageResource(R.drawable.user_preset);
        }

    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_image;
        TextView user_name , user_authority;
        CardView user_cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_cardView = itemView.findViewById(R.id.user_cardView);
            user_image = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.user_name);
            user_authority = itemView.findViewById(R.id.user_authority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    (String)view.getTag();
                }
            });

        }
    }



}
