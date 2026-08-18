package com.example.communityproject.Acyivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityproject.Attraction.ImageAdapter;
import com.example.communityproject.Attraction.ImageData;
import com.example.communityproject.R;

import java.util.List;

public class ActitityImageAdapter extends RecyclerView.Adapter<ActitityImageAdapter.ViewHolder> {
    private List<ActivityImageData> list_data;
    private Context context;

    public ActitityImageAdapter(Context context, List<ActivityImageData> list_data){
        this.list_data = list_data;
        this.context = context;
        notifyDataSetChanged();
    }

    public void setData(List<ActivityImageData> list_staple){
        this.list_data= list_staple;
    }

    public void removeData(int position) {
        list_data.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActitityImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attraction_item_image, parent, false);
        return new ActitityImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActitityImageAdapter.ViewHolder holder, int position) {
        ActivityImageData activityImageData = list_data.get(position);
        holder.imageView.setImageBitmap(activityImageData.getBitmap());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeData(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageButton delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
        }
    }
}
