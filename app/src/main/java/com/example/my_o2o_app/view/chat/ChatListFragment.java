// ChatListFragment.java
// 채팅 목록 프래그먼트 (임시 더미)
// - 추후 채팅방 RecyclerView로 확장 예정

package com.example.my_o2o_app.view.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // ✅ 임시 화면: 단순 TextView 반환
        TextView tv = new TextView(getContext());
        tv.setText("채팅 목록 화면 (개발 예정)");
        tv.setTextSize(18f);
        tv.setPadding(40, 40, 40, 40);

        return tv;
    }
}
