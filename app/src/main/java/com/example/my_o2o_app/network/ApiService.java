// ApiService.java
package com.example.my_o2o_app.network;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/user/login")
    Call<JsonObject> loginUser(@Body JsonObject body);

    @POST("register")
    Call<JsonObject> registerUser(@Body JsonObject userData);

    // ✅ 카테고리 목록 불러오기 (서버에 /category/list가 있어야 함)
    @GET("/category/list")
    Call<JsonObject> getCategoryList();  // ← 꼭 필요!!
}
