package com.example.my_o2o_app.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_o2o_app.model.ExpertEstimate;
import com.example.my_o2o_app.model.ExpertEstimateResponse;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 특정 견적 요청(estimateId)에 대한 전문가 견적 리스트를 관리하는 ViewModel
 */
public class ExpertEstimateViewModel extends ViewModel {

    private static final String TAG = "EstimateAPI";

    private final MutableLiveData<List<ExpertEstimate>> expertEstimates = new MutableLiveData<>();
    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public LiveData<List<ExpertEstimate>> getExpertEstimates() {
        return expertEstimates;
    }

    public void loadExpertEstimates(int estimateId) {
        Log.d(TAG, "loadExpertEstimates() 호출: estimateId=" + estimateId);

        apiService.getExpertEstimates(estimateId).enqueue(new Callback<ExpertEstimateResponse>() {
            @Override
            public void onResponse(Call<ExpertEstimateResponse> call, Response<ExpertEstimateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExpertEstimateResponse body = response.body();
                    Log.d(TAG, "응답 success=" + body.isSuccess() + ", 건수=" + (body.getEstimates() != null ? body.getEstimates().size() : 0));

                    // 상세 로그 출력
                    if (body.getEstimates() != null) {
                        for (ExpertEstimate ee : body.getEstimates()) {
                            Log.d(TAG, "expertId=" + ee.getExpertId()
                                    + ", companyName=" + ee.getCompanyName()
                                    + ", price=" + ee.getPrice()
                                    + ", message=" + ee.getMessage());
                        }
                    }

                    expertEstimates.postValue(body.getEstimates());
                } else {
                    Log.w(TAG, "응답 실패: code=" + response.code() + ", message=" + response.message());
                    expertEstimates.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ExpertEstimateResponse> call, Throwable t) {
                Log.e(TAG, "네트워크 오류: " + t.getMessage(), t);
                expertEstimates.postValue(null);
            }
        });
    }
}
