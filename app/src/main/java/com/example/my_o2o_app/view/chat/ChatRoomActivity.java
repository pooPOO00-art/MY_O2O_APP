// ChatRoomActivity.java
package com.example.my_o2o_app.view.chat;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.my_o2o_app.R;

public class ChatRoomActivity extends AppCompatActivity {

    private int roomId;
    private int userId;
    private int expertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room); // 너가 만든 XML로 연결

        // Intent 데이터 받기
        roomId = getIntent().getIntExtra("roomId", -1);
        userId = getIntent().getIntExtra("userId", -1);
        expertId = getIntent().getIntExtra("expertId", -1);

        // 예시: 텍스트 뿌리기
        TextView tvInfo = findViewById(R.id.tvInfo);
        tvInfo.setText("채팅방 입장\nroomId=" + roomId + "\nuserId=" + userId + "\nexpertId=" + expertId);
    }
}
