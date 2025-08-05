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
import com.example.my_o2o_app.view.chat.ChatActivity; // ✅ 채팅 전용 액티비티
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertProfileActivity extends AppCompatActivity {

    private static final String TAG = "ExpertProfileActivity";

    // ✅ View 참조
    private ImageView ivProfile;
    private TextView tvCompanyName, tvDescription, tvRegion, tvRating;
    private TextView tvHireCount, tvRatingStat, tvCareerStat;

    // ✅ 동적 버튼 컨테이너
    private FrameLayout layoutBottomContainer;

    // ✅ 전달받은 전문가 ID
    private int expertId;
    private String from; // 화면 진입 구분용

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);

        // 1️⃣ expertId & from 수신
        expertId = getIntent().getIntExtra("expertId", -1);
        from = getIntent().getStringExtra("from"); // "find_expert" or "estimate"
        Log.d(TAG, "받은 expertId=" + expertId + ", from=" + from);

        if (expertId == -1) {
            Toast.makeText(this, "전문가 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2️⃣ View 초기화
        initView();

        // 3️⃣ 하단 버튼 동적 로드
        setupBottomButtons();

        // 4️⃣ 서버에서 전문가 프로필 조회
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

        layoutBottomContainer = findViewById(R.id.layoutBottomContainer);
    }

    /** 하단 버튼 동적 로드 */
    private void setupBottomButtons() {
        LayoutInflater inflater = LayoutInflater.from(this);

        if ("find_expert".equals(from)) {
            // 고수찾기 → 견적 요청하기 버튼
            inflater.inflate(R.layout.layout_bottom_request, layoutBottomContainer, true);
            Button btnRequest = layoutBottomContainer.findViewById(R.id.btnRequestEstimate);
            btnRequest.setOnClickListener(v -> {
                Log.d(TAG, "견적 요청하기 클릭 (expertId=" + expertId + ")");
                // ✅ 견적 요청 화면 이동
                Intent intent = new Intent(this, EstimateRequestActivity.class);
                intent.putExtra("categoryId", -1); // TODO: 실제 categoryId 연결 가능 시 수정
                intent.putExtra("categoryName", tvCompanyName.getText().toString());
                startActivity(intent);
            });

        } else if ("estimate".equals(from)) {
            // 받은견적 → 채팅 버튼
            inflater.inflate(R.layout.layout_bottom_chat, layoutBottomContainer, true);
            Button btnChat = layoutBottomContainer.findViewById(R.id.btnChatExpert);
            btnChat.setOnClickListener(v -> {
                Log.d(TAG, "채팅하기 클릭 (expertId=" + expertId + ")");
                Toast.makeText(this, "채팅 화면으로 이동", Toast.LENGTH_SHORT).show();

                // ✅ ChatActivity로 이동
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("expertId", expertId); // 추후 채팅방 식별에 활용 가능
                startActivity(intent);
            });

        } else {
            Log.w(TAG, "from 값이 없거나 알 수 없음 → 하단 버튼 표시 안함");
        }
    }

    /** DB에서 전문가 프로필 조회 (Retrofit + JsonObject → DTO 변환) */
    private void loadExpertProfileFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getExpertProfile(expertId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ExpertProfileActivity.this,
                            "전문가 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "응답 실패 또는 body=null");
                    return;
                }

                JsonObject root = response.body();
                if (!root.has("expert") || root.get("expert").isJsonNull()) {
                    Toast.makeText(ExpertProfileActivity.this,
                            "전문가 데이터가 비어있습니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "expert key 없음 또는 null");
                    return;
                }

                JsonObject obj = root.getAsJsonObject("expert");
                ExpertWithStats expert = new ExpertWithStats();

                // ✅ 안전한 파싱
                if (obj.has("expert_id") && !obj.get("expert_id").isJsonNull()) {
                    expert.setExpertId(obj.get("expert_id").getAsInt());
                }
                if (obj.has("company_name") && !obj.get("company_name").isJsonNull()) {
                    expert.setCompanyName(obj.get("company_name").getAsString());
                }
                if (obj.has("profile_image") && !obj.get("profile_image").isJsonNull()) {
                    expert.setProfileImage(obj.get("profile_image").getAsString());
                }
                if (obj.has("avg_rating") && !obj.get("avg_rating").isJsonNull()) {
                    expert.setAvgRating(obj.get("avg_rating").getAsDouble());
                }
                if (obj.has("review_count") && !obj.get("review_count").isJsonNull()) {
                    expert.setReviewCount(obj.get("review_count").getAsInt());
                }
                if (obj.has("reservation_count") && !obj.get("reservation_count").isJsonNull()) {
                    expert.setReservationCount(obj.get("reservation_count").getAsInt());
                }
                if (obj.has("career_years") && !obj.get("career_years").isJsonNull()) {
                    expert.setCareerYears(obj.get("career_years").getAsInt());
                }
                if (obj.has("description") && !obj.get("description").isJsonNull()) {
                    expert.setDescription(obj.get("description").getAsString());
                }
                if (obj.has("service_info") && !obj.get("service_info").isJsonNull()) {
                    expert.setServiceInfo(obj.get("service_info").getAsString());
                }

                if (expert.getExpertId() <= 0) {
                    Toast.makeText(ExpertProfileActivity.this,
                            "전문가 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "expertId <= 0 → 서버 응답 확인 필요");
                    return;
                }

                // ✅ UI 반영
                updateUI(expert);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ExpertProfileActivity.this,
                        "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Retrofit 실패: " + t.getMessage());
            }
        });
    }

    /** ExpertWithStats DTO → UI 반영 */
    private void updateUI(ExpertWithStats expert) {
        tvCompanyName.setText(expert.getCompanyName());
        tvDescription.setText(expert.getDescription() != null ? expert.getDescription() : "설명 없음");
        tvRegion.setText(expert.getServiceInfo() != null ? expert.getServiceInfo() : "지역 정보 없음");
        tvRating.setText("⭐ " + expert.getAvgRating() + " (" + expert.getReviewCount() + ")");

        tvHireCount.setText("고용 " + expert.getReservationCount() + "회");
        tvRatingStat.setText("⭐ " + expert.getAvgRating() + " (" + expert.getReviewCount() + ")");
        tvCareerStat.setText("총 경력 " + expert.getCareerYears() + "년");

        if (expert.getProfileImage() != null && !expert.getProfileImage().isEmpty()) {
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
