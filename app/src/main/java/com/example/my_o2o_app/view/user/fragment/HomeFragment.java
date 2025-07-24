// HomeFragment.java
// 홈 탭에서 상위 카테고리만 출력하는 Fragment

package com.example.my_o2o_app.view.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.CategoryAdapter;
import com.example.my_o2o_app.model.Category;
import com.example.my_o2o_app.view.user.CategoryDetailActivity;
import com.example.my_o2o_app.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private CategoryViewModel categoryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // RecyclerView 설정
        recyclerView = view.findViewById(R.id.recyclerCategory);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new CategoryAdapter();
        recyclerView.setAdapter(adapter);

        // 카테고리 클릭 시 하위 카테고리 화면으로 이동
        adapter.setOnCategoryClickListener(category -> {
            Intent intent = new Intent(getActivity(), CategoryDetailActivity.class);
            intent.putExtra("category_id", category.getCategory_id());
            startActivity(intent);
        });

        // ViewModel 연결
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categoryList -> {
            List<Category> topLevel = new ArrayList<>();
            for (Category c : categoryList) {
                if (c.getParent_id() == null) {
                    topLevel.add(c);
                }
            }
            adapter.setItems(topLevel);
        });

        categoryViewModel.loadCategories();

        return view;
    }
}
