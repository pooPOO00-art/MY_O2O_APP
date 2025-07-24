// Region.java
// '도' 정보를 담는 모델 클래스
package com.example.my_o2o_app.model;

public class Region {
    private int region_id;       // 도 ID (서버로부터 받아옴)
    private String region_name;  // 도 이름

    // Getter
    public int getRegionId() {
        return region_id;
    }

    public String getRegionName() {
        return region_name;
    }
}
