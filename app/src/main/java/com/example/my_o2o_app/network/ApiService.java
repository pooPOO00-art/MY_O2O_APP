package com.example.my_o2o_app.network;

import com.google.gson.JsonObject;
import com.example.my_o2o_app.model.Expert;
import com.example.my_o2o_app.model.ExpertResponse;
import com.example.my_o2o_app.model.Region;
import com.example.my_o2o_app.model.District;
import com.example.my_o2o_app.model.Question;
import com.example.my_o2o_app.model.EstimateRequestBody;
import com.example.my_o2o_app.model.EstimateRequest;
import com.example.my_o2o_app.model.ExpertEstimate;
import com.example.my_o2o_app.model.ExpertEstimateResponse;


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
//    @POST("/register")
//    Call<JsonObject> registerUser(@Body JsonObject userData);
    /** 회원가입 */
    @POST("/user/register")
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

    /** 승인된 전문가 전체 목록 (필터링 없이) */
    @GET("/experts")
    Call<ExpertResponse> getApprovedExperts();

    /** 조건별 전문가 검색 (전국 전체 / 도 전체 / 시군구) */
    @GET("/experts/filter")
    Call<JsonObject> getExpertsByFilter(
            @Query("category_id") Integer categoryId,
            @Query("district_id") Integer districtId,
            @Query("region_id") Integer regionId,
            @Query("keyword") String keyword
    );
    // 전문가 프로필 조회
    @GET("/expert/profile")
    Call<JsonObject> getExpertProfile(@Query("expertId") int expertId);

    /** 승인된 전문가 전체 목록 (예약·리뷰 통계 포함) */
    @GET("/experts/stats")
    Call<JsonObject> getApprovedExpertsWithStats();

    /** 조건별 전문가 검색 (예약·리뷰 통계 포함) */
    @GET("/experts/stats/filter")
    Call<JsonObject> getExpertsWithStatsByFilter(
            @Query("category_id") Integer categoryId,
            @Query("district_id") Integer districtId,
            @Query("region_id") Integer regionId,
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
    // 5️⃣ 견적 요청 관련
    // ----------------------

    /** 세부 카테고리별 질문/옵션 조회 */
    @GET("/questions")
    Call<List<Question>> getQuestions(@Query("categoryId") int categoryId);

    /** 견적 요청 전송 (선택 옵션 저장) */
    @POST("/estimate")
    Call<Void> submitEstimate(@Body EstimateRequestBody body);

    // ✅ 받은 견적 목록 조회
    @GET("/estimate/list")
    Call<List<EstimateRequest>> getUserEstimates(@Query("userId") int userId);

    //@GET("/estimate/detail")
    //Call<List<ExpertEstimate>> getExpertEstimates(@Query("estimateId") int estimateId);


    @GET("/estimate/detail")
    Call<ExpertEstimateResponse> getExpertEstimates(@Query("estimateId") int estimateId);


}
