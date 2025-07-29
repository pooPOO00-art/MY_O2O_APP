package com.example.my_o2o_app.model;

import java.util.List;

/**
 * 서버에 보낼 견적 요청 데이터
 */
public class EstimateRequestBody {
    private int user_id;
    private int category_id;
    private List<Integer> selected_options;

    public EstimateRequestBody(int userId, int categoryId, List<Integer> selectedOptions) {
        this.user_id = userId;
        this.category_id = categoryId;
        this.selected_options = selectedOptions;
    }
}
