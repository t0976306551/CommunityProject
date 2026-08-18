package com.example.communityproject.LoginAndRegister;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityproject.Post.PostActivity;
import com.example.communityproject.Post.insert_post_Activity;
import com.example.communityproject.R;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder>  {
    private List<CommunityData> list_data;
    Context context;

    public CommunityAdapter(Context context, List<CommunityData> list_data){
        this.list_data = list_data;
        this.context = context;
        notifyDataSetChanged();
    }

    public void setData(List<CommunityData> list_data){
        this.list_data = list_data;
    }
    public void removeData(int position) {
        list_data.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_community_item , parent, false);
        return new CommunityAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.ViewHolder holder, int position) {
        CommunityData communityData = list_data.get(position);
        holder.communityName.setText(communityData.getName());
        holder.communityCardView.setTag(communityData.getC_id());
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    public void setFilter(ArrayList<CommunityData> newList){
        list_data = new ArrayList<>();
        list_data.addAll(newList);
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
                    Intent intent = new Intent(context, RegisterActivity.class);
                    intent.putExtra("c_id",itemView.getTag().toString());
                    intent.putExtra("c_name",communityName.getText().toString());
                    context.startActivity(intent);
                    ((SelectCommunityActivity)context).finish();
                }
            });
        }
    }
}
