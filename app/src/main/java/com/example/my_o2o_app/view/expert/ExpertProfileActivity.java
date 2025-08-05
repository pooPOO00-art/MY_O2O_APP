package com.example.my_o2o_app.view.expert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.ExpertWithStats;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;
import com.example.my_o2o_app.view.estimate.EstimateRequestActivity;
import com.example.my_o2o_app.view.chat.ChatActivity;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ExpertProfileActivity
 * - 전문가(고수) 상세 프로필 화면
 * - 고수찾기/받은견적에 따라 하단 버튼 변경
 * - 업체 정보는 스크롤 가능, 버튼은 하단 고정
 */
public class ExpertProfileActivity extends AppCompatActivity {

    private static final String TAG = "ExpertProfileActivity";

    private ImageView ivProfile;
    private TextView tvCompanyName, tvDescription, tvRegion, tvRating;
    private TextView tvHireCount, tvRatingStat, tvCareerStat;
    private FrameLayout layoutBottomFixed;

    private int expertId;
    private String from; // "find_expert" or "estimate"

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);

        System.out.println("### ExpertProfileActivity onCreate 실행됨 ###");

        // 1️⃣ Intent 데이터 수신
        expertId = getIntent().getIntExtra("expertId", -1);
        from = getIntent().getStringExtra("from");

        Log.i(TAG, "=== onCreate 실행됨 ===");
        Log.i(TAG, "expertId=" + expertId + ", from='" + from + "'");


        if (expertId == -1) {
            Toast.makeText(this, "전문가 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initView();
        setupBottomButtons();
        loadExpertProfileFromServer();
    }

    /** View 초기화 */
    private void initView() {
        ivProfile = findViewById(R.id.ivProfile);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvDescription = findViewById(R.id.tvDescription);
        tvRegion = findViewById(R.id.tvRegion);
        tvRating = findViewById(R.id.tvRating);

        tvHireCount = findViewById(R.id.tvHireCount);
        tvRatingStat = findViewById(R.id.tvRatingStat);
        tvCareerStat = findViewById(R.id.tvCareerStat);

        layoutBottomFixed = findViewById(R.id.layoutBottomFixed);
    }

    /** 하단 버튼 동적 로드 */
    private void setupBottomButtons() {
        LayoutInflater inflater = LayoutInflater.from(this);
        Log.i(TAG, "setupBottomButtons() 호출됨, from='" + from + "'");

        if ("find_expert".equalsIgnoreCase(from)) {
            Log.i(TAG, "inflate → 견적 요청 버튼");
            inflater.inflate(R.layout.layout_bottom_request, layoutBottomFixed, true);

            Button btnRequest = layoutBottomFixed.findViewById(R.id.btnRequestEstimate);
            btnRequest.setOnClickListener(v -> {
                Log.i(TAG, "견적 요청 클릭 (expertId=" + expertId + ")");
                Intent intent = new Intent(this, EstimateRequestActivity.class);
                intent.putExtra("categoryId", -1);
                intent.putExtra("categoryName", tvCompanyName.getText().toString());
                startActivity(intent);
            });

        } else if ("estimate".equalsIgnoreCase(from)) {
            Log.i(TAG, "inflate → 채팅 버튼");
            System.out.println("### 채팅 버튼 inflate 시작 ###");

            inflater.inflate(R.layout.layout_bottom_chat, layoutBottomFixed, true);

            Button btnChat = layoutBottomFixed.findViewById(R.id.btnChatExpert);
            btnChat.setOnClickListener(v -> {
                System.out.println("### 채팅 버튼 클릭됨 ###");
                Log.i(TAG, "채팅하기 클릭 (expertId=" + expertId + ")");
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("expertId", expertId);
                startActivity(intent);
            });

        } else {
            Log.w(TAG, "from 값 불명 → 버튼 미표시");
        }
    }

    /** DB에서 전문가 프로필 조회 (Retrofit + JsonObject → DTO 변환) */
    private void loadExpertProfileFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getExpertProfile(expertId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "응답 실패 또는 body=null");
                    Toast.makeText(ExpertProfileActivity.this,
                            "전문가 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject root = response.body();
                if (!root.has("expert") || root.get("expert").isJsonNull()) {
                    Log.e(TAG, "expert key 없음 또는 null");
                    Toast.makeText(ExpertProfileActivity.this,
                            "전문가 데이터가 비어있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject obj = root.getAsJsonObject("expert");
                ExpertWithStats expert = new ExpertWithStats();

                // ✅ 안전한 파싱
                expert.setExpertId(obj.has("expert_id") && !obj.get("expert_id").isJsonNull() ? obj.get("expert_id").getAsInt() : 0);
                expert.setCompanyName(obj.has("company_name") && !obj.get("company_name").isJsonNull() ? obj.get("company_name").getAsString() : "");
                expert.setProfileImage(obj.has("profile_image") && !obj.get("profile_image").isJsonNull() ? obj.get("profile_image").getAsString() : "");
                expert.setAvgRating(obj.has("avg_rating") && !obj.get("avg_rating").isJsonNull() ? obj.get("avg_rating").getAsDouble() : 0.0);
                expert.setReviewCount(obj.has("review_count") && !obj.get("review_count").isJsonNull() ? obj.get("review_count").getAsInt() : 0);
                expert.setReservationCount(obj.has("reservation_count") && !obj.get("reservation_count").isJsonNull() ? obj.get("reservation_count").getAsInt() : 0);
                expert.setCareerYears(obj.has("career_years") && !obj.get("career_years").isJsonNull() ? obj.get("career_years").getAsInt() : 0);
                expert.setDescription(obj.has("description") && !obj.get("description").isJsonNull() ? obj.get("description").getAsString() : "설명 없음");
                expert.setServiceInfo(obj.has("service_info") && !obj.get("service_info").isJsonNull() ? obj.get("service_info").getAsString() : "지역 정보 없음");

                updateUI(expert);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Retrofit 실패: " + t.getMessage());
                Toast.makeText(ExpertProfileActivity.this,
                        "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** ExpertWithStats DTO → UI 반영 */
    private void updateUI(ExpertWithStats expert) {
        tvCompanyName.setText(expert.getCompanyName());
        tvDescription.setText(expert.getDescription());
        tvRegion.setText(expert.getServiceInfo());
        tvRating.setText("⭐ " + expert.getAvgRating() + " (" + expert.getReviewCount() + ")");

        tvHireCount.setText("고용 " + expert.getReservationCount() + "회");
        tvRatingStat.setText("⭐ " + expert.getAvgRating() + " (" + expert.getReviewCount() + ")");
        tvCareerStat.setText("총 경력 " + expert.getCareerYears() + "년");

        if (!expert.getProfileImage().isEmpty()) {
            Glide.with(this)
                    .load(expert.getProfileImage())
                    .placeholder(R.drawable.ic_placeholder)
                    .circleCrop()
                    .into(ivProfile);
        } else {
            ivProfile.setImageResource(R.drawable.ic_placeholder);
        }
    }
}
