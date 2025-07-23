// CategoryAdapter.java
// 카테고리 목록을 RecyclerView에 표시하는 어댑터 클래스

package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView; // ⬅️ 이거 꼭 추가!

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList = new ArrayList<>();

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setItems(List<Category> categories) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvCategoryName);
            imageView = itemView.findViewById(R.id.ivCategoryIcon);
        }

        public void bind(Category category) {
            textView.setText(category.getCategory_name());

            // 아이콘 매핑
            int iconResId = getIconResId(category.getCategory_name());
            imageView.setImageResource(iconResId);
        }

        private int getIconResId(String categoryName) {
            switch (categoryName) {
                case "이사":
                    return R.drawable.ic_moving;
                case "청소":
                    return R.drawable.ic_cleaning;
                case "수리/설치":
                    return R.drawable.ic_repair;
                case "레슨":
                    return R.drawable.ic_lesson;
                default:
                    return R.drawable.ic_placeholder;
            }
        }
    }
}
