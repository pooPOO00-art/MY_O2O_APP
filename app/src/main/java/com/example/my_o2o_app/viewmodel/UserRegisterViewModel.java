package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_o2o_app.repository.UserRepository;

public class UserRegisterViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();

    public UserRegisterViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public void registerUser(String userId, String password, String name, String phone) {
        userRepository.registerUser(userId, password, name, phone, registerSuccess::postValue);
    }
}
