package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_o2o_app.model.EstimateRequestBody;
import com.example.my_o2o_app.model.Question;
import com.example.my_o2o_app.repository.EstimateRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 견적 요청 화면 ViewModel
 * - 서버에서 질문/옵션 데이터를 불러옴
 * - 사용자가 선택한 옵션 ID들을 저장
 * - 마지막에 submitEstimate() 호출로 서버에 전송
 */
public class EstimateRequestViewModel extends ViewModel {

    private final EstimateRepository repository = new EstimateRepository();
    private final List<Integer> selectedOptionIds = new ArrayList<>(); // 사용자가 선택한 모든 옵션 ID

    /** 질문 목록 LiveData 반환 */
    public LiveData<List<Question>> getQuestions() {
        return repository.getQuestionsLiveData();
    }

    /** 서버에서 질문/옵션 목록 불러오기 */
    public void loadQuestions(int categoryId) {
        repository.fetchQuestions(categoryId);
    }

    /** 단일 옵션 선택 시 호출 */
    public void selectOption(int optionId) {
        selectedOptionIds.add(optionId);
    }

    /** 다중 선택 시 호출 */
    public void selectOptions(List<Integer> optionIds) {
        selectedOptionIds.clear();
        if (optionIds != null) {
            selectedOptionIds.addAll(optionIds);
        }
    }

    /**
     * 최종 견적 요청 전송
     * @param userId 사용자 ID
     * @param categoryId 카테고리 ID
     * @param districtId 선택한 시군구 ID (전국=83, 도전체=47~63)
     */
    public void submitEstimate(int userId, int categoryId, Integer districtId,
                               Runnable onSuccess, Runnable onError) {

        EstimateRequestBody body =
                new EstimateRequestBody(userId, categoryId, districtId, selectedOptionIds);

        repository.submitEstimate(body, onSuccess, onError);
    }
}
