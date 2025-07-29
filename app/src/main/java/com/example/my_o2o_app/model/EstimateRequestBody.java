package com.example.my_o2o_app.model;

import java.util.List;

/**
 * 서버로 전송할 견적 요청 Body
 * - userId: 요청한 사용자
 * - categoryId: 세부 카테고리
 * - districtId: 선택한 시군구(전체 선택 시 null 또는 0)
 * - optionIds: 선택한 질문 옵션 ID 리스트
 */
public class EstimateRequestBody {

    private int userId;
    private int categoryId;
    private Integer districtId; // ✅ 추가
    private List<Integer> optionIds;

    public EstimateRequestBody(int userId, int categoryId, Integer districtId, List<Integer> optionIds) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.districtId = districtId;
        this.optionIds = optionIds;
    }

    // 기존 생성자 (districtId 없는 경우 호환)
    public EstimateRequestBody(int userId, int categoryId, List<Integer> optionIds) {
        this(userId, categoryId, null, optionIds);
    }

    // Getter
    public int getUserId() { return userId; }
    public int getCategoryId() { return categoryId; }
    public Integer getDistrictId() { return districtId; }
    public List<Integer> getOptionIds() { return optionIds; }
}
