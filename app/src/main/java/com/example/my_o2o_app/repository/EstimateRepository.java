package com.example.my_o2o_app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.my_o2o_app.model.EstimateRequestBody;
import com.example.my_o2o_app.model.Question;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 견적 요청 관련 Repository
 * - 질문/옵션 불러오기
 * - 선택 옵션 서버 전송
 * - 직접 견적 시 expertId 포함
 */
public class EstimateRepository {

    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    // 질문 목록 LiveData
    private final MutableLiveData<List<Question>> questionsLiveData = new MutableLiveData<>();

    /** 질문 LiveData 반환 */
    public LiveData<List<Question>> getQuestionsLiveData() {
        return questionsLiveData;
    }

    /**
     * 세부 카테고리별 질문/옵션 조회
     */
    public void fetchQuestions(int categoryId) {
        apiService.getQuestions(categoryId).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionsLiveData.postValue(response.body());
                } else {
                    questionsLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                questionsLiveData.postValue(null);
            }
        });
    }

    /**
     * 견적 요청 제출
     * @param body - userId, categoryId, districtId, optionIds, expertId 포함 // 🔹 수정
     */
    public void submitEstimate(EstimateRequestBody body, Runnable onSuccess, Runnable onError) {
        // 🔹 ApiService.submitEstimate()는 POST JSON Body 전송
        apiService.submitEstimate(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                } else {
                    onError.run();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onError.run();
            }
        });
    }
}
