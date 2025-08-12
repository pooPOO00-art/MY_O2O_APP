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
    private int userId; // ✅ 로그인한 사용자 ID 추가

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_detail);

        // 1️⃣ View 초기화
        recyclerView = findViewById(R.id.recyclerViewExpertEstimates);
        progressBar = findViewById(R.id.progressBar);
        tvEstimateTitle = findViewById(R.id.tvEstimateTitle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2️⃣ 로그인한 사용자 ID 및 estimateId 수신
        estimateId = getIntent().getIntExtra("estimateId", -1);
        userId = getIntent().getIntExtra("userId", -1); // ✅ userId 받기

        // 3️⃣ 어댑터 설정
        adapter = new ExpertEstimateAdapter(new ArrayList<>(), expert -> {
            Log.d(TAG, "전문가 클릭: expertId=" + expert.getExpertId());

            // 전문가 상세 화면으로 이동
            Intent intent = new Intent(this, ExpertProfileActivity.class);
            intent.putExtra("expertId", expert.getExpertId());
            intent.putExtra("from", "estimate");
            intent.putExtra("userId", userId); // ✅ 로그인한 사용자 ID도 전달
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // 4️⃣ ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(ExpertEstimateViewModel.class);

        // 5️⃣ 견적 ID 유효성 확인
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
            progressBar.setVisibility(View.GONE);
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
