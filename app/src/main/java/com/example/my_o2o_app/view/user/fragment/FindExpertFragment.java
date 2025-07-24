// 고수찾기 탭에서 검색 + 카테고리 필터 + 전문가 리스트 출력

package com.example.my_o2o_app.view.user.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.ExpertAdapter;
import com.example.my_o2o_app.view.common.CategorySelectBottomSheetDialog;
import com.example.my_o2o_app.viewmodel.ExpertListViewModel;
import com.example.my_o2o_app.model.Category;

public class FindExpertFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvExperts;
    private ExpertAdapter expertAdapter;
    private ExpertListViewModel viewModel;

    private Integer selectedCategoryId = null;  // 선택된 카테고리 ID 저장용

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_find_expert, container, false);

        etSearch = view.findViewById(R.id.etExpertSearch);
        rvExperts = view.findViewById(R.id.rvExperts);
        Button btnCategoryFilter = view.findViewById(R.id.btnCategoryFilter);

        rvExperts.setLayoutManager(new LinearLayoutManager(getContext()));
        expertAdapter = new ExpertAdapter();
        rvExperts.setAdapter(expertAdapter);

        viewModel = new ViewModelProvider(this).get(ExpertListViewModel.class);
        viewModel.getExpertList().observe(getViewLifecycleOwner(), experts -> {
            Log.d("FindExpertFragment", "불러온 전문가 수: " + experts.size());
            expertAdapter.submitList(experts);
        });

        // ✅ 초기 전체 전문가 로딩
        viewModel.loadExperts();

        // ✅ 카테고리 필터 버튼 클릭 시 BottomSheet 열기
        btnCategoryFilter.setOnClickListener(v -> {
            CategorySelectBottomSheetDialog dialog = new CategorySelectBottomSheetDialog();
            dialog.setOnCategorySelectedListener(category -> {
                selectedCategoryId = category.getCategory_id();

                // "전체" 선택 시 전체 로드
                if (selectedCategoryId == 0) {
                    btnCategoryFilter.setText("서비스 전체");
                    viewModel.loadExperts();
                } else {
                    btnCategoryFilter.setText(category.getCategory_name());
                    viewModel.loadExpertsByFilter(selectedCategoryId, null, null);
                }
            });
            dialog.show(getChildFragmentManager(), "category_select");
        });

        return view;
    }
}
