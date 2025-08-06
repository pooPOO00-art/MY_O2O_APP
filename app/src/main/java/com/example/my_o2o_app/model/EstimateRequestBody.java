package com.example.my_o2o_app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 서버로 전송할 견적 요청 Body
 * - userId: 요청한 사용자
 * - categoryId: 세부 카테고리
 * - districtId: 선택한 시군구(전체 선택 시 null 또는 0)
 * - optionIds: 선택한 질문 옵션 ID 리스트
 * - expertId: 직접 견적 시 선택된 전문가 ID (역매칭일 경우 null)
 */
public class EstimateRequestBody {

    @SerializedName("user_id")
    private int userId;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("district_id")
    private Integer districtId; // 시군구 ID

    @SerializedName("option_ids")
    private List<Integer> optionIds;

    @SerializedName("expert_id") // 🔹 직접 견적용 추가
    private Integer expertId;

    /**
     * 전체 필드를 초기화하는 생성자
     */
    public EstimateRequestBody(int userId, int categoryId, Integer districtId,
                               List<Integer> optionIds, Integer expertId) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.districtId = districtId;
        this.optionIds = optionIds;
        this.expertId = expertId; // 🔹 직접견적 시 expertId 설정
    }

    /**
     * 기존 역매칭용 생성자 (expertId 없음)
     */
    public EstimateRequestBody(int userId, int categoryId, Integer districtId,
                               List<Integer> optionIds) {
        this(userId, categoryId, districtId, optionIds, null);
    }

    /**
     * 기존 districtId 없는 경우 (호환용)
     */
    public EstimateRequestBody(int userId, int categoryId, List<Integer> optionIds) {
        this(userId, categoryId, null, optionIds, null);
    }

    // Getter
    public int getUserId() { return userId; }
    public int getCategoryId() { return categoryId; }
    public Integer getDistrictId() { return districtId; }
    public Integer getExpertId() { return expertId; }
    public List<Integer> getOptionIds() { return optionIds; }

    // Setter (필요 시)
    public void setExpertId(Integer expertId) { this.expertId = expertId; }
}
