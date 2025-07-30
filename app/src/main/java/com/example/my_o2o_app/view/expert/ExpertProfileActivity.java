// ExpertProfileActivity.java
// 전문가 상세 프로필 화면
// - DB에서 expertId 기반으로 전문가 정보 조회
// - 업체명, 프로필, 설명, 지역, 후기 표시
// - 하단: 채팅하기 / 예약하기 버튼

package com.example.my_o2o_app.view.expert;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.my_o2o_app.R;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertProfileActivity extends AppCompatActivity {

    private static final String TAG = "ExpertProfileActivity";

    // ✅ View 참조
    private ImageView ivProfile;
    private TextView tvCompanyName, tvDescription, tvRegion, tvRating;
    private Button btnChat, btnReserve;

    // ✅ 전달받은 전문가 ID
    private int expertId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);

        // 1️⃣ expertId 수신
        expertId = getIntent().getIntExtra("expertId", -1);
        Log.d(TAG, "받은 expertId=" + expertId);

        if (expertId == -1) {
            Toast.makeText(this, "전문가 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2️⃣ View 초기화
        initView();

        // 3️⃣ 버튼 리스너 설정
        initListeners();

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
        btnChat = findViewById(R.id.btnChat);
        btnReserve = findViewById(R.id.btnReserve);
    }

    /** 버튼 클릭 이벤트 */
    private void initListeners() {
        btnChat.setOnClickListener(v -> {
            Log.d(TAG, "채팅하기 클릭 (expertId=" + expertId + ")");
            Toast.makeText(this, "채팅 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        btnReserve.setOnClickListener(v -> {
            Log.d(TAG, "예약하기 클릭 (expertId=" + expertId + ")");
            Toast.makeText(this, "예약 기능 준비 중", Toast.LENGTH_SHORT).show();
        });
    }

    /** DB에서 전문가 프로필 조회 (Retrofit) */
    private void loadExpertProfileFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getExpertProfile(expertId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject expertJson = response.body().getAsJsonObject("expert");

                    // ✅ JSON 데이터 UI 반영
                    tvCompanyName.setText(expertJson.get("company_name").getAsString());
                    tvDescription.setText(expertJson.get("description").isJsonNull() ? "" : expertJson.get("description").getAsString());
                    tvRegion.setText(expertJson.get("company_address").isJsonNull() ? "" : expertJson.get("company_address").getAsString());
                    tvRating.setText("⭐ 리뷰 기능 준비 중");

                    // ✅ 프로필 이미지 로드
                    if (!expertJson.get("profile_image").isJsonNull()) {
                        Glide.with(ExpertProfileActivity.this)
                                .load(expertJson.get("profile_image").getAsString())
                                .placeholder(R.drawable.ic_placeholder)
                                .circleCrop()
                                .into(ivProfile);
                    } else {
                        ivProfile.setImageResource(R.drawable.ic_placeholder);
                    }

                } else {
                    Toast.makeText(ExpertProfileActivity.this,
                            "전문가 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ExpertProfileActivity.this,
                        "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
