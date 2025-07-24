// RegionAdapter.java
// 도(Region) 목록을 RecyclerView에 표시하는 어댑터 클래스
package com.example.my_o2o_app.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.model.Region;

import java.util.List;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.RegionViewHolder> {

    private List<Region> regionList;
    private int selectedPosition = -1; // 선택된 항목 인덱스
    private final OnRegionClickListener listener;

    public interface OnRegionClickListener {
        void onRegionClick(Region region);
    }

    public RegionAdapter(List<Region> regionList, OnRegionClickListener listener) {
        this.regionList = regionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RegionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new RegionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegionViewHolder holder, int position) {
        Region region = regionList.get(position);
        TextView textView = (TextView) holder.itemView;
        textView.setText(region.getRegionName());

        textView.setTextColor(selectedPosition == position ? Color.BLUE : Color.BLACK);

        textView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                selectedPosition = pos;
                notifyDataSetChanged();
                listener.onRegionClick(regionList.get(pos));
            }
        });
    }


    @Override
    public int getItemCount() {
        return regionList.size();
    }

    // 🔄 LiveData로 전달된 리스트 갱신용 메서드
    public void updateList(List<Region> newList) {
        this.regionList = newList;
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    static class RegionViewHolder extends RecyclerView.ViewHolder {
        public RegionViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
