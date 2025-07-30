// RegionSelectBottomSheetDialog.java
// 공용 지역 선택 BottomSheetDialogFragment
// - 좌측: 도(Region) 목록 (최상단 "전국 전체" 포함)
// - 우측: 시군구(District) 목록 (첫 행 "전체" = 도 전체)
// - 선택된 결과를 콜백으로 상위 Fragment/Activity에 전달

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
import java.util.List;

public class RegionSelectBottomSheetDialog extends BottomSheetDialogFragment {

    // 선택 결과를 상위 화면으로 전달할 인터페이스
    public interface OnRegionSelectedListener {
        void onSelected(int regionId, int districtId, String regionName, String districtName);
    }

    private OnRegionSelectedListener listener;
    public void setOnRegionSelectedListener(OnRegionSelectedListener listener) {
        this.listener = listener;
    }

    // ViewModel & Adapter
    private RegionFilterViewModel viewModel;
    private RegionAdapter regionAdapter;
    private DistrictAdapter districtAdapter;
    private Region selectedRegion; // 현재 선택된 Region

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_region_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerRegion = view.findViewById(R.id.recyclerRegion);
        RecyclerView recyclerDistrict = view.findViewById(R.id.recyclerDistrict);

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(RegionFilterViewModel.class);

        // 좌측 Region 어댑터
        regionAdapter = new RegionAdapter(new ArrayList<>(), region -> {
            selectedRegion = region;

            if (region.getRegionId() == 18) {
                // 전국 전체 선택 시 바로 콜백
                if (listener != null) {
                    listener.onSelected(18, -1, "전체", "전체");
                }
                dismiss();
            } else {
                // 해당 도의 시군구 로드
                viewModel.loadDistricts(region.getRegionId());
            }
        });

        // 우측 District 어댑터
        districtAdapter = new DistrictAdapter(new ArrayList<>(), district -> {
            if (listener != null && selectedRegion != null) {
                listener.onSelected(
                        selectedRegion.getRegionId(),
                        district.getDistrictId(),
                        selectedRegion.getRegionName(),
                        district.getDistrictName()
                );
            }
            dismiss();
        });

        // RecyclerView 세팅
        recyclerRegion.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerDistrict.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRegion.setAdapter(regionAdapter);
        recyclerDistrict.setAdapter(districtAdapter);

        // 🔹 Region LiveData 관찰
        viewModel.getRegionList().observe(getViewLifecycleOwner(), regions -> {
            if (regions != null) {
                List<Region> modList = new ArrayList<>();
                modList.add(new Region(18, "전체")); // 전국 전체
                modList.addAll(regions);
                regionAdapter.updateList(modList);

                // 🔹 첫 Region 자동 선택 → District 바로 표시
                if (!regions.isEmpty()) {
                    selectedRegion = regions.get(0);
                    viewModel.loadDistricts(selectedRegion.getRegionId());
                }
            }
        });

        // 🔹 District LiveData 관찰
        viewModel.getDistrictList().observe(getViewLifecycleOwner(), districts -> {
            if (selectedRegion == null) return;

            List<District> modList = new ArrayList<>();
            // 첫 행: 도 전체
            modList.add(new District(getRegionTotalId(selectedRegion.getRegionId()), "전체"));

            if (districts != null && !districts.isEmpty()) {
                modList.addAll(districts);
            }
            districtAdapter.updateList(modList);
        });

        // 초기 Region 목록 로드
        viewModel.loadRegions();
    }

    /** 선택한 도의 전체 districtId 반환 */
    private int getRegionTotalId(int regionId) {
        switch (regionId) {
            case 1: return 47; // 서울 전체
            case 2: return 48; // 부산 전체
            case 3: return 49; // 대구 전체
            case 4: return 50; // 인천 전체
            case 5: return 51; // 광주 전체
            case 6: return 52; // 대전 전체
            case 7: return 53; // 울산 전체
            case 8: return 54; // 세종 전체
            case 9: return 55; // 경기 전체
            case 10: return 56; // 강원 전체
            case 11: return 57; // 충북 전체
            case 12: return 58; // 충남 전체
            case 13: return 59; // 전북 전체
            case 14: return 60; // 전남 전체
            case 15: return 61; // 경북 전체
            case 16: return 62; // 경남 전체
            case 17: return 63; // 제주 전체
            default: return -1;
        }
    }
}
