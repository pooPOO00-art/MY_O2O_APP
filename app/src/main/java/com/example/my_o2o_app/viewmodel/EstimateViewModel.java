package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_o2o_app.model.EstimateRequest;
import com.example.my_o2o_app.repository.UserEstimateRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstimateViewModel extends ViewModel {

    private final MutableLiveData<List<EstimateRequest>> estimateList = new MutableLiveData<>();
    private final UserEstimateRepository repository = new UserEstimateRepository();

    public LiveData<List<EstimateRequest>> getEstimateList() {
        return estimateList;
    }

    /** 견적 목록 조회 */
    public void loadEstimates(int userId) {
        repository.getUserEstimates(userId, new Callback<List<EstimateRequest>>() {
            @Override
            public void onResponse(Call<List<EstimateRequest>> call, Response<List<EstimateRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    estimateList.postValue(response.body());
                } else {
                    estimateList.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<EstimateRequest>> call, Throwable t) {
                estimateList.postValue(null);
            }
        });
    }

    /** 🔹 상태 업데이트 */
    public void updateEstimateStatus(int estimateId, String status) {
        repository.updateEstimateStatus(estimateId, status, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 로그만 남김 (UI 갱신은 observe에서 처리됨)
                    System.out.println("✅ 상태 업데이트 성공: id=" + estimateId + ", status=" + status);
                } else {
                    System.out.println("⚠ 상태 업데이트 실패: id=" + estimateId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("❌ 상태 업데이트 네트워크 오류: " + t.getMessage());
            }
        });
    }
}
