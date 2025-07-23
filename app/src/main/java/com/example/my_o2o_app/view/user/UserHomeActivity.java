// UserHomeActivity.java
// 사용자 홈 화면에서 상위 카테고리만 Grid로 출력하는 화면

package com.example.my_o2o_app.view.user;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.viewmodel.CategoryViewModel;
import com.example.my_o2o_app.adapter.CategoryAdapter;
import com.example.my_o2o_app.model.Category;

import java.util.ArrayList;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // ✅ 사용자 이름 표시
        String userName = getIntent().getStringExtra("userName");
        TextView tvUserName = findViewById(R.id.tvUserName);
        if (userName != null) {
            tvUserName.setText(userName + "님");
        }

        // ✅ RecyclerView 설정 (GridLayout 4열)
        recyclerView = findViewById(R.id.recyclerCategory);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); // 4열 그리드

        // ✅ 어댑터 연결
        adapter = new CategoryAdapter();
        recyclerView.setAdapter(adapter);

        // ✅ ViewModel 연결
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // ✅ LiveData 관찰: 상위 카테고리만 필터링해서 어댑터에 전달
        categoryViewModel.getCategories().observe(this, categoryList -> {
            List<Category> topLevel = new ArrayList<>();
            for (Category c : categoryList) {
                if (c.getParent_id() == null) {
                    topLevel.add(c);
                }
            }
            adapter.setItems(topLevel);
        });

        // ✅ 카테고리 데이터 불러오기
        categoryViewModel.loadCategories();
    }
}
