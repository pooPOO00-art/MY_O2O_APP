// RegionSelectBottomSheetDialog.java
// 전문가 필터용 지역 선택 BottomSheetDialogFragment
// - 도(Region) → 시군구(District) 2단계 구조
// - 선택된 결과는 콜백으로 상위 Fragment/Activity에 전달

package com.example.my_o2o_app.view.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.RegionAdapter;
import com.example.my_o2o_app.adapter.DistrictAdapter;
import com.example.my_o2o_app.model.Region;
import com.example.my_o2o_app.model.District;
import com.example.my_o2o_app.viewmodel.RegionFilterViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RegionSelectBottomSheetDialog extends BottomSheetDialogFragment {

    // 지역 선택 결과를 상위 화면으로 전달할 인터페이스
    public interface OnRegionSelectedListener {
        void onSelected(String regionName, String districtName);
    }

    private OnRegionSelectedListener listener;
    public void setOnRegionSelectedListener(OnRegionSelectedListener listener) {
        this.listener = listener;
    }

    // 뷰모델 및 어댑터
    private RegionFilterViewModel viewModel;
    private RegionAdapter regionAdapter;
    private DistrictAdapter districtAdapter;
    private Region selectedRegion; // 선택된 도 객체

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // XML 레이아웃 inflate
        return inflater.inflate(R.layout.dialog_region_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerRegion = view.findViewById(R.id.recyclerRegion);
        RecyclerView recyclerDistrict = view.findViewById(R.id.recyclerDistrict);

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(RegionFilterViewModel.class);

        // 도(Region) 어댑터 초기화 및 클릭 시 시군구 로딩
        regionAdapter = new RegionAdapter(new ArrayList<>(), region -> {
            selectedRegion = region;
            viewModel.loadDistricts(region.getRegionId()); // 선택된 도에 따른 시군구 요청
        });

        // 시군구(District) 어댑터 클릭 시 결과 콜백 전달
        districtAdapter = new DistrictAdapter(new ArrayList<>(), district -> {
            if (listener != null && selectedRegion != null) {
                listener.onSelected(selectedRegion.getRegionName(), district.getDistrictName());
            }
            dismiss(); // BottomSheet 닫기
        });

        // RecyclerView 연결
        recyclerRegion.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerDistrict.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRegion.setAdapter(regionAdapter);
        recyclerDistrict.setAdapter(districtAdapter);

        // LiveData 관찰 → 데이터 변경 시 자동 UI 갱신
        viewModel.getRegionList().observe(getViewLifecycleOwner(), regionAdapter::updateList);
        viewModel.getDistrictList().observe(getViewLifecycleOwner(), districtAdapter::updateList);

        // 초기 도 목록 로딩
        viewModel.loadRegions();
    }
}
