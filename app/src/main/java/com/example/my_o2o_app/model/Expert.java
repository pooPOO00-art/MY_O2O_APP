package com.example.my_o2o_app.model;

/**
 * 전문가(고수) 정보를 담는 모델 클래스
 * - DB/서버 JSON과 매핑
 * - Retrofit + Gson 직렬화 지원
 * - 수동 생성/수정 가능
 */
public class Expert {

    private int expertId;           // 전문가 ID
    private String company_name;    // 업체명
    private String description;     // 업체 설명
    private String profileImage;    // 프로필 이미지 URL

    private Integer category_id;    // 서비스 카테고리 ID (선택적)
    private Integer district_id;    // 담당 시군구 ID (선택적)

    /** ✅ 기본 생성자 (Gson/Retrofit 직렬화용) */
    public Expert() {}

    /** ✅ 커스텀 생성자 (Repository 등에서 수동 생성용) */
    public Expert(int expertId, String companyName, String description, String profileImage) {
        this.expertId = expertId;
        this.company_name = companyName;
        this.description = description;
        this.profileImage = profileImage;
    }

    /** Getter */
    public int getExpertId() {
        return expertId;
    }

    public String getCompanyName() {
        return company_name;
    }

    public String getDescription() {
        return description;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public Integer getCategoryId() {
        return category_id;
    }

    public Integer getDistrictId() {
        return district_id;
    }

    /** Setter (선택적: 필요 시 값 수정) */
    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }

    public void setCompanyName(String companyName) {
        this.company_name = companyName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setCategoryId(Integer categoryId) {
        this.category_id = categoryId;
    }

    public void setDistrictId(Integer districtId) {
        this.district_id = districtId;
    }
}
