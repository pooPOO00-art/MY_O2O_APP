// ExpertListViewModel.java
// 전문가 리스트를 불러오는 ViewModel (고수찾기 전용)

package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.example.my_o2o_app.model.Expert;
import com.example.my_o2o_app.repository.ExpertRepository;

import java.util.List;

public class ExpertListViewModel extends ViewModel {

    private final ExpertRepository repository = new ExpertRepository();

    private final MutableLiveData<List<Expert>> expertList = new MutableLiveData<>();

    public LiveData<List<Expert>> getExpertList() {
        return expertList;
    }

    // 전문가 리스트 불러오기 (필터링 등은 추후 매개변수로 확장)
    public void loadExperts() {
        Log.d("ExpertListViewModel", "loadExperts() 호출됨");  // 이거 추가
        repository.getExperts(new ExpertRepository.ExpertCallback() {
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

    public void loadExpertsByFilter(Integer categoryId, Integer districtId, String keyword) {
        repository.getExpertsByFilter(categoryId, districtId, keyword, new ExpertRepository.ExpertCallback() {
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


}
