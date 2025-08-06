// ExpertWithStats.java
// Retrofit/Gson 매핑용 DTO (JSON 키명 지정)

package com.example.my_o2o_app.model;

import com.google.gson.annotations.SerializedName;

public class ExpertWithStats {
    @SerializedName("expert_id")
    private int expertId;

    @SerializedName("company_name")
    private String companyName;

    @SerializedName("profile_image")
    private String profileImage;

    @SerializedName("career_years")
    private int careerYears;

    @SerializedName("reservation_count")
    private int reservationCount;

    @SerializedName("avg_rating")
    private double avgRating;

    @SerializedName("review_count")
    private int reviewCount;

    @SerializedName("description")
    private String description;

    @SerializedName("service_info")
    private String serviceInfo;

    @SerializedName("category_id")   // 서버 JSON 키명과 맞춤
    private int categoryId;

    // ✅ 기본 생성자
    public ExpertWithStats() {}

    // ✅ Getter/Setter
    public int getExpertId() { return expertId; }
    public void setExpertId(int expertId) { this.expertId = expertId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public int getCareerYears() { return careerYears; }
    public void setCareerYears(int careerYears) { this.careerYears = careerYears; }

    public int getReservationCount() { return reservationCount; }
    public void setReservationCount(int reservationCount) { this.reservationCount = reservationCount; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getServiceInfo() { return serviceInfo; }
    public void setServiceInfo(String serviceInfo) { this.serviceInfo = serviceInfo; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
}
