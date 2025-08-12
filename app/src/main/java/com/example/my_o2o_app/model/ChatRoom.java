package com.example.my_o2o_app.model;

/**
 * 채팅방 정보를 담는 DTO
 * - 채팅 리스트 화면에서 표시될 데이터
 */
public class ChatRoom {
    private int roomId;
    private int expertId;
    private String expertName;      // 전문가 이름
    private String profileImage;    // 전문가 프로필 이미지 URL
    private String lastMessage;     // 마지막 메시지
    private String lastTime;        // 마지막 메시지 시간

    // ✅ 기본 생성자 (Gson / Retrofit 사용용)
    public ChatRoom() {}

    // ✅ 전체 생성자
    public ChatRoom(int roomId, int expertId, String expertName,
                    String profileImage, String lastMessage, String lastTime) {
        this.roomId = roomId;
        this.expertId = expertId;
        this.expertName = expertName;
        this.profileImage = profileImage;
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
    }

    // --- Getter & Setter ---
    public int getRoomId() { return roomId; }
    public int getExpertId() { return expertId; }
    public String getExpertName() { return expertName; }
    public String getProfileImage() { return profileImage; }
    public String getLastMessage() { return lastMessage; }
    public String getLastTime() { return lastTime; }

    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setExpertId(int expertId) { this.expertId = expertId; }
    public void setExpertName(String expertName) { this.expertName = expertName; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setLastTime(String lastTime) { this.lastTime = lastTime; }
}
