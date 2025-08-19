package com.example.my_o2o_app.view.expert;

/**
 * 파일 설명: 전문가(고수) 상세 프로필 화면
 * - '고수찾기'에서 들어온 경우: 하단 고정 버튼 = "견적 요청"
 * - '받은견적'에서 들어온 경우: 하단 고정 버튼 = "채팅하기"
 * - '채팅하기' 클릭 시: 프래그먼트를 붙이지 않고 전용 채팅 액티비티(ChatRoomActivity)로 이동
 *
 * 개발자 메모(중요):
 * - 과거 겹침 이슈 원인: fragmentContainerExpert에 ChatFragment를 add/replace 하던 코드
 * - 본 수정본에서는 해당 코드 전부 제거. 화면 전환은 Intent 로만 처리
 * - UI 텍스트 결합은 추후 strings.xml로 옮기는 것을 권장(TODO 주석)
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.my_o2o_app.view.chat.ChatRoomActivity;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertProfileActivity extends AppCompatActivity {

    private static final String TAG = "ExpertProfileActivity";

    // 데이터
    private ExpertWithStats expert;
    private int expertId;
    private int userId;     // 받은견적 → 프로필로 들어올 때 전달됨
    private String from;    // "find_expert" or "estimate"

    // 뷰
    private ImageView ivProfile;
    private TextView tvCompanyName, tvDescription, tvRegion;
    private TextView tvHireCount, tvRatingStat, tvCareerStat;
    private FrameLayout layoutBottomFixed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);

        // 1) 인텐트 파라미터 수신
        expertId = getIntent().getIntExtra("expertId", -1);
        userId   = getIntent().getIntExtra("userId", -1);   // '받은견적' 화면에서 넘겨줌
        from     = getIntent().getStringExtra("from");

        Log.i(TAG, "onCreate: expertId=" + expertId + ", userId=" + userId + ", from=" + from);

        if (expertId <= 0) {
            Toast.makeText(this, "전문가 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initView();
        setupBottomButtons();
        loadExpertProfileFromServer();
    }

    /** 기능: findViewById 및 기본 상태 설정 */
    private void initView() {
        ivProfile      = findViewById(R.id.ivProfile);
        tvCompanyName  = findViewById(R.id.tvCompanyName);
        tvDescription  = findViewById(R.id.tvDescription);
        tvRegion       = findViewById(R.id.tvRegion);
        tvHireCount    = findViewById(R.id.tvHireCount);
        tvRatingStat   = findViewById(R.id.tvRatingStat);
        tvCareerStat   = findViewById(R.id.tvCareerStat);
        layoutBottomFixed = findViewById(R.id.layoutBottomFixed);

        // 과거 오버레이 컨테이너는 사용하지 않음(겹침 원인). XML에 있더라도 항상 숨김.
        View overlay = findViewById(R.id.fragmentContainerExpert);
        if (overlay != null) overlay.setVisibility(View.GONE);
    }

    /** 기능: 하단 고정 버튼 영역을 상황에 맞게 inflate & 클릭 핸들러 연결 */
    private void setupBottomButtons() {
        LayoutInflater inflater = LayoutInflater.from(this);

        if ("find_expert".equalsIgnoreCase(from)) {
            inflater.inflate(R.layout.layout_bottom_request, layoutBottomFixed, true);

            Button btnRequest = layoutBottomFixed.findViewById(R.id.btnRequestEstimate);
            btnRequest.setOnClickListener(v -> {
                if (expert == null) {
                    Toast.makeText(this, "전문가 정보를 불러오는 중입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, EstimateRequestActivity.class);
                intent.putExtra("expertId", expertId);                 // 직접 견적
                intent.putExtra("categoryId", expert.getCategoryId()); // 질문 로딩용
                intent.putExtra("categoryName", expert.getCompanyName());
                startActivity(intent);
            });

        } else if ("estimate".equalsIgnoreCase(from)) {
            inflater.inflate(R.layout.layout_bottom_chat, layoutBottomFixed, true);

            Button btnChat = layoutBottomFixed.findViewById(R.id.btnChatExpert);
            btnChat.setOnClickListener(v -> {
                if (expert == null) {
                    Toast.makeText(this, "전문가 정보를 불러오는 중입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userId <= 0) {
                    Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                openChatRoom(userId, expertId, expert.getCompanyName());
            });

        } else {
            Log.w(TAG, "from 값이 없어 하단 버튼을 표시하지 않습니다.");
        }
    }

    /**
     * 기능: 채팅방 생성/조회 후 ChatRoomActivity로 화면 '이동'
     * - 이 메서드에서는 절대 프래그먼트 트랜잭션(add/replace)을 하지 않음(겹침 방지)
     */
    private void openChatRoom(int myUserId, int targetExpertId, String expertName) {
        ApiService api = ApiClient.getClient().create(ApiService.class);

        JsonObject body = new JsonObject();
        body.addProperty("user_id", myUserId);
        body.addProperty("expert_id", targetExpertId);

        api.createChatRoom(body).enqueue(new Callback<>() { // 다이아몬드 연산자 사용
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> resp) {
                if (!resp.isSuccessful() || resp.body() == null || !resp.body().get("success").getAsBoolean()) {
                    Toast.makeText(ExpertProfileActivity.this, "채팅방 생성 실패", Toast.LENGTH_SHORT).show();
                    return;
                }
                int roomId = resp.body().get("room_id").getAsInt();

                Intent i = new Intent(ExpertProfileActivity.this, ChatRoomActivity.class);
                i.putExtra("roomId", roomId);
                i.putExtra("userId", myUserId);
                i.putExtra("expertName", expertName);
                i.putExtra("expertProfile", expert.getProfileImage());  // ✅ 추가
                startActivity(i); // ✅ 전용 액티비티로 이동 (겹침 없음)
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ExpertProfileActivity.this, "네트워크 오류로 채팅 시작 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 기능: 서버에서 전문가 프로필 조회 */
    private void loadExpertProfileFromServer() {
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getExpertProfile(expertId).enqueue(new Callback<>() { // 다이아몬드 연산자
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ExpertProfileActivity.this, "전문가 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                JsonObject root = response.body();
                if (!root.has("expert") || root.get("expert").isJsonNull()) {
                    Toast.makeText(ExpertProfileActivity.this, "전문가 데이터가 비어있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject obj = root.getAsJsonObject("expert");

                expert = new ExpertWithStats();
                expert.setExpertId(obj.has("expert_id")      && !obj.get("expert_id").isJsonNull()      ? obj.get("expert_id").getAsInt()          : 0);
                expert.setCompanyName(obj.has("company_name") && !obj.get("company_name").isJsonNull()   ? obj.get("company_name").getAsString()    : "");
                expert.setProfileImage(obj.has("profile_image") && !obj.get("profile_image").isJsonNull()? obj.get("profile_image").getAsString()   : "");
                expert.setAvgRating(obj.has("avg_rating")     && !obj.get("avg_rating").isJsonNull()     ? obj.get("avg_rating").getAsDouble()      : 0.0);
                expert.setReviewCount(obj.has("review_count") && !obj.get("review_count").isJsonNull()   ? obj.get("review_count").getAsInt()       : 0);
                expert.setReservationCount(obj.has("reservation_count") && !obj.get("reservation_count").isJsonNull() ? obj.get("reservation_count").getAsInt() : 0);
                expert.setCareerYears(obj.has("career_years") && !obj.get("career_years").isJsonNull()   ? obj.get("career_years").getAsInt()       : 0);
                expert.setDescription(obj.has("description")  && !obj.get("description").isJsonNull()    ? obj.get("description").getAsString()     : "설명 없음");
                expert.setServiceInfo(obj.has("service_info") && !obj.get("service_info").isJsonNull()   ? obj.get("service_info").getAsString()    : "지역 정보 없음");
                expert.setCategoryId(obj.has("category_id")   && !obj.get("category_id").isJsonNull()    ? obj.get("category_id").getAsInt()        : 0);

                updateUI(expert);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ExpertProfileActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 기능: DTO → 화면 반영 */
    private void updateUI(ExpertWithStats e) {
        // TODO: 문자열 결합은 strings.xml의 포맷 문자열을 사용하는 것이 권장됨(경고 제거 목적)
        tvCompanyName.setText(e.getCompanyName());
        tvDescription.setText(e.getDescription());
        tvRegion.setText(e.getServiceInfo());
        tvHireCount.setText(e.getReservationCount() + "회");
        tvRatingStat.setText("⭐ " + e.getAvgRating() + " (" + e.getReviewCount() + ")");
        tvCareerStat.setText(e.getCareerYears() + "년");

        if (!e.getProfileImage().isEmpty()) {
            Glide.with(this)
                    .load(e.getProfileImage())
                    .placeholder(R.drawable.ic_placeholder)
                    .circleCrop()
                    .into(ivProfile);
        } else {
            ivProfile.setImageResource(R.drawable.ic_placeholder);
        }
    }
}
