// ChatMessage.java
// 기능: 채팅 메시지 DTO. 서버 JSON 키(스네이크/카멜)를 모두 안전하게 매핑한다.
// - DB/서버가 created_at, sender_type 등 스네이크 케이스로 주어도 파싱되게 @SerializedName 사용
// - 어댑터에서 '내/상대' 판별을 위해 senderType("USER"/"EXPERT") 값을 그대로 보존
package com.example.my_o2o_app.model;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {

    /** 서버가 messageId 또는 message_id로 내려와도 매핑됨 */
    @SerializedName(value = "messageId", alternate = {"message_id"})
    private int messageId;

    /** roomId 또는 room_id 매핑 */
    @SerializedName(value = "roomId", alternate = {"room_id"})
    private int roomId;

    /** senderId 또는 sender_id 매핑 */
    @SerializedName(value = "senderId", alternate = {"sender_id"})
    private int senderId;

    /** senderType 또는 sender_type 매핑 ("USER" / "EXPERT") */
    @SerializedName(value = "senderType", alternate = {"sender_type"})
    private String senderType;

    /** message 또는 message_content 매핑 */
    @SerializedName(value = "message", alternate = {"message_content"})
    private String message;

    /** createdAt 또는 created_at을 timestamp 필드로 받음 */
    @SerializedName(value = "timestamp", alternate = {"createdAt", "created_at"})
    private String timestamp;

    // ---- 상수: 어댑터/분기에서 오탈자 방지용 ----
    public static final String TYPE_USER = "USER";
    public static final String TYPE_EXPERT = "EXPERT";

    /** 기본 생성자 (Gson/Room 등에서 필요) */
    public ChatMessage() {}

    /** 최소 필드 생성자 (소켓 수신 등 임시 구성에 사용) */
    public ChatMessage(int messageId, int roomId, int senderId, String message) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.senderId = senderId;
        this.message = message;
    }

    /** 전체 필드 생성자 */
    public ChatMessage(int messageId, int roomId, int senderId, String senderType, String message, String timestamp) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderType = senderType;
        this.message = message;
        this.timestamp = timestamp;
    }

    // ---- Getter / Setter ----
    /** 기능: 서버가 부여한 메시지 PK */
    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    /** 기능: 방 ID */
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    /** 기능: 발신자 식별자 (user_id 또는 expert_id) */
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    /** 기능: 발신자 타입 ("USER"/"EXPERT") */
    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }

    /** 기능: 본문 텍스트 */
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    /** 기능: 생성 시각 문자열 (createdAt/created_at 매핑) */
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
