package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_o2o_app.model.ExpertEstimate;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertEstimateViewModel extends ViewModel {

    private final MutableLiveData<List<ExpertEstimate>> expertEstimates = new MutableLiveData<>();
    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public LiveData<List<ExpertEstimate>> getExpertEstimates() {
        return expertEstimates;
    }

    public void loadExpertEstimates(int estimateId) {
        apiService.getExpertEstimates(estimateId).enqueue(new Callback<List<ExpertEstimate>>() {
            @Override
            public void onResponse(Call<List<ExpertEstimate>> call, Response<List<ExpertEstimate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    expertEstimates.postValue(response.body());
                } else {
                    expertEstimates.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<ExpertEstimate>> call, Throwable t) {
                expertEstimates.postValue(null);
            }
        });
    }
}
