// User.java
// 사용자 정보를 저장하는 데이터 모델 클래스 (MVVM의 Model 역할)

package com.example.my_o2o_app.model;

public class User {
    // 사용자 고유 ID (DB에서 자동 증가로 처리 가능)
    private int userId;

    // 로그인용 아이디
    private String id;

    // 로그인용 비밀번호
    private String password;

    // 사용자 이름
    private String name;

    // 사용자 전화번호
    private String phone;

    // 생성자
    public User(String id, String password, String name, String phone) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    // 전체 필드를 받는 생성자 (userId 포함)
    public User(int userId, String id, String password, String name, String phone) {
        this.userId = userId;
        this.id = id;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    // Getter & Setter (필수)

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
