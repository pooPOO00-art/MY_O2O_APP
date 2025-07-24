// DistrictAdapter.java
// 시군구(District) 목록을 RecyclerView에 표시하는 어댑터 클래스
package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.model.District;

import java.util.List;

public class DistrictAdapter extends RecyclerView.Adapter<DistrictAdapter.DistrictViewHolder> {

    private List<District> districtList;
    private final OnDistrictClickListener listener;

    public interface OnDistrictClickListener {
        void onDistrictClick(District district);
    }

    public DistrictAdapter(List<District> districtList, OnDistrictClickListener listener) {
        this.districtList = districtList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DistrictViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new DistrictViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistrictViewHolder holder, int position) {
        District district = districtList.get(position);
        TextView textView = (TextView) holder.itemView;
        textView.setText(district.getDistrictName());

        textView.setOnClickListener(v -> listener.onDistrictClick(district));
    }

    @Override
    public int getItemCount() {
        return districtList.size();
    }

    // 🔄 LiveData로 전달된 리스트 갱신용 메서드
    public void updateList(List<District> newList) {
        this.districtList = newList;
        notifyDataSetChanged();
    }

    static class DistrictViewHolder extends RecyclerView.ViewHolder {
        public DistrictViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
