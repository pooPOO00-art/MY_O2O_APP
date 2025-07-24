// ApiService.java
package com.example.my_o2o_app.network;

import com.google.gson.JsonObject;
// ApiService.java 상단에 추가
import com.example.my_o2o_app.model.Expert;
import java.util.List;
import com.example.my_o2o_app.model.ExpertResponse;

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


    @GET("/experts")
    Call<ExpertResponse> getApprovedExperts();

    @GET("/experts")
    Call<ExpertResponse> getExpertsByFilter(
            @retrofit2.http.Query("category_id") Integer categoryId,
            @retrofit2.http.Query("district_id") Integer districtId,
            @retrofit2.http.Query("keyword") String keyword
    );


}
