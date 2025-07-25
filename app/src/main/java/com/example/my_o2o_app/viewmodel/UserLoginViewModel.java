// UserLoginViewModel.java
// 사용자 로그인 로직을 처리하는 ViewModel 클래스
// 현재는 더미 데이터를 통해 로그인 검증. 추후 Repository와 연동 예정.
package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_o2o_app.repository.UserRepository;
import com.example.my_o2o_app.model.User;

public class UserLoginViewModel extends ViewModel {

    private final UserRepository repository = new UserRepository();
    private final MutableLiveData<User> loginUser = new MutableLiveData<>();

    public LiveData<User> getLoginUser() {
        return loginUser;
    }

    public void login(String id, String password) {
        repository.loginUser(id, password, user -> {
            loginUser.postValue(user);  // 성공 시 User, 실패 시 null
        });
    }
}

