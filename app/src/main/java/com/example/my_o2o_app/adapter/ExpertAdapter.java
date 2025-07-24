// ExpertAdapter.java
// 전문가 리스트를 RecyclerView에 바인딩하는 어댑터

package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.Expert;

import java.util.ArrayList;
import java.util.List;

public class ExpertAdapter extends RecyclerView.Adapter<ExpertAdapter.ExpertViewHolder> {

    private List<Expert> expertList = new ArrayList<>();

    public void submitList(List<Expert> list) {
        expertList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expert, parent, false);
        return new ExpertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpertViewHolder holder, int position) {
        Expert expert = expertList.get(position);
        holder.tvCompanyName.setText(expert.getCompanyName());
        Log.d("ExpertAdapter", "회사명: " + expert.getCompanyName());

        // 설명은 최대 15자까지만 표시
        String desc = expert.getDescription();
        if (desc != null && desc.length() > 40) {
            desc = desc.substring(0, 15) + "...";
        }
        holder.tvDescription.setText(desc != null ? desc : "소개 정보 없음");

        // 프로필 이미지
        if (expert.getProfileImage() != null && !expert.getProfileImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(expert.getProfileImage())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(holder.ivProfileImage);
        } else {
            holder.ivProfileImage.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return expertList.size();
    }

    static class ExpertViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvCompanyName;
        TextView tvDescription;

        public ExpertViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }


}
