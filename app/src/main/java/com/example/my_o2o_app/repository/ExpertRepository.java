// ExpertRepository.java
// 전문가 리스트 + 예약/리뷰 통계 조회 Repository

package com.example.my_o2o_app.repository;

import android.util.Log;

import com.example.my_o2o_app.model.ExpertWithStats;
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

    /** 전문가 + 통계 콜백 인터페이스 */
    public interface ExpertStatsCallback {
        void onSuccess(List<ExpertWithStats> experts);
        void onError(String message);
    }

    /** 승인된 전문가 + 예약/리뷰 통계 전체 조회 */
    public void getExpertsWithStats(ExpertStatsCallback callback) {
        apiService.getApprovedExpertsWithStats().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    if (body.has("success") && body.get("success").getAsBoolean()) {
                        List<ExpertWithStats> result = parseExpertStatsArray(body.getAsJsonArray("experts"));
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

    /** 필터 조건으로 전문가 + 통계 조회 */
    public void getExpertsWithStatsByFilter(Integer categoryId, Integer districtId, Integer regionId,
                                            String keyword, ExpertStatsCallback callback) {
        apiService.getExpertsWithStatsByFilter(categoryId, districtId, regionId, keyword)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject body = response.body();
                            if (body.has("success") && body.get("success").getAsBoolean()) {
                                List<ExpertWithStats> result = parseExpertStatsArray(body.getAsJsonArray("experts"));
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

    /** JSON 배열 -> ExpertWithStats 리스트 변환 */
    private List<ExpertWithStats> parseExpertStatsArray(JsonArray arr) {
        List<ExpertWithStats> result = new ArrayList<>();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            ExpertWithStats expert = new ExpertWithStats();

            expert.setExpertId(obj.get("expert_id").getAsInt());
            expert.setCompanyName(obj.get("company_name").getAsString());
            expert.setProfileImage(obj.has("profile_image") && !obj.get("profile_image").isJsonNull()
                    ? obj.get("profile_image").getAsString() : "");
            expert.setCareerYears(obj.has("career_years") && !obj.get("career_years").isJsonNull()
                    ? obj.get("career_years").getAsInt() : 0);
            expert.setReservationCount(obj.has("reservation_count") && !obj.get("reservation_count").isJsonNull()
                    ? obj.get("reservation_count").getAsInt() : 0);
            expert.setAvgRating(obj.has("avg_rating") && !obj.get("avg_rating").isJsonNull()
                    ? obj.get("avg_rating").getAsDouble() : 0.0);
            expert.setReviewCount(obj.has("review_count") && !obj.get("review_count").isJsonNull()
                    ? obj.get("review_count").getAsInt() : 0);

            // ✅ 업체 설명 추가
            expert.setDescription(obj.has("description") && !obj.get("description").isJsonNull()
                    ? obj.get("description").getAsString() : "");

            // ✅ 대표 서비스/지역 추가
            expert.setServiceInfo(obj.has("service_info") && !obj.get("service_info").isJsonNull()
                    ? obj.get("service_info").getAsString() : "");

            result.add(expert);
        }
        return result;
    }
}
