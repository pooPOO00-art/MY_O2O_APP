package com.example.my_o2o_app.repository;

import android.util.Log;

import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;
import com.google.gson.JsonObject;
import com.example.my_o2o_app.model.User;

import java.util.function.Consumer;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService;

    public UserRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }
    public void loginUser(String id, String password, Consumer<User> callback) {
        JsonObject body = new JsonObject();
        body.addProperty("id", id);
        body.addProperty("password", password);

        Call<JsonObject> call = apiService.loginUser(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject json = response.body();

                    Log.d("LoginDebug", "서버 응답 JSON: " + json.toString());


                    boolean success = json.get("success").getAsBoolean();
                    if (success && json.has("user")) {
                        JsonObject userJson = json.getAsJsonObject("user");
                        String id = userJson.get("id").getAsString();
                        String name = userJson.get("name").getAsString();
                        String password = userJson.get("password").getAsString();
                        String phone = userJson.get("phone").getAsString();
                        int userId = userJson.get("userId").getAsInt();

                        User user = new User(userId, id, password, name, phone);
                        callback.accept(user);
                        return;
                    }
                }
                callback.accept(null);
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                callback.accept(null);
            }
        });
    }



    // 사용자 회원가입 요청 (서버에 JsonObject 전송)
    public void registerUser(String id, String password, String name, String phoneNumber, Consumer<Boolean> callback) {
        JsonObject body = new JsonObject();
        body.addProperty("id", id);
        body.addProperty("password", password);
        body.addProperty("name", name);
        body.addProperty("phone_number", phoneNumber);

        Call<JsonObject> call = apiService.registerUser(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("UserRegister", "✅ 서버 응답 성공: " + response.body());
                    callback.accept(true);
                } else {
                    Log.e("UserRegister", "❌ 서버 응답 실패: " + response.code());
                    callback.accept(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("UserRegister", "🚨 통신 실패: " + t.getMessage());
                callback.accept(false);
            }
        });
    }
}
