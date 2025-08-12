package com.example.my_o2o_app.view.estimate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.EstimateAdapter;
import com.example.my_o2o_app.model.EstimateRequest;
import com.example.my_o2o_app.view.expert.ExpertProfileActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 받은 견적 목록 화면 (안정화 + 프로필 이동 지원)
 * - 초기 표시: 요청중 상태만
 * - 상태별 필터: 요청중 / 응답중 / 만료
 * - 카드 클릭 시:
 *   1) expertId 없는 경우 → EstimateDetailActivity 이동
 *   2) expertId 있는 경우 → ExpertProfileActivity 이동
 */
public class EstimateListFragment extends Fragment {

    private static final String TAG = "EstimateListFragment";


    private int userId; // 로그인한 사용자 ID

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EstimateAdapter adapter;
    private com.example.my_o2o_app.viewmodel.EstimateViewModel viewModel;

    // 상단 필터 버튼
    private TextView tvFilterRequesting, tvFilterResponding, tvFilterExpired;

    // 전체 견적 리스트 (필터링용)
    private List<EstimateRequest> allEstimates = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estimate_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewEstimate);
        progressBar = view.findViewById(R.id.progressBarEstimate);

        // 상단 필터 TextView 연결
        tvFilterRequesting = view.findViewById(R.id.tvFilterRequesting);
        tvFilterResponding = view.findViewById(R.id.tvFilterResponding);
        tvFilterExpired = view.findViewById(R.id.tvFilterExpired);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ✅ 어댑터 생성 (아이템 클릭 시 상태별 화면 이동)
        adapter = new EstimateAdapter(estimate -> {
            if (estimate == null) return;

            Integer expertId = estimate.getExpertId();
            String status = estimate.getStatus();

            Log.d(TAG, "카드 클릭: estimateId=" + estimate.getEstimateId() +
                    ", status=" + status +
                    ", expertId=" + expertId);

            // ✅ 모든 견적(일반/직접) 동일하게 상세 화면으로 이동
            Intent intent = new Intent(getContext(), EstimateDetailActivity.class);
            intent.putExtra("estimateId", estimate.getEstimateId());
            intent.putExtra("userId", userId); // ✅ 반드시 추가

            startActivity(intent);

        });
        recyclerView.setAdapter(adapter);

        // 🔹 필터 클릭 이벤트 설정
        tvFilterRequesting.setOnClickListener(v -> filterByStatus("요청중"));
        tvFilterResponding.setOnClickListener(v -> filterByStatus("응답중"));
        tvFilterExpired.setOnClickListener(v -> filterByStatus("만료"));



        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
        }

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(com.example.my_o2o_app.viewmodel.EstimateViewModel.class);
        observeViewModel();

        // 예시: 사용자 ID 1번으로 견적 목록 로드
        viewModel.loadEstimates(userId);

        return view;
    }

    public static EstimateListFragment newInstance(int userId) {
        EstimateListFragment fragment = new EstimateListFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }


    /** LiveData 관찰 → RecyclerView 갱신 */
    /** LiveData 관찰 → RecyclerView 갱신 */
    private void observeViewModel() {
        viewModel.getEstimateList().observe(getViewLifecycleOwner(), estimateList -> {
            progressBar.setVisibility(View.GONE);

            if (estimateList == null) {
                Log.w(TAG, "서버에서 견적 목록 없음 → 빈 리스트 처리");
                estimateList = new ArrayList<>();
            }

            allEstimates = estimateList;

            // 초기 화면: 요청중 + 직접견적
            List<EstimateRequest> requestingList = new ArrayList<>();
            for (EstimateRequest e : allEstimates) {
                String s = e.getStatus();
                if ("요청중".equals(s) || "직접견적".equals(s)) {
                    requestingList.add(e);
                }
            }
            adapter.updateData(requestingList);

            updateFilterCounts();
            highlightSelectedFilter("요청중");
        });
    }

    /** 상태별 필터링 */
    private void filterByStatus(String status) {
        List<EstimateRequest> filtered = new ArrayList<>();
        for (EstimateRequest e : allEstimates) {
            String s = e.getStatus();
            if (status.equals("요청중")) {
                if ("요청중".equals(s) || "직접견적".equals(s)) {
                    filtered.add(e);
                }
            } else if (status.equals("응답중")) {
                if ("응답중".equals(s) || "직접견적(응답중)".equals(s)) {
                    filtered.add(e);
                }
            } else if (status.equals("만료")) {
                if ("만료".equals(s)) {
                    filtered.add(e);
                }
            }
        }
        adapter.updateData(filtered);
        highlightSelectedFilter(status);
    }

    /** 🔹 선택된 필터만 강조 + 글씨 색상 변경 */
    private void highlightSelectedFilter(String status) {
        // 초기화
        tvFilterRequesting.setBackgroundResource(R.drawable.bg_filter_unselected);
        tvFilterResponding.setBackgroundResource(R.drawable.bg_filter_unselected);
        tvFilterExpired.setBackgroundResource(R.drawable.bg_filter_unselected);

        tvFilterRequesting.setTextColor(Color.BLACK);
        tvFilterResponding.setTextColor(Color.BLACK);
        tvFilterExpired.setTextColor(Color.BLACK);

        switch (status) {
            case "요청중":
                tvFilterRequesting.setBackgroundResource(R.drawable.bg_filter_selected);
                tvFilterRequesting.setTextColor(Color.WHITE);
                break;
            case "응답중":
                tvFilterResponding.setBackgroundResource(R.drawable.bg_filter_selected);
                tvFilterResponding.setTextColor(Color.WHITE);
                break;
            case "만료":
                tvFilterExpired.setBackgroundResource(R.drawable.bg_filter_selected);
                tvFilterExpired.setTextColor(Color.WHITE);
                break;
        }
    }

    /** 상태별 건수 갱신 */
    /** 상태별 건수 갱신 */
    private void updateFilterCounts() {
        long requesting = allEstimates.stream()
                .filter(e -> {
                    String s = e.getStatus();
                    return "요청중".equals(s) || "직접견적".equals(s);
                })
                .count();

        long responding = allEstimates.stream()
                .filter(e -> {
                    String s = e.getStatus();
                    return "응답중".equals(s) || "직접견적(응답중)".equals(s);
                })
                .count();

        long expired = allEstimates.stream()
                .filter(e -> "만료".equals(e.getStatus()))
                .count();

        tvFilterRequesting.setText("요청중(" + requesting + ")");
        tvFilterResponding.setText("응답중(" + responding + ")");
        tvFilterExpired.setText("만료(" + expired + ")");
    }
}
