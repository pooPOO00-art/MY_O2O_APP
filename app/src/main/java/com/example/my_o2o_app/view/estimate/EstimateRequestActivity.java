package com.example.my_o2o_app.view.estimate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.my_o2o_app.adapter.DistrictAdapter;
import com.example.my_o2o_app.adapter.OptionAdapter;
import com.example.my_o2o_app.adapter.RegionAdapter;
import com.example.my_o2o_app.model.District;
import com.example.my_o2o_app.model.Question;
import com.example.my_o2o_app.model.QuestionOption;
import com.example.my_o2o_app.model.Region;
import com.example.my_o2o_app.viewmodel.EstimateRequestViewModel;
import com.example.my_o2o_app.viewmodel.RegionFilterViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class EstimateRequestActivity extends AppCompatActivity {

    private TextView tvCategoryName, tvQuestion, tvProgressPercent;
    private ProgressBar progressBar;
    private Button btnNext, btnPrev, btnSelectRegion;
    private RecyclerView rvOptions;
    private OptionAdapter optionAdapter;

    private EstimateRequestViewModel viewModel;
    private RegionFilterViewModel regionViewModel;

    private List<Question> questionList = new ArrayList<>();
    private List<Integer> selectedPositionsPerQuestion = new ArrayList<>();
    private int currentIndex = 0;

    private int categoryId;
    private int userId = 1; // TODO: 실제 로그인 연동
    private Integer selectedRegionId = null;
    private Integer selectedDistrictId = null;

    // BottomSheetDialog 관련
    private BottomSheetDialog regionDialog;
    private RegionAdapter regionAdapter;
    private DistrictAdapter districtAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_request);

        categoryId = getIntent().getIntExtra("categoryId", -1);
        String categoryName = getIntent().getStringExtra("categoryName");
        Log.d("EstimateRequest", "categoryId=" + categoryId + ", name=" + categoryName);

        // View 초기화
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressBar = findViewById(R.id.progressBar);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnSelectRegion = findViewById(R.id.btnSelectRegion);
        rvOptions = findViewById(R.id.rvOptions);

        tvCategoryName.setText(categoryName != null ? categoryName : "카테고리 없음");

        viewModel = new ViewModelProvider(this).get(EstimateRequestViewModel.class);
        regionViewModel = new ViewModelProvider(this).get(RegionFilterViewModel.class);

        optionAdapter = new OptionAdapter(selectedIndex -> {
            if (currentIndex < selectedPositionsPerQuestion.size()) {
                selectedPositionsPerQuestion.set(currentIndex, selectedIndex);
            }
        });
        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        rvOptions.setAdapter(optionAdapter);

        // 질문 LiveData
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

        btnSelectRegion.setOnClickListener(v -> showRegionDialog());

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
                if (selectedDistrictId == null) {
                    Toast.makeText(this, "지역을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Integer> selectedOptionIds = collectSelectedOptionIds();
                viewModel.selectOptions(selectedOptionIds);

                Log.d("EstimateRequest", "submit with districtId=" + selectedDistrictId);

                viewModel.submitEstimate(userId, categoryId, selectedDistrictId,
                        () -> runOnUiThread(() -> {
                            Toast.makeText(this, "견적 요청 성공!", Toast.LENGTH_SHORT).show();
                            finish();
                        }),
                        () -> runOnUiThread(() ->
                                Toast.makeText(this, "견적 요청 실패", Toast.LENGTH_SHORT).show())
                );
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showQuestion(currentIndex);
            }
        });
    }

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

    private void showRegionDialog() {
        if (regionDialog == null) {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_region_select, null);
            RecyclerView recyclerRegion = dialogView.findViewById(R.id.recyclerRegion);
            RecyclerView recyclerDistrict = dialogView.findViewById(R.id.recyclerDistrict);

            recyclerRegion.setLayoutManager(new LinearLayoutManager(this));
            recyclerDistrict.setLayoutManager(new LinearLayoutManager(this));

            regionAdapter = new RegionAdapter(new ArrayList<>(), region -> {
                selectedRegionId = region.getRegionId();
                regionViewModel.loadDistricts(selectedRegionId);
            });
            recyclerRegion.setAdapter(regionAdapter);

            districtAdapter = new DistrictAdapter(new ArrayList<>(), district -> {
                selectedDistrictId = district.getDistrictId();
                btnSelectRegion.setText("선택 지역: " + district.getDistrictName());
                regionDialog.dismiss();
            });
            recyclerDistrict.setAdapter(districtAdapter);

            regionDialog = new BottomSheetDialog(this);
            regionDialog.setContentView(dialogView);

            regionViewModel.getRegionList().observe(this, regions -> {
                if (regions != null) regionAdapter.updateList(regions);
            });
            regionViewModel.getDistrictList().observe(this, districts -> {
                if (districts != null) {
                    // DB 기준으로 이미 전체 행 존재하므로 그대로 표시
                    districtAdapter.updateList(districts);
                }
            });

            regionViewModel.loadRegions();
        }
        regionDialog.show();
    }
}
