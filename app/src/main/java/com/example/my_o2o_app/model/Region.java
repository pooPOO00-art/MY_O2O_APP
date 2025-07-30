// Region.java
// '도(Region)' 정보를 담는 모델 클래스
package com.example.my_o2o_app.model;

public class Region {
    private int region_id;       // 도 ID (DB/서버와 매칭)
    private String region_name;  // 도 이름

    /** ✅ 기본 생성자 (Gson/Retrofit 직렬화용) */
    public Region() {}

    /** ✅ 커스텀 생성자 (UI에서 임의 생성용) */
    public Region(int regionId, String regionName) {
        this.region_id = regionId;
        this.region_name = regionName;
    }

    /** Getter */
    public int getRegionId() {
        return region_id;
    }

    public String getRegionName() {
        return region_name;
    }

    /** Setter (필요 시 동적 수정용) */
    public void setRegionId(int regionId) {
        this.region_id = regionId;
    }

    public void setRegionName(String regionName) {
        this.region_name = regionName;
    }
}
