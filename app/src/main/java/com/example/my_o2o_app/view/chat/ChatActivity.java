/**
 * ChatActivity.java
 * 기능: 전문가와의 채팅 화면 (현재는 채팅 목록 Fragment만 표시)
 */

package com.example.my_o2o_app.view.chat;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.my_o2o_app.R;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // ✅ ChatListFragment 표시
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.chatContainer, new ChatListFragment())
                    .commit();
        }
    }
}
