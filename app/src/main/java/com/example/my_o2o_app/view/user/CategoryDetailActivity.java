// CategoryDetailActivity.java
// 선택된 상위 카테고리를 기반으로 하위 카테고리 목록을 출력하고
// 세부 카테고리 선택 시 견적 요청 화면으로 이동하는 액티비티

package com.example.my_o2o_app.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.TopCategoryAdapter;
import com.example.my_o2o_app.adapter.SubCategoryAdapter;
import com.example.my_o2o_app.model.Category;
import com.example.my_o2o_app.viewmodel.CategoryDetailViewModel;
import com.example.my_o2o_app.view.estimate.EstimateRequestActivity;

public class CategoryDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerTopCategory;   // 상위 카테고리 목록
    private RecyclerView recyclerSubCategory;   // 하위 카테고리 목록
    private TopCategoryAdapter topAdapter;      // 상위 카테고리 어댑터
    private SubCategoryAdapter subAdapter;      // 하위 카테고리 어댑터
    private CategoryDetailViewModel viewModel;  // 카테고리 관리용 ViewModel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        // 🔹 Intent로 전달받은 초기 카테고리 ID
        int categoryId = getIntent().getIntExtra("category_id", -1);
        Log.d("CategoryDetail", "받은 category_id: " + categoryId);

        // 🔹 뒤로가기 버튼
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // ============================
        // 1️⃣ 상위 카테고리 RecyclerView 설정
        // ============================
        recyclerTopCategory = findViewById(R.id.recyclerTopCategory);
        recyclerTopCategory.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        );

        // 람다식에 Category 타입 명시 → getCategory_id() 사용 가능
        topAdapter = new TopCategoryAdapter((Category category) -> {
            // 상위 카테고리 클릭 시 선택된 카테고리 ID로 하위 카테고리 갱신
            viewModel.selectTopCategory(category.getCategory_id());
        });
        recyclerTopCategory.setAdapter(topAdapter);

        // ============================
        // 2️⃣ 하위 카테고리 RecyclerView 설정
        // ============================
        recyclerSubCategory = findViewById(R.id.recyclerSubCategory);
        recyclerSubCategory.setLayoutManager(new LinearLayoutManager(this));

        // 하위 카테고리 클릭 시 견적 요청 화면으로 이동
        subAdapter = new SubCategoryAdapter((Category category) -> {
            Intent intent = new Intent(CategoryDetailActivity.this, EstimateRequestActivity.class);
            // 선택한 세부 카테고리 정보 전달
            intent.putExtra("categoryId", category.getCategory_id());
            intent.putExtra("categoryName", category.getCategory_name());
            startActivity(intent);
        });
        recyclerSubCategory.setAdapter(subAdapter);

        // ============================
        // 3️⃣ ViewModel 초기화 및 LiveData 관찰
        // ============================
        viewModel = new ViewModelProvider(this).get(CategoryDetailViewModel.class);

        // 상위 카테고리 목록 관찰 → 어댑터 갱신
        viewModel.getTopCategories().observe(this, topAdapter::setItems);

        // 선택된 상위 카테고리에 따른 하위 카테고리 목록 관찰 → 어댑터 갱신
        viewModel.getSubCategories().observe(this, subAdapter::setItems);

        // 초기 카테고리 ID로 데이터 로드 (없으면 -1)
        int initialCategoryId = getIntent().getIntExtra("category_id", -1);
        viewModel.loadCategories(initialCategoryId);
    }
}
