// UserHomeActivity.java
// 중앙 fragmentContainer에 Fragment를 교체하는 역할만 함

package com.example.my_o2o_app.view.user;

import android.os.Bundle;
import android.widget.TextView;
import com.example.my_o2o_app.view.user.fragment.FindExpertFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.my_o2o_app.R;
import com.example.my_o2o_app.view.user.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.my_o2o_app.view.estimate.EstimateListFragment;
import com.example.my_o2o_app.view.chat.ChatListFragment;
import com.example.my_o2o_app.view.reservation.ReservationListFragment;

// ✅ UserHomeActivity.java
// - 로그인 시 userId를 Intent로 받아서 전체 Fragment에 전달

public class UserHomeActivity extends AppCompatActivity {

    private TextView tvUserName;
    private int userId; // ✅ 로그인한 사용자 ID 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        tvUserName = findViewById(R.id.tvUserName);

        // ✅ 로그인 시 전달받은 정보
        userId = getIntent().getIntExtra("userId", 1); // 기본값 1
        String userName = getIntent().getStringExtra("userName");
        if (userName != null) tvUserName.setText(userName + "님");

        // 초기 화면: 홈
        loadFragment(new HomeFragment());

        // ✅ 하단 네비게이션
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (id == R.id.nav_search) {
                loadFragment(new FindExpertFragment());
            } else if (id == R.id.nav_quotes) {
                loadFragment(EstimateListFragment.newInstance(userId));
            } else if (id == R.id.nav_chat) {
                // ✅ userId를 Bundle로 전달
                ChatListFragment chatListFragment = ChatListFragment.newInstance(userId);
                loadFragment(chatListFragment);
            } else if (id == R.id.nav_reservation) {
                loadFragment(new ReservationListFragment());
            }

            return true;
        });
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}

