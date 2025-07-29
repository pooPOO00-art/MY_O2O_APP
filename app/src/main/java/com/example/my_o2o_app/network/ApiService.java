package com.example.my_o2o_app.network;

import com.google.gson.JsonObject;
import com.example.my_o2o_app.model.Expert;
import com.example.my_o2o_app.model.ExpertResponse;
import com.example.my_o2o_app.model.Region;
import com.example.my_o2o_app.model.District;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API Service
 * - 서버와 통신할 모든 API를 정의
 */
public interface ApiService {

    // ----------------------
    // 1️⃣ 사용자 관련
    // ----------------------

    /** 로그인 */
    @POST("/user/login")
    Call<JsonObject> loginUser(@Body JsonObject body);

    /** 회원가입 */
    @POST("/register")
    Call<JsonObject> registerUser(@Body JsonObject userData);

    // ----------------------
    // 2️⃣ 카테고리 관련
    // ----------------------

    /** 카테고리 목록 불러오기 */
    @GET("/category/list")
    Call<JsonObject> getCategoryList();

    // ----------------------
    // 3️⃣ 전문가(고수) 관련
    // ----------------------

    /** 승인된 전문가 전체 목록 */
    @GET("/experts")
    Call<ExpertResponse> getApprovedExperts();

    /** 조건별 전문가 검색 */
    @GET("/experts")
    Call<ExpertResponse> getExpertsByFilter(
            @Query("category_id") Integer categoryId,
            @Query("district_id") Integer districtId,
            @Query("keyword") String keyword
    );

    // ----------------------
    // 4️⃣ 지역(도/시군구) 관련
    // ----------------------

    /** 도(region) 목록 */
    @GET("/regions")
    Call<List<Region>> getRegions();

    /** 특정 도의 시군구 목록 */
    @GET("/regions/{regionId}/districts")
    Call<List<District>> getDistricts(@Path("regionId") int regionId);

    /** 전체 시군구 목록 */
    @GET("/districts")
    Call<List<District>> getAllDistricts();

    // ----------------------
    // 5️⃣ 견적 요청 관련 (추가 예정)
    // ----------------------

    /**
     * 세부 카테고리별 질문/옵션 조회
     * 예: GET /questions?categoryId=16
     */
    @GET("/questions")
    Call<List<com.example.my_o2o_app.model.Question>> getQuestions(@Query("categoryId") int categoryId);

    /**
     * 견적 요청 전송 (선택 옵션 저장)
     */
    @POST("/estimate")
    Call<Void> submitEstimate(@Body com.example.my_o2o_app.model.EstimateRequestBody body);

}
