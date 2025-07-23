package com.example.my_o2o_app.repository;

import com.example.my_o2o_app.model.Category;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private final ApiService apiService;

    public CategoryRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void fetchCategories(Consumer<List<Category>> callback) {
        Call<JsonObject> call = apiService.getCategoryList();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonArray arr = response.body().getAsJsonArray("categories");
                    List<Category> result = new ArrayList<>();

                    for (JsonElement el : arr) {
                        JsonObject obj = el.getAsJsonObject();
                        Category category = new Category(
                                obj.get("category_id").getAsInt(),
                                obj.has("parent_id") && !obj.get("parent_id").isJsonNull()
                                        ? obj.get("parent_id").getAsInt()
                                        : null,
                                obj.get("category_name").getAsString()
                        );
                        result.add(category);
                    }
                    callback.accept(result);
                } else {
                    callback.accept(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.accept(Collections.emptyList());
            }
        });
    }
}
