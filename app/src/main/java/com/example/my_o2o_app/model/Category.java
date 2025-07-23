package com.example.my_o2o_app.model;

// Category.java
// 서비스 카테고리 정보를 저장하는 데이터 모델 클래스

public class Category {
    // 카테고리 고유 ID
    private int category_id;

    // 상위 카테고리 ID (null 가능)
    private Integer parent_id;

    // 카테고리 이름
    private String category_name;

    // 생성자
    public Category(int category_id, Integer parent_id, String category_name) {
        this.category_id = category_id;
        this.parent_id = parent_id;
        this.category_name = category_name;
    }

    // Getter
    public int getCategory_id() {
        return category_id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    // Setter
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}

