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

public class UserHomeActivity extends AppCompatActivity {

    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // ✅ 사용자 이름 표시
        tvUserName = findViewById(R.id.tvUserName);
        String userName = getIntent().getStringExtra("userName");
        if (userName != null) {
            tvUserName.setText(userName + "님");
        }

        // ✅ 초기 화면: 홈
        loadFragment(new HomeFragment());

        // ✅ 바텀 네비게이션 처리
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_search) {
                loadFragment(new FindExpertFragment());
                return true;
            }
            // TODO: 추가 탭 처리
            return false;
        });
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
