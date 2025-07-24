// District.java
// '시군구' 정보를 담는 모델 클래스
package com.example.my_o2o_app.model;

public class District {
    private int district_id;        // 시군구 ID
    private String district_name;   // 시군구 이름

    // Getter
    public int getDistrictId() {
        return district_id;
    }

    public String getDistrictName() {
        return district_name;
    }
}
