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
    private final UserEstimateRepository repository = new UserEstimateRepository(); // ✅ 수정

    public LiveData<List<EstimateRequest>> getEstimateList() {
        return estimateList;
    }

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
}
