package com.example.my_o2o_app.view.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.ChatListAdapter;
import com.example.my_o2o_app.model.ChatRoom;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private int userId;
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;

    // ✅ newInstance로 userId 전달
    public static ChatListFragment newInstance(int userId) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_USER_ID, 1); // 기본값 1
        }

        recyclerView = view.findViewById(R.id.recyclerViewChatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatListAdapter(chatRoom -> {
            // ✅ 채팅방 클릭 → ChatFragment로 이동 (userId 전달)
            ChatFragment fragment = ChatFragment.newInstance(
                    chatRoom.getRoomId(),
                    chatRoom.getExpertName(),
                    userId
            );
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        loadChatList();
        return view;
    }

    /** 서버에서 채팅방 목록 로드 */
    private void loadChatList() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getChatList(userId)
                .enqueue(new Callback<List<ChatRoom>>() {
                    @Override
                    public void onResponse(Call<List<ChatRoom>> call, Response<List<ChatRoom>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.submitList(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatRoom>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
}
