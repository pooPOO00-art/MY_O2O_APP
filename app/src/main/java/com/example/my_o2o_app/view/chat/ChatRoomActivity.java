// ChatRoomActivity.java
// 기능: 채팅 전용 화면(Activity)
// - 상단 MaterialToolbar를 액션바로 등록해 제목(업체명) + 뒤로가기 아이콘 제공
// - 중앙 컨테이너(chatContainer)에 ChatFragment를 호스팅하여 채팅 UI 렌더링
// - Intent로 전달받은 roomId/userId/expertName/expertProfile 사용
// 개발자 메모:
// 1) 레이아웃 activity_chat_room.xml 에 반드시 toolbar(id=toolbar)와
//    FrameLayout(id=chatContainer)가 존재해야 함.
// 2) Manifest에 본 액티비티가 등록되어 있어야 함.
// 3) expertProfile은 상대방 아바타 노출용(없으면 null 전달 가능)

package com.example.my_o2o_app.view.chat;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.my_o2o_app.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ChatRoomActivity extends AppCompatActivity {

    // --- 인텐트로 전달받는 값 ---
    private int roomId;
    private int userId;
    private String expertName;
    private String expertProfile; // 상대방 프로필 URL(옵션)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room); // toolbar + chatContainer 포함 레이아웃

        // 1) 인텐트 파라미터 수신
        roomId        = getIntent().getIntExtra("roomId", -1);
        userId        = getIntent().getIntExtra("userId", -1);
        expertName    = getIntent().getStringExtra("expertName");
        expertProfile = getIntent().getStringExtra("expertProfile"); // 없으면 null

        // 간단 유효성 체크(개발 중 편의용)
        if (roomId <= 0 || userId <= 0) {
            // 잘못 들어온 경우: 그냥 뒤로가기
            finish();
            return;
        }

        // 2) 상단 툴바를 액션바로 등록 + 제목/뒤로가기 설정
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // 제목: 업체명(없으면 "채팅")
            getSupportActionBar().setTitle(expertName != null ? expertName : "채팅");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 ←
        }
        // 네비게이션 아이콘(←) 클릭 시 뒤로가기 동작
        // ChatRoomActivity.java (툴바 네비 클릭 리스너)
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());


        // 3) 프래그먼트 최초 1회만 부착
        if (savedInstanceState == null) {
            // 프로필 URL까지 넘겨서 어댑터에서 아바타 노출 가능
            ChatFragment fragment = ChatFragment.newInstance(roomId, expertName, userId, expertProfile);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.chatContainer, fragment)
                    .commit();
        }
    }

    // 액션바 홈(←) 클릭 처리(백스택 pop)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
