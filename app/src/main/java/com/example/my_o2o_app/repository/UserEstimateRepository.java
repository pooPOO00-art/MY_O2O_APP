// UserEstimateRepository.java
// 받은 견적 목록 + 상태 업데이트 Repository

package com.example.my_o2o_app.repository;

import com.example.my_o2o_app.model.EstimateRequest;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class UserEstimateRepository {

    private final ApiService apiService;

    public UserEstimateRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    /**
     * 받은 견적 목록 조회
     */
    public void getUserEstimates(int userId, Callback<List<EstimateRequest>> callback) {
        apiService.getUserEstimates(userId).enqueue(callback);
    }

    /**
     * 🔹 상태 업데이트 (요청중 → 응답중/만료)
     */
    public void updateEstimateStatus(int estimateId, String status, Callback<Void> callback) {
        apiService.updateEstimateStatus(estimateId, status).enqueue(callback);
    }
}
