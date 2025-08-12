// ApiService.java
// ✅ 서버와 통신할 모든 API를 정의하는 Retrofit 인터페이스
// ✅ 중복 메서드 제거 완료, ChatRoom 모델 import 완료

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
import com.example.my_o2o_app.model.ChatRoom;   // ✅ 추가
import com.example.my_o2o_app.model.MessagesResponse;  // ✅ 추가

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.PATCH;

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
    @POST("/user/register")
    Call<JsonObject> registerUser(@Body JsonObject userData);

    // ----------------------
    // 2️⃣ 채팅 관련
    // ----------------------

    /** 채팅방 생성 또는 조회 (중복 제거됨) */


    @POST("/chat/room")
    Call<JsonObject> createChatRoom(@Body JsonObject body);


    /** 로그인한 유저의 채팅방 목록 조회 */
    @GET("/chat/list")
    Call<List<ChatRoom>> getChatList(@Query("user_id") int userId);

    // ----------------------
    // 3️⃣ 카테고리 관련
    // ----------------------

    /** 카테고리 목록 불러오기 */
    @GET("/category/list")
    Call<JsonObject> getCategoryList();

    // ----------------------
    // 4️⃣ 전문가(고수) 관련
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

    /** 전문가 프로필 조회 */
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

    // ✅ 받은 견적 상태 업데이트
    @PATCH("/estimate/status")
    Call<Void> updateEstimateStatus(
            @Query("estimateId") int estimateId,
            @Query("status") String status
    );

    // ----------------------
    // 5️⃣ 지역(도/시군구) 관련
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
    // 6️⃣ 견적 요청 관련
    // ----------------------

    /** 세부 카테고리별 질문/옵션 조회 */
    @GET("/questions")
    Call<List<Question>> getQuestions(@Query("categoryId") int categoryId);

    /** 견적 요청 전송 (선택 옵션 저장) */
    @POST("/estimate")
    Call<Void> submitEstimate(@Body EstimateRequestBody body);

    /** 받은 견적 목록 조회 */
    @GET("/estimate/list")
    Call<List<EstimateRequest>> getUserEstimates(@Query("userId") int userId);

    /** 받은 견적 상세 → 전문가별 견적 리스트 */
    @GET("/estimate/detail")
    Call<ExpertEstimateResponse> getExpertEstimates(@Query("estimateId") int estimateId);

    // 인터페이스 내부에 아래 메서드 추가
    @GET("/chat/messages")
    Call<MessagesResponse> getChatMessages(@Query("room_id") int roomId);

}
