// UserLoginActivity.java
// 사용자 로그인 화면을 구성하고 로그인 이벤트를 처리하는 액티비티 클래스

package com.example.my_o2o_app.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.viewmodel.UserLoginViewModel;
import com.example.my_o2o_app.model.User;

public class UserLoginActivity extends AppCompatActivity {

    // 사용자 ID 입력 필드
    private EditText userIdEditText;

    // 비밀번호 입력 필드
    private EditText passwordEditText;

    // 로그인 버튼
    private Button loginButton;

    // 회원가입 텍스트 → 클릭 시 회원가입 화면으로 이동
    private TextView registerText;

    // 로그인 처리 뷰모델
    private UserLoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // XML 레이아웃 설정
        setContentView(R.layout.activity_user_login);

        // ViewModel 초기화
        viewModel = new UserLoginViewModel();

        // XML 뷰 연결
        userIdEditText = findViewById(R.id.etUserId);
        passwordEditText = findViewById(R.id.etUserPassword);
        loginButton = findViewById(R.id.btnUserLogin);
        registerText = findViewById(R.id.tvGoToRegister);

        // ✅ [1] 로그인 결과를 관찰해서 로그인 성공/실패 처리
        viewModel.getLoginUser().observe(this, user -> {
            if (user != null) {
                // 로그인 성공 시
                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                // 사용자 메인화면으로 이동 + 이름 전달
                Intent intent = new Intent(this, UserHomeActivity.class);
                intent.putExtra("userName", user.getName());
                startActivity(intent);

                // 로그인 화면 종료 (뒤로가기 방지)
                finish();
            } else {
                // 로그인 실패 시
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ [2] 로그인 버튼 클릭 시 ViewModel을 통해 로그인 시도
        loginButton.setOnClickListener(v -> {
            String userId = userIdEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 로그인 로직 호출 (결과는 LiveData<User>로 전달됨)
            viewModel.login(userId, password);
        });

        // ✅ [3] 회원가입 텍스트 클릭 시 회원가입 화면으로 이동
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
            startActivity(intent);
        });
    }
}
