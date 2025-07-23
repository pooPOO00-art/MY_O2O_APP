// CategoryDetailViewModel.java
// 카테고리 목록 전체를 관리하고, 선택된 상위 카테고리 기준으로 하위 카테고리를 필터링하는 ViewModel

package com.example.my_o2o_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_o2o_app.model.Category;
import com.example.my_o2o_app.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailViewModel extends ViewModel {

    private final CategoryRepository repository = new CategoryRepository();
    private List<Category> allCategories = new ArrayList();  // 전체 데이터 (캐시)
    private final MutableLiveData<List<Category>> topCategories = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> subCategories = new MutableLiveData<>();

    private Integer selectedTopCategoryId = null;

    public LiveData<List<Category>> getTopCategories() {
        return topCategories;
    }

    public LiveData<List<Category>> getSubCategories() {
        return subCategories;
    }

    // ✅ 초기 데이터 불러오기 및 상위 카테고리 필터
    public void loadCategories(int initialTopCategoryId) {
        repository.fetchCategories(result -> {
            allCategories = result;

            // 상위 카테고리 필터
            List<Category> top = new ArrayList<>();
            for (Category c : result) {
                if (c.getParent_id() == null) {
                    top.add(c);
                }
            }
            topCategories.postValue(top);

            // 초기 선택값 설정
            if (initialTopCategoryId != -1) {
                selectTopCategory(initialTopCategoryId);
            } else if (!top.isEmpty()) {
                selectTopCategory(top.get(0).getCategory_id()); // 기본 첫 번째 선택
            }
        });
    }

    // ✅ 선택된 상위 카테고리 ID 설정 및 하위 카테고리 필터링
    public void selectTopCategory(int categoryId) {
        this.selectedTopCategoryId = categoryId;

        List<Category> sub = new ArrayList<>();
        for (Category c : allCategories) {
            if (c.getParent_id() != null && c.getParent_id().equals(categoryId)) {
                sub.add(c);
            }
        }
        subCategories.postValue(sub);
    }

    public Integer getSelectedTopCategoryId() {
        return selectedTopCategoryId;
    }
}
