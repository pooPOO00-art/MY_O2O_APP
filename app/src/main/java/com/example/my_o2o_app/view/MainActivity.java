// 기능: 앱 실행 시 사용자 OR 고수를 구분 로그인 화면 이동
package com.example.my_o2o_app.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.my_o2o_app.databinding.ActivityMainBinding;
import com.example.my_o2o_app.view.user.UserLoginActivity;
import com.example.my_o2o_app.view.expert.ExpertLoginActivity;

public class MainActivity extends AppCompatActivity {

    //private ActivityMainBinding binding; // ViewBinding 객체 : 지역 변수로 변경

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding을 통해 XML inflate 및 세팅
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 버튼 클릭 이벤트 (ViewBinding 방식)
        binding.btnUserLogin.setOnClickListener(v ->
                startActivity(new Intent(this, UserLoginActivity.class)));

        binding.btnExpertLogin.setOnClickListener(v ->
                startActivity(new Intent(this, ExpertLoginActivity.class)));
    }
}
