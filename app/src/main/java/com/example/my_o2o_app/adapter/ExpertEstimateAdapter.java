// ExpertEstimateAdapter.java
// 전문가 견적(받은 견적) 목록 RecyclerView 어댑터
// - 업체명, 리뷰(⭐평점+리뷰수), 설명(message), 가격, 프로필 이미지 표시

package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.ExpertEstimate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpertEstimateAdapter extends RecyclerView.Adapter<ExpertEstimateAdapter.ViewHolder> {

    private List<ExpertEstimate> expertList = new ArrayList<>();
    private final OnItemClickListener listener;

    // 아이템 클릭 인터페이스
    public interface OnItemClickListener {
        void onItemClick(ExpertEstimate expert);
    }

    public ExpertEstimateAdapter(List<ExpertEstimate> list, OnItemClickListener listener) {
        this.expertList = (list != null) ? list : new ArrayList<>();
        this.listener = listener;
    }

    // 데이터 갱신
    public void updateData(List<ExpertEstimate> newList) {
        expertList = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expert_estimate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpertEstimate expert = expertList.get(position);

        // 1️⃣ 업체명
        holder.tvCompanyName.setText(expert.getCompanyName());

        // 2️⃣ 리뷰 표시 (⭐ 평점 (리뷰수))
        holder.tvReview.setText("⭐ " + expert.getRating() + " (" + expert.getReviewCount() + ")");

        // 3️⃣ 설명 메시지
        holder.tvMessage.setText(expert.getMessage());

        // 4️⃣ 가격 (천 단위 콤마 + ₩)
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
        holder.tvPrice.setText("₩" + numberFormat.format(expert.getPrice()));

        // 5️⃣ 프로필 이미지 Glide 로드
        if (expert.getProfileImage() != null && !expert.getProfileImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(expert.getProfileImage())
                    .placeholder(R.drawable.ic_placeholder) // 기본 이미지
                    .centerCrop()
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_placeholder);
        }

        // 6️⃣ 아이템 클릭 이벤트
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(expert);
        });
    }

    @Override
    public int getItemCount() {
        return expertList.size();
    }

    // ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompanyName, tvPrice, tvMessage, tvReview;
        ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvReview = itemView.findViewById(R.id.tvReview); // ✅ 리뷰 표시
            ivProfile = itemView.findViewById(R.id.ivProfile);
        }
    }
}
