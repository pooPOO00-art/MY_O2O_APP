// UserRegisterActivity.java
// 사용자의 회원가입 화면 Activity 클래스
// 사용자로부터 ID, 비밀번호, 이름, 전화번호를 입력받고 회원가입 처리 요청을 ViewModel에 전달함

package com.example.my_o2o_app.view.user;

import android.content.Intent; // intent 클래스 사용하기
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.my_o2o_app.R;
import com.example.my_o2o_app.viewmodel.UserRegisterViewModel;
import com.example.my_o2o_app.view.MainActivity;


public class UserRegisterActivity extends AppCompatActivity {

    // 사용자 입력 필드
    private EditText etUserId, etUserPassword, etUserName, etUserPhone;
    private Button btnRegister;

    // ViewModel 선언
    private UserRegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);  // 레이아웃 연결

        // 1. ViewModel 초기화
        viewModel = new UserRegisterViewModel();

        // 2. 뷰 요소들 연결
        etUserId = findViewById(R.id.etUserId);
        etUserPassword = findViewById(R.id.etUserPassword);
        etUserName = findViewById(R.id.etUserName);
        etUserPhone = findViewById(R.id.etUserPhone);
        btnRegister = findViewById(R.id.btnUserRegister);

        viewModel.getRegisterSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class)); // Main으로 이동
                finish(); // 현재 액티비티 종료
            } else {
                Toast.makeText(this, "회원가입 실패. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. 회원가입 버튼 클릭 시 처리
        btnRegister.setOnClickListener(v -> {
            // 사용자 입력값 가져오기
            String userId = etUserId.getText().toString().trim();
            String password = etUserPassword.getText().toString().trim();
            String name = etUserName.getText().toString().trim();
            String phone = etUserPhone.getText().toString().trim();

            // 입력값 유효성 검사
            if (userId.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // ViewModel을 통해 회원가입 처리 요청 (비동기)
            viewModel.registerUser(userId, password, name, phone);

            // 서버 응답 결과는 Logcat이나 DB에서 확인
            //Toast.makeText(this, "회원가입 요청 전송됨 (서버 응답 확인)", Toast.LENGTH_SHORT).show();
        });

    }
}
