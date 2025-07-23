/**
 * MainActivity.java
 * 기능: 앱 실행 시 사용자와 고수를 구분하여 로그인 화면으로 이동시키는 진입점 역할을 하는 Activity
 */

package com.example.my_o2o_app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.view.user.UserLoginActivity;
import com.example.my_o2o_app.view.expert.ExpertLoginActivity;

public class MainActivity extends AppCompatActivity {

    // 사용자, 고수 로그인 버튼 객체 선언
    private Button btnUserLogin, btnExpertLogin;

    /**
     * onCreate()
     * 기능: 액티비티가 생성될 때 호출되며, 레이아웃 초기화 및 버튼 클릭 이벤트를 등록한다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // XML 레이아웃 파일 연결

        // 버튼 컴포넌트 ID 매핑
        btnUserLogin = findViewById(R.id.btnUserLogin);
        btnExpertLogin = findViewById(R.id.btnExpertLogin);

        // 사용자 로그인 버튼 클릭 시 -> UserLoginActivity로 이동
        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            /**
             * onClick()
             * 기능: 사용자 로그인 버튼 클릭 시 사용자 로그인 액티비티로 화면 전환
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivity(intent);  // 사용자 로그인 화면으로 이동
            }
        });

        // 고수 로그인 버튼 클릭 시 -> ExpertLoginActivity로 이동
        btnExpertLogin.setOnClickListener(new View.OnClickListener() {
            /**
             * onClick()
             * 기능: 고수 로그인 버튼 클릭 시 고수 로그인 액티비티로 화면 전환
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExpertLoginActivity.class);
                startActivity(intent);  // 고수 로그인 화면으로 이동
            }
        });
    }
}
