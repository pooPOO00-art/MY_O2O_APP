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
import com.example.my_o2o_app.view.common.RegionSelectBottomSheetDialog;
import com.example.my_o2o_app.viewmodel.ExpertListViewModel;

public class FindExpertFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvExperts;
    private ExpertAdapter expertAdapter;
    private ExpertListViewModel viewModel;

    private Button btnCategoryFilter;
    private Button btnLocationFilter;

    private Integer selectedCategoryId = null;
    private Integer selectedDistrictId = -1;  // 전국 전체 기본값
    private Integer selectedRegionId = 18;    // 전국 전체 regionId

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_find_expert, container, false);

        // ✅ View 초기화
        etSearch = view.findViewById(R.id.etExpertSearch);
        rvExperts = view.findViewById(R.id.rvExperts);
        btnCategoryFilter = view.findViewById(R.id.btnCategoryFilter);
        btnLocationFilter = view.findViewById(R.id.btnLocationFilter);

        rvExperts.setLayoutManager(new LinearLayoutManager(getContext()));
        expertAdapter = new ExpertAdapter();
        rvExperts.setAdapter(expertAdapter);

        // ✅ ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(ExpertListViewModel.class);
        viewModel.getExpertList().observe(getViewLifecycleOwner(), experts -> {
            Log.d("FindExpertFragment", "불러온 전문가 수: " + experts.size());
            expertAdapter.submitList(experts);
        });

        // ✅ 초기 전체 전문가 로드
        viewModel.loadExperts();
        viewModel.loadDistrictList();

        // ✅ 카테고리 필터
        btnCategoryFilter.setOnClickListener(v -> {
            CategorySelectBottomSheetDialog dialog = new CategorySelectBottomSheetDialog();
            dialog.setOnCategorySelectedListener(category -> {
                selectedCategoryId = category.getCategory_id();

                if (selectedCategoryId == 0) {
                    btnCategoryFilter.setText("서비스 전체");
                    selectedCategoryId = null;
                } else {
                    btnCategoryFilter.setText(category.getCategory_name());
                }

                applyExpertFilter();
            });
            dialog.show(getChildFragmentManager(), "category_select");
        });

        // ✅ 지역 필터 (공용 RegionSelectBottomSheetDialog)
        btnLocationFilter.setOnClickListener(v -> {
            RegionSelectBottomSheetDialog dialog = new RegionSelectBottomSheetDialog();
            dialog.setOnRegionSelectedListener((regionId, districtId, regionName, districtName) -> {
                selectedRegionId = regionId;
                selectedDistrictId = districtId;

                // 버튼 라벨
                if ("전체".equals(regionName) && "전체".equals(districtName)) {
                    btnLocationFilter.setText("지역 전체");
                } else if ("전체".equals(districtName)) {
                    btnLocationFilter.setText(regionName + " 전체");
                } else {
                    btnLocationFilter.setText(districtName);
                }

                applyExpertFilter();
            });
            dialog.show(getChildFragmentManager(), "region_select");
        });

        return view;
    }

    /** 선택된 필터를 적용하여 전문가 목록 로드 */
    private void applyExpertFilter() {
        String keyword = etSearch.getText().toString().trim();

        Log.d("ExpertFilter", "필터 적용: categoryId=" + selectedCategoryId +
                ", districtId=" + selectedDistrictId +
                ", regionId=" + selectedRegionId +
                ", keyword=" + keyword);

        viewModel.loadExpertsByFilter(selectedCategoryId, selectedDistrictId, selectedRegionId, keyword);
    }
}
