package com.example.my_o2o_app.view.estimate;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.OptionAdapter;
import com.example.my_o2o_app.model.Question;
import com.example.my_o2o_app.model.QuestionOption;
import com.example.my_o2o_app.view.common.RegionSelectBottomSheetDialog;
import com.example.my_o2o_app.viewmodel.EstimateRequestViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 견적 요청 화면
 * - 질문/옵션 단일 선택
 * - 공용 RegionSelectBottomSheetDialog로 지역 선택
 * - 선택 완료 후 서버로 견적 요청 전송
 */
public class EstimateRequestActivity extends AppCompatActivity {

    private TextView tvCategoryName, tvQuestion, tvProgressPercent;
    private ProgressBar progressBar;
    private Button btnNext, btnPrev, btnSelectRegion;
    private RecyclerView rvOptions;
    private OptionAdapter optionAdapter;

    private EstimateRequestViewModel viewModel;

    private List<Question> questionList = new ArrayList<>();
    private List<Integer> selectedPositionsPerQuestion = new ArrayList<>();
    private int currentIndex = 0;

    private int categoryId;
    private int userId = 1; // TODO: 로그인 연동 시 수정
    private Integer selectedRegionId = null;
    private Integer selectedDistrictId = null; // 서버 전송용

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_request);

        categoryId = getIntent().getIntExtra("categoryId", -1);
        String categoryName = getIntent().getStringExtra("categoryName");
        Log.d("EstimateRequest", "categoryId=" + categoryId + ", name=" + categoryName);

        int expertId = getIntent().getIntExtra("expertId", 0); // 🔹 추가: 직접 견적 여부 확인용
        Log.d("EstimateRequest", "expertId=" + expertId);      // 🔹 추가


        // ✅ View 초기화
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressBar = findViewById(R.id.progressBar);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnSelectRegion = findViewById(R.id.btnSelectRegion);
        rvOptions = findViewById(R.id.rvOptions);

        tvCategoryName.setText(categoryName != null ? categoryName : "카테고리 없음");

        // ✅ ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(EstimateRequestViewModel.class);

        // ✅ 옵션 RecyclerView
        optionAdapter = new OptionAdapter(selectedIndex -> {
            if (currentIndex < selectedPositionsPerQuestion.size()) {
                selectedPositionsPerQuestion.set(currentIndex, selectedIndex);
            }
        });
        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        rvOptions.setAdapter(optionAdapter);

        // ✅ 질문 LiveData
        viewModel.getQuestions().observe(this, questions -> {
            if (questions != null && !questions.isEmpty()) {
                questionList = questions;
                selectedPositionsPerQuestion.clear();
                for (int i = 0; i < questionList.size(); i++) {
                    selectedPositionsPerQuestion.add(-1);
                }
                currentIndex = 0;
                showQuestion(currentIndex);
            } else {
                tvQuestion.setText("질문을 불러오지 못했습니다.");
            }
        });

        viewModel.loadQuestions(categoryId);

        // ✅ 지역 선택 버튼 클릭 → 공용 BottomSheetDialog 사용
        btnSelectRegion.setOnClickListener(v -> {
            RegionSelectBottomSheetDialog dialog = new RegionSelectBottomSheetDialog();
            dialog.setOnRegionSelectedListener((regionId, districtId, regionName, districtName) -> {
                selectedRegionId = regionId;
                selectedDistrictId = districtId; // 서버 전송용
                btnSelectRegion.setText("선택 지역: " + regionName + " " + districtName);
            });
            dialog.show(getSupportFragmentManager(), "RegionSelect");
        });

        // ✅ 다음 버튼
        btnNext.setOnClickListener(v -> {
            int selectedOption = selectedPositionsPerQuestion.get(currentIndex);
            if (selectedOption == -1) {
                Toast.makeText(this, "옵션을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentIndex < questionList.size() - 1) {
                currentIndex++;
                showQuestion(currentIndex);
            } else {
                // 마지막 질문 → 견적 요청
                if (selectedDistrictId == null) {
                    Toast.makeText(this, "지역을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Integer> selectedOptionIds = collectSelectedOptionIds();
                viewModel.selectOptions(selectedOptionIds);

                Log.d("EstimateRequest", "submit with districtId=" + selectedDistrictId);

                viewModel.submitEstimate(userId, categoryId, selectedDistrictId,expertId,
                        () -> runOnUiThread(() -> {
                            Toast.makeText(this, "견적 요청 성공!", Toast.LENGTH_SHORT).show();
                            finish();
                        }),
                        () -> runOnUiThread(() ->
                                Toast.makeText(this, "견적 요청 실패", Toast.LENGTH_SHORT).show())
                );
            }
        });

        // ✅ 이전 버튼
        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showQuestion(currentIndex);
            }
        });
    }

    /** 질문/옵션 표시 및 진행률 갱신 */
    private void showQuestion(int index) {
        Question question = questionList.get(index);
        tvQuestion.setText(question.getContent());

        List<String> optionTexts = new ArrayList<>();
        for (QuestionOption option : question.getOptions()) {
            optionTexts.add(option.getContent());
        }

        optionAdapter.setOptions(optionTexts);

        int savedPos = selectedPositionsPerQuestion.get(index);
        if (savedPos != -1) {
            rvOptions.post(() -> optionAdapter.setSelectedPosition(savedPos));
        }

        int progress = (int) (((index + 1) / (float) questionList.size()) * 100);
        progressBar.setProgress(progress);
        tvProgressPercent.setText(progress + "%");

        btnNext.setText(index == questionList.size() - 1 ? "견적 요청" : "다음");
    }

    /** 전체 선택 옵션 ID 수집 */
    private List<Integer> collectSelectedOptionIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (int qIndex = 0; qIndex < questionList.size(); qIndex++) {
            int pos = selectedPositionsPerQuestion.get(qIndex);
            if (pos != -1) {
                selectedIds.add(questionList.get(qIndex).getOptions().get(pos).getOption_id());
            }
        }
        return selectedIds;
    }
}
