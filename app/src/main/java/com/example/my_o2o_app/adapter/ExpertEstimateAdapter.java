package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.ExpertEstimate;

import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class ExpertEstimateAdapter extends RecyclerView.Adapter<ExpertEstimateAdapter.ViewHolder> {

    private List<ExpertEstimate> expertList = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ExpertEstimate expert);
    }

    public ExpertEstimateAdapter(List<ExpertEstimate> list, OnItemClickListener listener) {
        this.expertList = (list != null) ? list : new ArrayList<>();
        this.listener = listener;
    }

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

        // 업체명
        holder.tvCompanyName.setText(expert.getCompanyName());

        // ✅ 견적 메시지
        holder.tvMessage.setText(expert.getMessage());

        // ✅ 금액에 천 단위 콤마 추가
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
        holder.tvPrice.setText(numberFormat.format(expert.getPrice()) + "원");

        // ✅ 리뷰 표시 (모델에 rating, reviewCount 있을 경우)
//        if (holder.tvReview != null) {
//            holder.tvReview.setText("⭐ " + expert.getRating() + " (" + expert.getReviewCount() + ")");
//        }

//        // ✅ Glide로 프로필 이미지 로드
//        if (holder.ivProfile != null) {
//            Glide.with(holder.itemView.getContext())
//                    .load(expert.getProfileImage())
//                    .placeholder(R.drawable.ic_placeholder) // 기본 이미지
//                    .circleCrop() // 원형 처리
//                    .into(holder.ivProfile);
//        }

        // ✅ 아이템 클릭 이벤트
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(expert);
        });
    }


    @Override
    public int getItemCount() {
        return expertList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompanyName, tvPrice, tvMessage;
        ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivProfile = itemView.findViewById(R.id.ivProfile); // 추후 Glide로 이미지 로드
        }
    }
}
