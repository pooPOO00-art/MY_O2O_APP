// TopCategoryAdapter.java
// 상위 카테고리 목록을 가로로 출력하고, 클릭 시 강조 표시하는 어댑터 클래스

package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.Category;

import java.util.ArrayList;
import java.util.List;

public class TopCategoryAdapter extends RecyclerView.Adapter<TopCategoryAdapter.TopCategoryViewHolder> {

    private List<Category> categoryList = new ArrayList<>();
    private int selectedPosition = -1; // 현재 선택된 항목
    private OnTopCategoryClickListener listener; // 클릭 이벤트 리스너

    // 🔹 상위 카테고리 클릭 시 전달할 인터페이스
    public interface OnTopCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // ✅ 기본 생성자 (XML 미리보기/경고 제거용)
    public TopCategoryAdapter() {
        this.listener = null;
    }

    // ✅ 기존 생성자 (람다/리스너를 받는 경우)
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

        // 🔹 항목 클릭 시 동작
        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // 이전 항목과 새 항목 상태 갱신
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);

            // 리스너에 선택된 카테고리 전달
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // 🔹 RecyclerView에 표시할 데이터 갱신
    public void setItems(List<Category> list) {
        this.categoryList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    // =======================
    // 🔹 ViewHolder 내부 클래스
    // =======================
    public static class TopCategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        public TopCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivTopCategoryIcon);
            textView = itemView.findViewById(R.id.tvTopCategoryName);
        }

        // 🔹 데이터 바인딩
        public void bind(Category category, boolean isSelected) {
            textView.setText(category.getCategory_name());

            // 아이콘 설정
            int iconResId = getIconResId(category.getCategory_name());
            imageView.setImageResource(iconResId);

            // 🔹 selector 방식으로 선택 상태만 적용
            imageView.setSelected(isSelected);

            // 🔹 아이콘 색상만 변경
            if (isSelected) {
                imageView.setColorFilter(Color.WHITE); // 아이콘 흰색
            } else {
                imageView.clearColorFilter(); // 원래 아이콘 색상
            }

            // 🔹 글씨 굵기는 그대로 두고 필요하면 Bold만
            textView.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);
        }



        // 🔹 카테고리명에 따른 아이콘 매핑
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
