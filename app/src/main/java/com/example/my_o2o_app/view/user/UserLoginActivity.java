// UserLoginActivity.java
// 사용자 로그인 화면을 구성하고 로그인 이벤트를 처리하는 액티비티 클래스

package com.example.my_o2o_app.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_o2o_app.databinding.ActivityUserLoginBinding;
import com.example.my_o2o_app.viewmodel.UserLoginViewModel;

public class UserLoginActivity extends AppCompatActivity {
    private ActivityUserLoginBinding binding;

    // 로그인 처리 뷰모델
    private UserLoginViewModel viewModel; //이 뷰모델 처리는 아직 잘모르겠음

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewModel new가 아닌 Porvider로 가져와서 화면 회전시에도 동일 인스턴스 유지
        viewModel = new ViewModelProvider(this).get(UserLoginViewModel.class);


        // 로그인 결과를 관찰해서 로그인 성공/실패 처리 , postvalue(user)의 변화
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

        //  로그인 버튼 클릭 시 ViewModel을 통해 로그인 시도
        binding.btnUserLogin.setOnClickListener(v -> {
            String userId = binding.etUserId.getText().toString().trim();
            String password = binding.etUserPassword.getText().toString().trim();

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 중요부분 로그인 로직 호출 (결과는 LiveData<User>로 전달됨)
            viewModel.login(userId, password);
        });

        binding.tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this,UserRegisterActivity.class)));
    }
}
