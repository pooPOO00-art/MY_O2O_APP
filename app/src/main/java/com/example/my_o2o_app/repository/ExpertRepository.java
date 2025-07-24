// ExpertRepository.java
// 전문가 리스트를 서버에서 가져오는 Repository 계층

package com.example.my_o2o_app.repository;
import com.example.my_o2o_app.model.ExpertResponse;


import com.example.my_o2o_app.model.Expert;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertRepository {

    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public interface ExpertCallback {
        void onSuccess(List<Expert> experts);
        void onError(String message);
    }

    // 전문가 리스트 불러오기 (추후 categoryId, keyword 등 필터 추가 가능)
    public void getExperts(ExpertCallback callback) {
        Call<ExpertResponse> call = apiService.getApprovedExperts();
        call.enqueue(new Callback<ExpertResponse>() {
            @Override
            public void onResponse(Call<ExpertResponse> call, Response<ExpertResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getExperts());
                } else {
                    callback.onError("응답 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ExpertResponse> call, Throwable t) {
                callback.onError("서버 연결 실패: " + t.getMessage());
            }
        });

    }

    public void getExpertsByFilter(Integer categoryId, Integer districtId, String keyword, ExpertCallback callback) {
        Call<ExpertResponse> call = apiService.getExpertsByFilter(categoryId, districtId, keyword);
        call.enqueue(new Callback<ExpertResponse>() {
            @Override
            public void onResponse(Call<ExpertResponse> call, Response<ExpertResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getExperts());
                } else {
                    callback.onError("응답 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ExpertResponse> call, Throwable t) {
                callback.onError("서버 연결 실패: " + t.getMessage());
            }
        });
    }


}
