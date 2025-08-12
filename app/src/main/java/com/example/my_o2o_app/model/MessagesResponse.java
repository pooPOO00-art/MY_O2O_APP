// MessagesResponse.java
// 기능: 서버 /chat/messages 응답 래퍼 DTO
// - success: 처리 성공 여부
// - messages: 채팅 메시지 리스트

package com.example.my_o2o_app.model;

import java.util.List;

public class MessagesResponse {
    public boolean success;
    public List<ChatMessage> messages;
}
