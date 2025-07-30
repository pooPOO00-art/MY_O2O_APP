// EstimateDetailActivity.java
// 특정 견적 요청에 대한 전문가 제안 리스트 화면
// - RecyclerView로 전문가 견적 표시
// - 전문가 카드 클릭 시 ExpertProfileActivity로 이동

package com.example.my_o2o_app.view.estimate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


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

    private ProgressBar progressBar;
    private TextView tvEstimateTitle;

    private int estimateId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_detail);

        // 1️⃣ View 초기화
        recyclerView = findViewById(R.id.recyclerViewExpertEstimates);
        progressBar = findViewById(R.id.progressBar); // xml에 추가됨
        tvEstimateTitle = findViewById(R.id.tvEstimateTitle); // xml에 추가됨

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2️⃣ 어댑터 설정
        adapter = new ExpertEstimateAdapter(new ArrayList<>(), expert -> {
            Log.d(TAG, "전문가 클릭: expertId=" + expert.getExpertId());

            // 전문가 상세 화면으로 이동
            Intent intent = new Intent(this, ExpertProfileActivity.class);
            intent.putExtra("expertId", expert.getExpertId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // 3️⃣ ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(ExpertEstimateViewModel.class);

        // 4️⃣ estimateId 수신
        estimateId = getIntent().getIntExtra("estimateId", -1);
        if (estimateId != -1) {
            Log.d(TAG, "받은 estimateId=" + estimateId);
            progressBar.setVisibility(View.VISIBLE);
            viewModel.loadExpertEstimates(estimateId);
        } else {
            Log.w(TAG, "estimateId가 유효하지 않습니다.");
        }

        observeViewModel();
    }

    /** LiveData 관찰하여 UI 갱신 */
    private void observeViewModel() {
        viewModel.getExpertEstimates().observe(this, estimates -> {
            progressBar.setVisibility(View.GONE); // 로딩 완료 시 숨김
            if (estimates == null || estimates.isEmpty()) {
                Toast.makeText(this, "받은 견적이 없습니다.", Toast.LENGTH_SHORT).show();
                adapter.updateData(new ArrayList<>());
            } else {
                Log.d(TAG, "전문가 견적 수신: " + estimates.size());
                adapter.updateData(estimates);
            }
        });
    }
}

