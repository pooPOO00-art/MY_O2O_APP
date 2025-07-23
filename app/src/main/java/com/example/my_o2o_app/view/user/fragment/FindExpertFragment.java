// FindExpertFragment.java
// 고수찾기 탭에서 검색 + 필터 + 전문가 리스트 출력

package com.example.my_o2o_app.view.user.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.ExpertAdapter;
import com.example.my_o2o_app.viewmodel.ExpertListViewModel;

public class FindExpertFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvExperts;
    private ExpertAdapter expertAdapter;
    private ExpertListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_find_expert, container, false);

        // UI 초기화
        etSearch = view.findViewById(R.id.etExpertSearch);
        rvExperts = view.findViewById(R.id.rvExperts);

        rvExperts.setLayoutManager(new LinearLayoutManager(getContext()));
        expertAdapter = new ExpertAdapter();
        rvExperts.setAdapter(expertAdapter);

        // ViewModel 연결
        viewModel = new ViewModelProvider(this).get(ExpertListViewModel.class);
        viewModel.getExpertList().observe(getViewLifecycleOwner(), expertAdapter::submitList);

        // 데이터 로드
        viewModel.loadExperts(); // categoryId, keyword 등은 나중에 파라미터화

        return view;
    }
}
