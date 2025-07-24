// 전문가 필터용 카테고리 선택 BottomSheetDialogFragment

package com.example.my_o2o_app.view.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.Category;
import com.example.my_o2o_app.adapter.TopCategoryAdapter;
import com.example.my_o2o_app.adapter.SubCategoryAdapter;
import com.example.my_o2o_app.repository.CategoryRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategorySelectBottomSheetDialog extends BottomSheetDialogFragment {

    public interface OnCategorySelectedListener {
        void onSelected(Category selectedCategory);
    }

    private OnCategorySelectedListener listener;
    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    private List<Category> allCategories;
    private TopCategoryAdapter topAdapter;
    private SubCategoryAdapter subAdapter;
    private CategoryRepository repository = new CategoryRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_category_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerTop = view.findViewById(R.id.recyclerTopCategory);
        RecyclerView recyclerSub = view.findViewById(R.id.recyclerSubCategory);

        topAdapter = new TopCategoryAdapter(topCategory -> {
            // "전체" 선택 시 하위 없이 바로 선택 종료
            if (topCategory.getCategory_id() == 0) {
                if (listener != null) listener.onSelected(topCategory);
                dismiss();
                return;
            }
            updateSubCategories(topCategory.getCategory_id());
        });

        recyclerTop.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerTop.setAdapter(topAdapter);

        subAdapter = new SubCategoryAdapter();
        recyclerSub.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSub.setAdapter(subAdapter);

        subAdapter.setOnSubCategoryClickListener(category -> {
            if (listener != null) {
                listener.onSelected(category);
            }
            dismiss();
        });

        // 전체 카테고리 불러오기
        repository.fetchCategories(result -> {
            this.allCategories = result;

            // "전체" 항목 추가 (가짜 카테고리)
            List<Category> topList = new ArrayList<>();
            topList.add(new Category(0, null, "서비스 전체"));  // 가짜 상위 카테고리
            topList.addAll(
                    result.stream()
                            .filter(c -> c.getParent_id() == null)
                            .collect(Collectors.toList())
            );

            topAdapter.setItems(topList);

            // 기본 선택: 첫 번째 실제 상위 카테고리의 하위 항목 표시
            if (topList.size() > 1) {
                updateSubCategories(topList.get(1).getCategory_id());
            }
        });
    }

    private void updateSubCategories(int topCategoryId) {
        List<Category> subList = allCategories.stream()
                .filter(c -> c.getParent_id() != null && c.getParent_id() == topCategoryId)
                .collect(Collectors.toList());
        subAdapter.setItems(subList);
    }
}
