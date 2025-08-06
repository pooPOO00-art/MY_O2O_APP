package com.example.my_o2o_app.view.chat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import com.example.my_o2o_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatActivity extends AppCompatActivity {

    private Socket mSocket;
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);

        try {
            // 에뮬레이터면 10.0.2.2, 실제 기기는 PC IP 사용
            mSocket = IO.socket("http://172.29.230.71:5001");
            mSocket.connect();
            Log.d("Socket", "서버 연결 시도");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // 서버에서 오는 메시지 이벤트 처리
        mSocket.on("receive_message", args -> {
            JSONObject data = (JSONObject) args[0];
            Log.d("Socket", "서버에서 메시지 수신: " + data.toString());
        });

        // 버튼 클릭 시 메시지 전송
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                JSONObject data = new JSONObject();
                try {
                    data.put("sender_id", 1);      // 테스트용
                    data.put("receiver_id", 2);    // 테스트용
                    data.put("message", message);
                } catch (JSONException e) { e.printStackTrace(); }

                mSocket.emit("send_message", data);
                etMessage.setText("");
            }
        });
    }
}
