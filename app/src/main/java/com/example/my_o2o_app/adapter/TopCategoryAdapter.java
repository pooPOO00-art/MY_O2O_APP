// TopCategoryAdapter.java
// 상위 카테고리 목록을 가로로 출력하고, 클릭 시 강조 표시하는 어댑터 클래스

package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.Category;

import java.util.ArrayList;
import java.util.List;

public class TopCategoryAdapter extends RecyclerView.Adapter<TopCategoryAdapter.TopCategoryViewHolder> {

    private List<Category> categoryList = new ArrayList<>();
    private int selectedPosition = -1;
    private final OnTopCategoryClickListener listener;

    // 클릭 이벤트를 전달받기 위한 인터페이스
    public interface OnTopCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public TopCategoryAdapter(OnTopCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_category, parent, false);
        return new TopCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopCategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        boolean isSelected = (position == selectedPosition);
        holder.bind(category, isSelected);

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev); // 이전 항목 un-highlight
            notifyItemChanged(selectedPosition); // 새 항목 highlight

            listener.onCategoryClick(category); // ViewModel에 알림
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setItems(List<Category> list) {
        this.categoryList = list;
        notifyDataSetChanged();
    }

    public static class TopCategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        public TopCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivTopCategoryIcon);
            textView = itemView.findViewById(R.id.tvTopCategoryName);
        }

        public void bind(Category category, boolean isSelected) {
            textView.setText(category.getCategory_name());

            // 아이콘 설정
            int iconResId = getIconResId(category.getCategory_name());
            imageView.setImageResource(iconResId);

            // 선택 강조 처리
            if (isSelected) {
                imageView.setBackgroundResource(R.drawable.category_item_selected_bg);
                textView.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                imageView.setBackgroundResource(R.drawable.category_item_bg);
                textView.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
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
                case "카테고리1":
                    return R.drawable.ic_placeholder;
                case "카테고리2":
                    return R.drawable.ic_placeholder;
                case "카테고리3":
                    return R.drawable.ic_placeholder;
                case "카테고리4":
                    return R.drawable.ic_placeholder;
                case "카테고리5":
                    return R.drawable.ic_placeholder;
                default:
                    return R.drawable.ic_placeholder;
            }
        }
    }
}
