// ExpertListViewModel.java (리팩터링 버전)
// 전문가 리스트를 ExpertWithStats 형태로 관리

package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.example.my_o2o_app.model.ExpertWithStats;
import com.example.my_o2o_app.model.District;
import com.example.my_o2o_app.repository.ExpertRepository;
import com.example.my_o2o_app.repository.RegionRepository;

import java.util.List;

public class ExpertListViewModel extends ViewModel {

    private final ExpertRepository expertRepository = new ExpertRepository();
    private final RegionRepository regionRepository = new RegionRepository();

    private final MutableLiveData<List<ExpertWithStats>> expertList = new MutableLiveData<>();
    private final MutableLiveData<List<District>> districtList = new MutableLiveData<>();

    public LiveData<List<ExpertWithStats>> getExpertList() {
        return expertList;
    }

    public LiveData<List<District>> getDistrictList() {
        return districtList;
    }

    /** 초기 전체 전문가 불러오기 */
    public void loadExperts() {
        Log.d("ExpertListViewModel", "loadExperts() 호출됨");
        expertRepository.getExpertsWithStats(new ExpertRepository.ExpertStatsCallback() {
            @Override
            public void onSuccess(List<ExpertWithStats> experts) {
                Log.d("ExpertListViewModel", "전문가 " + experts.size() + "명 로드됨");
                expertList.setValue(experts);
            }

            @Override
            public void onError(String message) {
                Log.e("ExpertListViewModel", "전문가 로딩 실패: " + message);
            }
        });
    }

    /** 전국 전체/도 전체/시군구 필터링 지원 */
    public void loadExpertsByFilter(Integer categoryId, Integer districtId, Integer regionId, String keyword) {
        expertRepository.getExpertsWithStatsByFilter(categoryId, districtId, regionId, keyword,
                new ExpertRepository.ExpertStatsCallback() {
                    @Override
                    public void onSuccess(List<ExpertWithStats> experts) {
                        expertList.postValue(experts);
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("ExpertListViewModel", "필터 조회 실패: " + message);
                    }
                });
    }

    /** 전체 시군구 목록 서버에서 불러오기 */
    public void loadDistrictList() {
        regionRepository.fetchAllDistricts(new RegionRepository.OnResultListener<List<District>>() {
            @Override
            public void onSuccess(List<District> result) {
                districtList.setValue(result);
            }
        });
    }
}
