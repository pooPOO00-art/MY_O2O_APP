// CategoryDetailActivity.java
// 선택된 상위 카테고리를 기반으로 하위 카테고리 목록을 출력하는 액티비티

package com.example.my_o2o_app.view.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.TopCategoryAdapter;
import com.example.my_o2o_app.adapter.SubCategoryAdapter;
import com.example.my_o2o_app.model.Category;
import com.example.my_o2o_app.viewmodel.CategoryDetailViewModel;

import java.util.List;

public class CategoryDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerTopCategory;
    private RecyclerView recyclerSubCategory;
    private TopCategoryAdapter topAdapter;
    private SubCategoryAdapter subAdapter;
    private CategoryDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        int categoryId = getIntent().getIntExtra("category_id", -1);
        Log.d("CategoryDetail", "받은 category_id: " + categoryId);

        // ⬅️ 뒤로가기 버튼
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 🔧 RecyclerView 연결
        recyclerTopCategory = findViewById(R.id.recyclerTopCategory);
        recyclerTopCategory.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        topAdapter = new TopCategoryAdapter(category -> {
            // 선택된 상위 카테고리 ID를 ViewModel에 전달
            viewModel.selectTopCategory(category.getCategory_id());
        });
        recyclerTopCategory.setAdapter(topAdapter);

        recyclerSubCategory = findViewById(R.id.recyclerSubCategory);
        recyclerSubCategory.setLayoutManager(new LinearLayoutManager(this));
        subAdapter = new SubCategoryAdapter();  // 클릭 이벤트는 이후 추가
        recyclerSubCategory.setAdapter(subAdapter);

        // ✅ ViewModel 연결
        viewModel = new ViewModelProvider(this).get(CategoryDetailViewModel.class);

        // 🔁 상위 카테고리 전체 관찰
        viewModel.getTopCategories().observe(this, topAdapter::setItems);

        // 🔁 선택된 상위 카테고리의 하위 카테고리만 출력
        viewModel.getSubCategories().observe(this, subAdapter::setItems);

        // 🔽 intent로 전달받은 초기 선택 카테고리 ID
        int initialCategoryId = getIntent().getIntExtra("category_id", -1);
        viewModel.loadCategories(initialCategoryId);  // 전체 목록 불러오고 선택 상태 설정
    }
}
