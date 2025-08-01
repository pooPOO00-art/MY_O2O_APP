// EstimateAdapter.java
// 받은 견적 목록 RecyclerView Adapter (3단계 구조용)
// - 서비스명/지역명 + 남은시간 + 받은견적 수 + 상태 표시 + 카테고리 아이콘
// - 아이템 클릭 시 상세 화면 이동 (콜백 방식)
// - 세부 카테고리명도 대카테고리 아이콘으로 매칭 (부분 문자열 매칭 방식)

package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.EstimateRequest;

import java.util.ArrayList;
import java.util.List;

public class EstimateAdapter extends RecyclerView.Adapter<EstimateAdapter.ViewHolder> {

    private List<EstimateRequest> estimateList = new ArrayList<>();
    private final OnItemClickListener listener;

    /** 클릭 이벤트를 Fragment/Activity로 전달하는 인터페이스 */
    public interface OnItemClickListener {
        void onItemClick(EstimateRequest estimate);
    }

    public EstimateAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    /** 데이터 갱신 */
    public void updateData(List<EstimateRequest> newList) {
        this.estimateList = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_estimate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EstimateRequest item = estimateList.get(position);

        // 1️⃣ 서비스명 + 지역명
        String categoryName = (item.getCategoryName() != null) ? item.getCategoryName().trim() : "서비스";
        String districtName = (item.getDistrictName() != null) ? item.getDistrictName().trim() : "지역";

        holder.tvCategoryAndRegion.setText(categoryName + " | " + districtName);

        // 2️⃣ 카테고리 아이콘 (부분 문자열 매칭)
        holder.ivCategoryIcon.setImageResource(getIconResId(categoryName));

        // 3️⃣ 남은시간 + 받은 견적 수 (서버에서 hoursLeft와 receivedCount 제공)
        String remainTime = (item.getHoursLeft() <= 0) ? "만료"
                : item.getHoursLeft() + "시간";
        holder.tvEstimateInfo.setText(
                "남은 시간 " + remainTime +
                        " | 받은 견적 " + item.getReceivedCount() + "건"
        );

        // 4️⃣ 상태 표시
        holder.tvStatus.setText(item.getStatus());

        // 5️⃣ 아이템 클릭 시 상세 화면으로 전달
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return estimateList.size();
    }

    /** 카테고리 이름 → 아이콘 매핑 (부분 문자열 매칭) */
    private int getIconResId(String categoryName) {
        if (categoryName == null) return R.drawable.ic_placeholder;
        categoryName = categoryName.trim();

        if (categoryName.contains("청소")) return R.drawable.ic_cleaning;
        if (categoryName.contains("이사")) return R.drawable.ic_moving;
        if (categoryName.contains("수리") || categoryName.contains("설치")) return R.drawable.ic_repair;
        if (categoryName.contains("레슨") || categoryName.contains("과외")) return R.drawable.ic_lesson;
        if (categoryName.contains("개인")) return R.drawable.ic_person;

        return R.drawable.ic_placeholder; // 기본 아이콘
    }

    /** ViewHolder: item_estimate.xml에 맞춤 */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryAndRegion, tvEstimateInfo, tvStatus;
        ImageView ivCategoryIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryAndRegion = itemView.findViewById(R.id.tvCategoryAndRegion);
            tvEstimateInfo = itemView.findViewById(R.id.tvEstimateInfo);
            tvStatus = itemView.findViewById(R.id.tvEstimateStatus);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
        }
    }
}
