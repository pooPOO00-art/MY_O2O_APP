// ExpertAdapter.java
// 전문가 리스트를 RecyclerView에 바인딩하는 어댑터 (업체카드 최종버전)
// - 업체명, ⭐평점+경력+예약(한 줄), 업체 설명(2줄), 대표 서비스·지역(1줄), 프로필 이미지

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
import com.example.my_o2o_app.model.ExpertWithStats;

import java.util.ArrayList;
import java.util.List;

public class ExpertAdapter extends RecyclerView.Adapter<ExpertAdapter.ExpertViewHolder> {

    private List<ExpertWithStats> expertList = new ArrayList<>();

    /** 외부에서 리스트 갱신 */
    public void submitList(List<ExpertWithStats> list) {
        expertList = list != null ? list : new ArrayList<>();
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
        ExpertWithStats expert = expertList.get(position);

        // 1️⃣ 업체명
        holder.tvCompanyName.setText(expert.getCompanyName());

        // 2️⃣ ⭐평점 + 경력 + 예약 (한 줄)
        String careerRatingText = "⭐ " + expert.getAvgRating()
                + " (" + expert.getReviewCount() + ") · 경력 "
                + expert.getCareerYears() + "년 · 예약 "
                + expert.getReservationCount() + "회";
        holder.tvRatingCareerReservation.setText(careerRatingText);

        // 3️⃣ 업체 설명 (2줄 제한)
        String description = (expert.getDescription() != null && !expert.getDescription().isEmpty())
                ? expert.getDescription()
                : "업체 설명 없음";
        holder.tvDescription.setText(description);

        // 4️⃣ 대표 서비스·지역 (1줄)
        String serviceInfo = (expert.getServiceInfo() != null && !expert.getServiceInfo().isEmpty())
                ? expert.getServiceInfo()
                : "서비스/지역 정보 없음";
        holder.tvServiceInfo.setText(serviceInfo);

        // 5️⃣ 프로필 이미지
        if (expert.getProfileImage() != null && !expert.getProfileImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(expert.getProfileImage())
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return expertList.size();
    }

    /** ViewHolder: item_expert.xml의 모든 View 참조 */
    static class ExpertViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvCompanyName, tvRatingCareerReservation, tvDescription, tvServiceInfo;

        public ExpertViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvRatingCareerReservation = itemView.findViewById(R.id.tvRatingCareerReservation); // ⭐평점+경력+예약
            tvDescription = itemView.findViewById(R.id.tvDescription);       // 업체 설명
            tvServiceInfo = itemView.findViewById(R.id.tvServiceInfo);       // 대표 서비스/지역
        }
    }
}
