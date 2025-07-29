// District.java
// '시군구' 정보를 담는 모델 클래스
package com.example.my_o2o_app.model;

public class District {
    private int district_id;        // 시군구 ID (DB 컬럼명과 동일)
    private String district_name;   // 시군구 이름

    /** ✅ 기본 생성자 (Gson/Retrofit 직렬화용) */
    public District() {}

    /** ✅ 커스텀 생성자 (UI에서 '전체' 등 가짜 항목 생성용) */
    public District(int districtId, String districtName) {
        this.district_id = districtId;
        this.district_name = districtName;
    }

    // Getter
    public int getDistrictId() {
        return district_id;
    }

    public String getDistrictName() {
        return district_name;
    }

    // Setter (UI에서 임의 생성 시 사용)
    public void setDistrictId(int districtId) {
        this.district_id = districtId;
    }

    public void setDistrictName(String districtName) {
        this.district_name = districtName;
    }
}
