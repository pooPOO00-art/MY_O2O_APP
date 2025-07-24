// RegionFilterViewModel.java
// 지역(도) 및 시군구 목록을 관리하는 ViewModel 클래스
// - 서버에서 불러온 데이터를 LiveData로 제공하여 UI에 연결
// - Repository를 통해 비동기 호출 처리

package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_o2o_app.model.Region;
import com.example.my_o2o_app.model.District;
import com.example.my_o2o_app.repository.RegionRepository;

import java.util.List;

public class RegionFilterViewModel extends ViewModel {

    private final RegionRepository repository = new RegionRepository();

    private final MutableLiveData<List<Region>> regionList = new MutableLiveData<>();
    private final MutableLiveData<List<District>> districtList = new MutableLiveData<>();
    private final MutableLiveData<List<District>> allDistrictList = new MutableLiveData<>();  // ✅ 전체 district 추가

    // 🔹 도 목록 LiveData
    public LiveData<List<Region>> getRegionList() {
        return regionList;
    }

    // 🔹 특정 도의 시군구 LiveData
    public LiveData<List<District>> getDistrictList() {
        return districtList;
    }

    // 🔹 전체 시군구 목록 LiveData
    public LiveData<List<District>> getAllDistrictList() {
        return allDistrictList;
    }

    // ✅ 도 목록 불러오기
    public void loadRegions() {
        repository.fetchRegions(result -> regionList.postValue(result));
    }

    // ✅ 해당 도의 시군구 목록 불러오기
    public void loadDistricts(int regionId) {
        repository.fetchDistricts(regionId, result -> districtList.postValue(result));
    }

    // ✅ 전체 시군구 목록 불러오기 (district_name → district_id 변환용)
    public void loadAllDistricts() {
        repository.fetchAllDistricts(result -> allDistrictList.postValue(result));
    }
}
