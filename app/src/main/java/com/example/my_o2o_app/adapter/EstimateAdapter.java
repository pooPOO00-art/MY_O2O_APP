// EstimateAdapter.java
// 받은 견적 목록 RecyclerView Adapter (3단계 구조용)
// - 서비스명/지역명 + 남은시간 + 받은견적 수 + 상태 표시
// - 아이템 클릭 시 상세 화면 이동 (콜백 방식)

package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.tvCategoryAndRegion.setText(
                (item.getCategoryName() != null ? item.getCategoryName() : "서비스") + " | " +
                        (item.getDistrictName() != null ? item.getDistrictName() : "지역")
        );

        // 2️⃣ 남은시간 + 받은 견적 수 (서버에서 hoursLeft와 receivedCount 제공)
        String remainTime = (item.getHoursLeft() <= 0) ? "만료"
                : item.getHoursLeft() + "시간";
        holder.tvEstimateInfo.setText(
                "남은 시간 " + remainTime +
                        " | 받은 견적 " + item.getReceivedCount() + "건"
        );

        // 3️⃣ 상태 표시
        holder.tvStatus.setText(item.getStatus());

        // 4️⃣ 아이템 클릭 시 상세 화면으로 전달
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return estimateList.size();
    }

    /** ViewHolder: item_estimate.xml에 맞춤 */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryAndRegion, tvEstimateInfo, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryAndRegion = itemView.findViewById(R.id.tvCategoryAndRegion);
            tvEstimateInfo = itemView.findViewById(R.id.tvEstimateInfo);
            tvStatus = itemView.findViewById(R.id.tvEstimateStatus);
        }
    }
}
