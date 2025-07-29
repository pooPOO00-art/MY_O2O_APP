// SubCategoryAdapter.java
// 선택된 상위 카테고리의 하위 카테고리 목록을 출력하고,
// 항목 클릭 시 Listener를 통해 Activity/Fragment로 전달하는 어댑터 클래스

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

    // 🔹 표시할 하위 카테고리 목록
    private List<Category> categoryList = new ArrayList<>();

    // 🔹 클릭 이벤트 리스너
    private OnSubCategoryClickListener listener;

    /**
     * 하위 카테고리 클릭 시 이벤트를 전달받는 인터페이스
     */
    public interface OnSubCategoryClickListener {
        void onSubCategoryClick(Category category);
    }

    // ✅ 디폴트 생성자 (XML 미리보기/경고 제거용)
    public SubCategoryAdapter() {
        this.listener = null;
    }

    // ✅ 기존처럼 람다/리스너를 받을 수 있는 생성자
    public SubCategoryAdapter(OnSubCategoryClickListener listener) {
        this.listener = listener;
    }

    /**
     * 외부(Activity/Fragment)에서 클릭 리스너를 등록
     */
    public void setOnSubCategoryClickListener(OnSubCategoryClickListener listener) {
        this.listener = listener;
    }

    /**
     * RecyclerView에 표시할 데이터 갱신
     */
    public void setItems(List<Category> list) {
        this.categoryList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🔹 하위 카테고리 한 줄 레이아웃(item_sub_category.xml) inflate
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sub_category, parent, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);

        // 🔹 항목 클릭 시 Listener 호출
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

    /**
     * ViewHolder 내부 클래스
     * - item_sub_category.xml의 TextView에 카테고리 이름 바인딩
     */
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
