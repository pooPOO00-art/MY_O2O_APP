// 파일 기능: 전문가-사용자 채팅 화면 Fragment
// - 소켓 연결/해제, 메시지 전송/수신
// - RecyclerView 하단 정렬 + 날짜 헤더/시간 표기는 어댑터가 담당
// - 상대 아바타 URL(expertProfileUrl) 전달 받아 어댑터에 세팅
// 개발자 메모:
// 1) newInstance 오버로드 2종(3파라미터/4파라미터) 제공. 3파라미터는 4파라미터(null) 위임.
// 2) 서버 주소는 하드코딩 되어 있으므로 BuildConfig로 분리 권장.
// 3) 액티비티는 ChatFragment.newInstance(roomId, expertName, userId, expertProfile) 호출.

package com.example.my_o2o_app.view.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.adapter.ChatMessageAdapter;
import com.example.my_o2o_app.model.ChatMessage;
import com.example.my_o2o_app.model.MessagesResponse;
import com.example.my_o2o_app.network.ApiClient;
import com.example.my_o2o_app.network.ApiService;

import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    // ==== Argument Keys ====
    private static final String ARG_ROOM_ID = "room_id";
    private static final String ARG_EXPERT_NAME = "expert_name";
    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_EXPERT_PROFILE = "expert_profile"; // ✅ 추가: 아바타 URL

    // ==== Args ====
    private int roomId;
    private int myUserId;
    private String expertName;
    private String otherProfileUrl; // 상대 아바타 URL

    // ==== UI / Socket ====
    private Socket mSocket;
    private RecyclerView recyclerView;
    private ChatMessageAdapter adapter;
    private EditText etMessage;

    /** 기능: 3-파라미터 버전(호환용). 4-파라미터 버전을 null 프로필로 위임한다. */
    public static ChatFragment newInstance(int roomId, String expertName, int userId) {
        return newInstance(roomId, expertName, userId, null);
    }

    /** 기능: 4-파라미터 버전. 상대 프로필 URL까지 전달한다. */
    public static ChatFragment newInstance(int roomId, String expertName, int userId, @Nullable String expertProfileUrl) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROOM_ID, roomId);
        args.putInt(ARG_USER_ID, userId);
        args.putString(ARG_EXPERT_NAME, expertName);
        args.putString(ARG_EXPERT_PROFILE, expertProfileUrl); // ✅ 저장
        fragment.setArguments(args);
        return fragment;
    }

    /** 기능: 인자 파싱 및 소켓 연결 시작 */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            roomId          = getArguments().getInt(ARG_ROOM_ID);
            expertName      = getArguments().getString(ARG_EXPERT_NAME);
            myUserId        = getArguments().getInt(ARG_USER_ID, 1);
            otherProfileUrl = getArguments().getString(ARG_EXPERT_PROFILE); // ✅ 복원
        }

        // ⚠️ 서버 IP는 환경에 따라 변경됨. build.gradle의 buildConfigField로 분리 권장.
        new Thread(() -> {
            try {
                Log.i(TAG, "소켓 연결 시도...");
                mSocket = IO.socket("http://172.29.22.244:5001");
                mSocket.connect();

                JSONObject joinData = new JSONObject();
                joinData.put("room_id", roomId);
                joinData.put("user_id", myUserId);
                mSocket.emit("join_room", joinData);

                // 수신 리스너 등록
                mSocket.on("receive_message", onReceiveMessage);

                Log.i(TAG, "소켓 연결 및 방 입장 완료: roomId=" + roomId);
            } catch (Exception e) {
                Log.e(TAG, "소켓 연결 실패: " + e.getMessage());
            }
        }).start();
    }

    /** 기능: UI inflate 및 리스트/전송창 바인딩. RecyclerView를 하단 정렬로 세팅한다. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewChat);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true); // 최신 메시지 하단 정렬
        recyclerView.setLayoutManager(lm);

        adapter = new ChatMessageAdapter(new ArrayList<>());
        adapter.setMyUserId(myUserId);
        if (!TextUtils.isEmpty(otherProfileUrl)) {
            adapter.setOtherProfileUrl(otherProfileUrl); // ✅ 아바타 URL 전달
        }
        recyclerView.setAdapter(adapter);

        // 과거 메시지 로드
        loadHistory();

        etMessage = view.findViewById(R.id.etMessage);
        Button btnSend = view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sendMessage());

        // IME의 전송 액션으로도 전송
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        return view;
    }

    /** 기능: 메시지 전송. sender_type="USER" 고정으로 보낸다. */
    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) return;

        try {
            JSONObject data = new JSONObject();
            data.put("room_id", roomId);
            data.put("sender_id", myUserId);
            data.put("sender_type", "USER");
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

    /** 기능: 실시간 수신 메시지 → 어댑터에 추가하고 스크롤을 맨 아래로 이동 */
    // ChatFragment.java (onReceiveMessage 전체 교체 권장)
// 기능: 소켓 수신 → ChatMessage로 매핑 (createdAt/created_at 모두 수용)
    private final Emitter.Listener onReceiveMessage = args -> {
        if (getActivity() == null || args.length == 0) return;

        getActivity().runOnUiThread(() -> {
            try {
                JSONObject data = (JSONObject) args[0];
                // 디버깅: 서버에서 내려오는 필드 확인
                Log.d(TAG, "receive_message payload = " + data);

                ChatMessage msg = new ChatMessage();
                msg.setMessageId(data.optInt("messageId", 0));
                msg.setRoomId(data.optInt("roomId", 0));
                msg.setSenderId(data.optInt("senderId", 0));
                msg.setSenderType(data.optString("senderType", "USER"));
                msg.setMessage(data.optString("message", ""));

                // ✅ 핵심: createdAt 없으면 created_at도 시도
                String ts = data.optString("createdAt", data.optString("created_at", ""));
                msg.setTimestamp(ts); // ← 이 한 번만

                adapter.addMessage(msg);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            } catch (Exception e) {
                Log.e(TAG, "메시지 처리 오류: " + e.getMessage());
            }
        });
    };


    /** 기능: 방 입장 시 과거 메시지 로드 */
    private void loadHistory() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getChatMessages(roomId).enqueue(new Callback<MessagesResponse>() {
            @Override
            public void onResponse(Call<MessagesResponse> call, Response<MessagesResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null || !resp.body().success) return;
                adapter.submitMessages(resp.body().messages);
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

    /** 기능: 뷰 파괴 시 소켓 리스너 해제 및 연결 종료 */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSocket != null) {
            mSocket.off("receive_message", onReceiveMessage);
            if (mSocket.connected()) mSocket.disconnect();
        }
    }
}
