package com.example.my_o2o_app.model;

// 전문가(고수) 정보를 담는 모델 클래스
public class Expert {
    private int expertId;
    private String company_name;

    private String description;
    private String profileImage;

    private Integer category_id;
    private Integer district_id;


    // 기본 생성자 (Retrofit + Gson 용)
    public Expert() {}

    // getter
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

}
