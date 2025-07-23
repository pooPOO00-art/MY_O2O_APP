package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_o2o_app.model.Category;
import com.example.my_o2o_app.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository repository = new CategoryRepository();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void loadCategories() {
        repository.fetchCategories(result -> {
            categories.postValue(result);
        });
    }
}
