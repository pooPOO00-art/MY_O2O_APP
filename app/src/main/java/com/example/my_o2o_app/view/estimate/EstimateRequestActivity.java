package com.example.my_o2o_app.view.estimate;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.OptionAdapter;
import com.example.my_o2o_app.model.Question;
import com.example.my_o2o_app.model.QuestionOption;
import com.example.my_o2o_app.viewmodel.EstimateRequestViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * DB 연동 견적 요청 화면
 * - 질문/옵션 단일 선택
 * - 이전/다음 이동 시 선택 상태 복원
 * - 마지막 질문에서 submitEstimate() 호출
 */
public class EstimateRequestActivity extends AppCompatActivity {

    private TextView tvCategoryName, tvQuestion, tvProgressPercent;
    private ProgressBar progressBar;
    private Button btnNext, btnPrev;
    private RecyclerView rvOptions;
    private OptionAdapter optionAdapter;

    private EstimateRequestViewModel viewModel;
    private List<Question> questionList = new ArrayList<>();
    private List<Integer> selectedPositionsPerQuestion = new ArrayList<>();

    private int currentIndex = 0;
    private int categoryId;
    private int userId = 1; // TODO: 로그인 후 실제 유저 ID 사용

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_request);

        // ✅ Intent 데이터
        categoryId = getIntent().getIntExtra("categoryId", -1);
        String categoryName = getIntent().getStringExtra("categoryName");
        Log.d("EstimateRequest", "categoryId=" + categoryId + ", name=" + categoryName);

        // ✅ View 초기화
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressBar = findViewById(R.id.progressBar);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        rvOptions = findViewById(R.id.rvOptions);

        tvCategoryName.setText(categoryName != null ? categoryName : "카테고리 없음");

        // ✅ ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(EstimateRequestViewModel.class);

        // ✅ RecyclerView + Adapter (단일 선택)
        optionAdapter = new OptionAdapter(selectedIndex -> {
            if (currentIndex < selectedPositionsPerQuestion.size()) {
                selectedPositionsPerQuestion.set(currentIndex, selectedIndex);
            }
        });
        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        rvOptions.setAdapter(optionAdapter);

        // ✅ LiveData 관찰
        viewModel.getQuestions().observe(this, questions -> {
            if (questions != null && !questions.isEmpty()) {
                questionList = questions;
                selectedPositionsPerQuestion.clear();
                for (int i = 0; i < questionList.size(); i++) {
                    selectedPositionsPerQuestion.add(-1); // 초기 선택 없음
                }
                currentIndex = 0;
                showQuestion(currentIndex);
            } else {
                tvQuestion.setText("질문을 불러오지 못했습니다.");
            }
        });

        // ✅ 서버에서 질문/옵션 불러오기
        viewModel.loadQuestions(categoryId);

        // ✅ 다음 버튼
        btnNext.setOnClickListener(v -> {
            if (currentIndex < questionList.size() - 1) {
                currentIndex++;
                showQuestion(currentIndex);
            } else {
                // 마지막 질문 → 견적 요청 전송
                List<Integer> selectedOptionIds = collectSelectedOptionIds();
                viewModel.selectOptions(selectedOptionIds);
                viewModel.submitEstimate(userId, categoryId,
                        () -> Log.i("EstimateRequest", "견적 요청 성공"),
                        () -> Log.e("EstimateRequest", "견적 요청 실패"));
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

    /**
     * 질문/옵션 표시 및 진행률 갱신
     */
    private void showQuestion(int index) {
        Question question = questionList.get(index);
        tvQuestion.setText(question.getContent());

        // 옵션 텍스트 목록
        List<String> optionTexts = new ArrayList<>();
        for (QuestionOption option : question.getOptions()) {
            optionTexts.add(option.getContent());
        }

        // 어댑터 갱신
        optionAdapter.setOptions(optionTexts);

        // 이전 선택 상태 복원
        int savedPos = selectedPositionsPerQuestion.get(index);
        if (savedPos != -1) {
            rvOptions.post(() -> optionAdapter.setSelectedPosition(savedPos));
        }

        // 진행률
        int progress = (int) (((index + 1) / (float) questionList.size()) * 100);
        progressBar.setProgress(progress);
        tvProgressPercent.setText(progress + "%");

        // 버튼 텍스트 갱신
        btnNext.setText(index == questionList.size() - 1 ? "견적 요청" : "다음");
    }

    /**
     * 전체 선택 옵션 ID를 수집
     */
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
