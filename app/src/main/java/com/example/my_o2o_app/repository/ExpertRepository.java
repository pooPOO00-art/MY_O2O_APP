// ExpertRepository.java
// 전문가 리스트를 서버에서 가져오는 Repository 계층

package com.example.my_o2o_app.repository;

import android.util.Log;

import com.example.my_o2o_app.model.Expert;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
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

    /** 승인된 전문가 전체 불러오기 */
    public void getExperts(ExpertCallback callback) {
        apiService.getApprovedExperts().enqueue(new Callback<com.example.my_o2o_app.model.ExpertResponse>() {
            @Override
            public void onResponse(Call<com.example.my_o2o_app.model.ExpertResponse> call,
                                   Response<com.example.my_o2o_app.model.ExpertResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getExperts());
                } else {
                    callback.onError("응답 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<com.example.my_o2o_app.model.ExpertResponse> call, Throwable t) {
                callback.onError("서버 연결 실패: " + t.getMessage());
            }
        });
    }

    /** 조건별 전문가 검색 (전국 전체/도 전체/시군구) */
    public void getExpertsByFilter(Integer categoryId, Integer districtId, Integer regionId, String keyword, ExpertCallback callback) {
        apiService.getExpertsByFilter(categoryId, districtId, regionId, keyword)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject body = response.body();
                            if (body.has("success") && body.get("success").getAsBoolean()) {
                                List<Expert> result = new ArrayList<>();
                                JsonArray arr = body.getAsJsonArray("experts");
                                for (JsonElement el : arr) {
                                    JsonObject obj = el.getAsJsonObject();
                                    Expert expert = new Expert(
                                            obj.get("expert_id").getAsInt(),
                                            obj.get("company_name").getAsString(),
                                            obj.has("description") && !obj.get("description").isJsonNull()
                                                    ? obj.get("description").getAsString() : "",
                                            obj.has("profile_image") && !obj.get("profile_image").isJsonNull()
                                                    ? obj.get("profile_image").getAsString() : ""
                                    );
                                    result.add(expert);
                                }
                                callback.onSuccess(result);
                            } else {
                                callback.onError("서버 응답 실패");
                            }
                        } else {
                            callback.onError("응답 실패: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        callback.onError("서버 연결 실패: " + t.getMessage());
                    }
                });
    }
}
