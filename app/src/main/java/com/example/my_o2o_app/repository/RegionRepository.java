// RegionRepository.java
// 도 및 시군구 정보를 서버로부터 받아오는 API 요청 처리 클래스
// Retrofit 인터페이스를 통해 비동기 요청을 수행하고 결과 콜백으로 반환

package com.example.my_o2o_app.repository;

import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;

import com.example.my_o2o_app.model.Region;
import com.example.my_o2o_app.model.District;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegionRepository {

    private final ApiService apiService =
            ApiClient.getClient().create(ApiService.class);

    // ✅ 도(region) 목록 불러오기
    public void fetchRegions(OnResultListener<List<Region>> listener) {
        apiService.getRegions().enqueue(new Callback<List<Region>>() {
            @Override
            public void onResponse(Call<List<Region>> call, Response<List<Region>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Region>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // ✅ 특정 도에 속한 시군구 목록 불러오기
    public void fetchDistricts(int regionId, OnResultListener<List<District>> listener) {
        apiService.getDistricts(regionId).enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // 전체 시군구 리스트 불러오기
    public void fetchAllDistricts(OnResultListener<List<District>> listener) {
        apiService.getAllDistricts().enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                t.printStackTrace(); // 실패 로그
            }
        });
    }


    // ✅ 공통 콜백 인터페이스
    public interface OnResultListener<T> {
        void onSuccess(T result);
    }
}
