// EstimateListFragment.java
// 받은 견적 목록 화면 (RecyclerView)
// - estimate_request 테이블 기준으로 받은 견적 리스트 표시
// - 클릭 시 EstimateDetailActivity 이동

package com.example.my_o2o_app.view.estimate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.EstimateAdapter;
import com.example.my_o2o_app.model.EstimateRequest;
import com.example.my_o2o_app.viewmodel.EstimateViewModel;

import java.util.ArrayList;
import java.util.List;

public class EstimateListFragment extends Fragment {

    private static final String TAG = "EstimateListFragment"; // ✅ 로그 태그

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EstimateAdapter adapter;
    private EstimateViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estimate_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewEstimate);
        progressBar = view.findViewById(R.id.progressBarEstimate);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ✅ 어댑터 클릭 이벤트 → 상세 화면 이동
        adapter = new EstimateAdapter(estimate -> {
            Log.d(TAG, "카드 클릭: estimateId=" + estimate.getEstimateId());

            Intent intent = new Intent(getContext(), EstimateDetailActivity.class);
            intent.putExtra("estimateId", estimate.getEstimateId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "onCreateView: Fragment 생성 완료");

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(EstimateViewModel.class);
        observeViewModel();

        // 임시: 사용자 ID 1번 기준으로 테스트
        Log.d(TAG, "onCreateView: loadEstimates 호출");
        viewModel.loadEstimates(1);

        return view;
    }

    private void observeViewModel() {
        viewModel.getEstimateList().observe(getViewLifecycleOwner(), estimateList -> {
            Log.d(TAG, "LiveData 수신: " + (estimateList != null ? estimateList.size() : "null"));
            updateUI(estimateList);
        });
    }

    private void updateUI(List<EstimateRequest> estimateList) {
        progressBar.setVisibility(View.GONE);

        // ✅ Null-safe 처리
        if (estimateList == null) {
            Log.w(TAG, "updateUI: 서버 응답 없음 → 빈 목록으로 처리");
            adapter.updateData(new ArrayList<>());
        } else {
            Log.d(TAG, "updateUI: 목록 갱신, 개수 = " + estimateList.size());
            adapter.updateData(estimateList);
        }
    }
}
