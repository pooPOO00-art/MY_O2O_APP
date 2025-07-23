// SubCategoryAdapter.java
// 선택된 상위 카테고리의 하위 카테고리 목록을 출력하는 어댑터 클래스

package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.Category;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder> {

    private List<Category> categoryList = new ArrayList<>();
    private OnSubCategoryClickListener listener;

    public interface OnSubCategoryClickListener {
        void onSubCategoryClick(Category category);
    }

    public void setOnSubCategoryClickListener(OnSubCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Category> list) {
        this.categoryList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sub_category, parent, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSubCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class SubCategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvSubCategoryName);
        }

        public void bind(Category category) {
            textView.setText(category.getCategory_name());
        }
    }
}
