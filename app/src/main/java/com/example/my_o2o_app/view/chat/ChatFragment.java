// ChatFragment.java
// 전문가와 사용자 간 채팅 기능 Fragment (소켓 연결, 메시지 전송/수신)

package com.example.my_o2o_app.view.chat;


// ChatFragment.java 상단 import (추가)
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.my_o2o_app.model.MessagesResponse;  // ✅ 추가


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.inputmethod.EditorInfo;  // ✅ 추가!

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.ChatMessageAdapter;
import com.example.my_o2o_app.model.ChatMessage;

import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";
    private static final String ARG_ROOM_ID = "room_id";
    private static final String ARG_EXPERT_NAME = "expert_name";
    private static final String ARG_USER_ID = "user_id";

    private int roomId;
    private int myUserId;
    private String expertName;

    private Socket mSocket;
    private RecyclerView recyclerView;
    private ChatMessageAdapter adapter;
    private EditText etMessage;

    /** Fragment 생성 시 roomId, expertName, userId 전달 */
    public static ChatFragment newInstance(int roomId, String expertName, int userId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROOM_ID, roomId);
        args.putInt(ARG_USER_ID, userId);
        args.putString(ARG_EXPERT_NAME, expertName);
        fragment.setArguments(args);
        return fragment;
    }

    /** Fragment 생성: 소켓 연결 시작 */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            roomId = getArguments().getInt(ARG_ROOM_ID);
            expertName = getArguments().getString(ARG_EXPERT_NAME);
            myUserId = getArguments().getInt(ARG_USER_ID, 1);
        }

        // ✅ 소켓 연결을 별도 쓰레드에서 실행
        new Thread(() -> {
            try {
                Log.i(TAG, "소켓 연결 시도...");
                mSocket = IO.socket("http://172.29.12.37:5001"); // ⚠️ 서버 IP 확인 필요
                mSocket.connect();

                JSONObject joinData = new JSONObject();
                joinData.put("room_id", roomId);
                joinData.put("user_id", myUserId);
                mSocket.emit("join_room", joinData);

                Log.i(TAG, "소켓 연결 및 방 입장 완료: roomId=" + roomId);

                // ✅ 연결 완료 후 리스너 등록
                mSocket.on("receive_message", onReceiveMessage);

            } catch (Exception e) {
                Log.e(TAG, "소켓 연결 실패: " + e.getMessage());
            }
        }).start();
    }

    /** 화면 UI 구성 */
    // ChatFragment.java
// 기능: 채팅 화면 UI 구성 및 소켓 연결. 리스트를 하단 정렬(stackFromEnd)로 설정해 최근 메시지가 아래에 표시되도록 함.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewChat);

        // ✅ 리스트를 "아래부터" 쌓이게 설정 (최근 메시지가 하단)
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true); // 최신 메시지 하단 정렬
        recyclerView.setLayoutManager(lm);

        adapter = new ChatMessageAdapter(new ArrayList<>());
        adapter.setMyUserId(myUserId);
        recyclerView.setAdapter(adapter);

        loadHistory();

        etMessage = view.findViewById(R.id.etMessage);
        Button btnSend = view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sendMessage());

        // (선택) 키보드에서 엔터로도 전송
        // import android.view.inputmethod.EditorInfo;
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        Log.i(TAG, "ChatFragment UI 초기화 완료");
        return view;
    }




    /** 메시지 전송 처리 */
    // ChatFragment.java (sendMessage 부분만 교체)
// 기능: 메시지 전송 시 sender_type="USER"를 함께 보낸다.

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) return;

        try {
            JSONObject data = new JSONObject();
            data.put("room_id", roomId);
            data.put("sender_id", myUserId);
            data.put("sender_type", "USER"); // ✅ A안 기준 필수
            data.put("message", message);

            if (mSocket != null && mSocket.connected()) {
                mSocket.emit("send_message", data);
                etMessage.setText("");
            } else {
                Log.e(TAG, "메시지 전송 실패: 소켓 미연결");
            }
        } catch (Exception e) {
            Log.e(TAG, "메시지 전송 실패: " + e.getMessage());
        }
    }

    /** 메시지 수신 리스너 */
    // ChatFragment.java (onReceiveMessage 리스너 교체)
// 기능: 서버 emit 키(CamelCase)와 모델에 맞게 매핑
    private final Emitter.Listener onReceiveMessage = args -> {
        if (getActivity() == null || args.length == 0) return;

        getActivity().runOnUiThread(() -> {
            try {
                JSONObject data = (JSONObject) args[0];

                ChatMessage msg = new ChatMessage();
                msg.setMessageId(data.getInt("messageId"));
                msg.setRoomId(data.getInt("roomId"));
                msg.setSenderId(data.getInt("senderId"));
                msg.setSenderType(data.optString("senderType", "USER"));
                msg.setMessage(data.optString("message", ""));
                msg.setTimestamp(data.optString("createdAt", ""));

                adapter.addMessage(msg);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                Log.i(TAG, "메시지 수신: " + msg.getMessage());

            } catch (Exception e) {
                Log.e(TAG, "메시지 처리 오류: " + e.getMessage());
            }
        });
    };

    // ChatFragment.java 내부에 추가
    /** 기능: 방 입장 시 DB에 저장된 과거 메시지를 불러와 리스트에 세팅한다. */
    private void loadHistory() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getChatMessages(roomId).enqueue(new Callback<MessagesResponse>() {
            @Override
            public void onResponse(Call<MessagesResponse> call, Response<MessagesResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null || !resp.body().success) return;
                adapter.setMessages(resp.body().messages);
                recyclerView.post(() -> {
                    if (adapter.getItemCount() > 0) {
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                });
            }
            @Override
            public void onFailure(Call<MessagesResponse> call, Throwable t) { t.printStackTrace(); }
        });
    }



    /** Fragment 제거 시 소켓 정리 */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSocket != null) {
            mSocket.off("receive_message", onReceiveMessage);
            if (mSocket.connected()) mSocket.disconnect();
        }
    }
}
