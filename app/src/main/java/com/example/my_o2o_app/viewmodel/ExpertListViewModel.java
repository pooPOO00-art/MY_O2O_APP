// ExpertListViewModel.java
// 전문가 리스트 + 지역 정보 (district) 관리하는 ViewModel 클래스
// - 서버에서 전문가 및 지역 목록을 불러오고 LiveData로 제공
// - 필터 조건에 따라 전문가를 조회 가능

package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.example.my_o2o_app.model.Expert;
import com.example.my_o2o_app.model.District;
import com.example.my_o2o_app.repository.ExpertRepository;
import com.example.my_o2o_app.repository.RegionRepository;

import java.util.List;

public class ExpertListViewModel extends ViewModel {

    // ✅ Repository 객체 선언
    private final ExpertRepository expertRepository = new ExpertRepository();
    private final RegionRepository regionRepository = new RegionRepository();

    // ✅ 전문가 리스트 및 시군구 리스트를 LiveData로 관리
    private final MutableLiveData<List<Expert>> expertList = new MutableLiveData<>();
    private final MutableLiveData<List<District>> districtList = new MutableLiveData<>();

    // ✅ 외부에서 접근 가능한 LiveData getter
    public LiveData<List<Expert>> getExpertList() {
        return expertList;
    }

    public LiveData<List<District>> getDistrictList() {
        return districtList;
    }

    // ✅ 전체 전문가 불러오기 (초기 진입 시 사용)
    public void loadExperts() {
        Log.d("ExpertListViewModel", "loadExperts() 호출됨");
        expertRepository.getExperts(new ExpertRepository.ExpertCallback() {
            @Override
            public void onSuccess(List<Expert> experts) {
                Log.d("ExpertListViewModel", "전문가 " + experts.size() + "명 로드됨");
                expertList.setValue(experts);
            }

            @Override
            public void onError(String message) {
                Log.e("ExpertListViewModel", "전문가 로딩 실패: " + message);
            }
        });
    }

    // ✅ 필터 조건(categoryId, districtId, keyword)으로 전문가 필터링
    public void loadExpertsByFilter(Integer categoryId, Integer districtId, String keyword) {
        expertRepository.getExpertsByFilter(categoryId, districtId, keyword, new ExpertRepository.ExpertCallback() {
            @Override
            public void onSuccess(List<Expert> experts) {
                expertList.setValue(experts);
            }

            @Override
            public void onError(String message) {
                Log.e("ExpertListViewModel", "필터 조회 실패: " + message);
            }
        });
    }

    // ✅ 전체 시군구 리스트 서버에서 불러오기
    public void loadDistrictList() {
        regionRepository.fetchAllDistricts(new RegionRepository.OnResultListener<List<District>>() {
            @Override
            public void onSuccess(List<District> result) {
                districtList.setValue(result);
            }
        });
    }

    // ✅ 시군구 이름으로 district_id 찾기 (필터링 시 사용)
    public Integer getDistrictIdByName(String name) {
        if (districtList.getValue() == null) return null;

        for (District d : districtList.getValue()) {
            if (d.getDistrictName().equals(name)) {
                return d.getDistrictId();
            }
        }
        return null; // 못 찾은 경우
    }
}
