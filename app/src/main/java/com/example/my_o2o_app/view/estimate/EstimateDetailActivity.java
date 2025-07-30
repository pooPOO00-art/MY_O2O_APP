// EstimateDetailActivity.java
// 특정 견적 요청에 대한 전문가 제안 리스트 화면
// - RecyclerView로 전문가 견적 표시
// - 전문가 카드 클릭 시 ExpertProfileActivity로 이동

package com.example.my_o2o_app.view.estimate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.ExpertEstimateAdapter;
import com.example.my_o2o_app.model.ExpertEstimate;
import com.example.my_o2o_app.view.expert.ExpertProfileActivity;
import com.example.my_o2o_app.viewmodel.ExpertEstimateViewModel;

import java.util.ArrayList;

public class EstimateDetailActivity extends AppCompatActivity {

    private static final String TAG = "EstimateDetailActivity";

    private RecyclerView recyclerView;
    private ExpertEstimateAdapter adapter;
    private ExpertEstimateViewModel viewModel;
    private int estimateId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_detail);

        // ✅ 1. 이전 화면에서 전달받은 estimateId 확인
        estimateId = getIntent().getIntExtra("estimateId", -1);
        Log.d(TAG, "받은 estimateId=" + estimateId);

        // ✅ 2. RecyclerView 초기화
        recyclerView = findViewById(R.id.recyclerViewExpertEstimates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ✅ 3. 어댑터 생성 (전문가 클릭 시 ExpertProfileActivity 이동)
        adapter = new ExpertEstimateAdapter(new ArrayList<>(), expert -> {
            Log.d(TAG, "전문가 클릭: expertId=" + expert.getExpertId());

            // 전문가 상세 화면으로 이동
            Intent intent = new Intent(this, ExpertProfileActivity.class);
            intent.putExtra("expertId", expert.getExpertId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // ✅ 4. ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(ExpertEstimateViewModel.class);

        observeViewModel();

        // ✅ 5. 견적 데이터 로드
        if (estimateId != -1) {
            viewModel.loadExpertEstimates(estimateId);
        } else {
            Log.w(TAG, "estimateId가 유효하지 않습니다.");
        }
    }

    /** LiveData 관찰하여 UI 갱신 */
    private void observeViewModel() {
        viewModel.getExpertEstimates().observe(this, estimates -> {
            Log.d(TAG, "전문가 견적 수신: " + (estimates != null ? estimates.size() : "null"));
            adapter.updateData(estimates);
        });
    }
}
