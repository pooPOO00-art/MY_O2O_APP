package com.example.my_o2o_app.model;

/**
 * 견적 요청 데이터 모델
 * - /estimate/list API에서 내려주는 JSON과 매핑
 * - 서비스명, 지역명, 받은 견적 수, 남은 시간 포함
 */
public class EstimateRequest {
    private int estimateId;
    private int userId;
    private Integer expertId;

    private int districtId;
    private String districtName;    // ✅ 지역명

    private int categoryId;
    private String categoryName;    // ✅ 서비스명

    private String desiredDate;
    private String detailAddress;
    private Integer minPrice;
    private Integer maxPrice;

    private String status;
    private String createdAt;

    private int receivedCount;      // ✅ 받은 견적 수
    private int hoursLeft;          // ✅ 남은 시간 (48시간 기준)

    // ----------------------------
    // Getter
    // ----------------------------
    public int getEstimateId() { return estimateId; }
    public int getUserId() { return userId; }
    public Integer getExpertId() { return expertId; }

    public int getDistrictId() { return districtId; }
    public String getDistrictName() { return districtName; }

    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }

    public String getDesiredDate() { return desiredDate; }
    public String getDetailAddress() { return detailAddress; }

    public Integer getMinPrice() { return minPrice; }
    public Integer getMaxPrice() { return maxPrice; }

    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public int getReceivedCount() { return receivedCount; }
    public int getHoursLeft() { return hoursLeft; }

    public void setStatus(String status) { this.status = status; }

}
