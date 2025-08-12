package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ME = 1;      // 내가 보낸 메시지
    private static final int TYPE_OTHER = 2;   // 상대방 메시지

    // ✅ 서버/소켓에서 받은 메시지 목록
    private final List<ChatMessage> messageList = new ArrayList<>();

    // ✅ 로그인한 사용자 ID (반드시 setMyUserId로 주입)
    private int myUserId = -1; // 기본값 -1로 방어

    public ChatMessageAdapter(List<ChatMessage> initial) {
        if (initial != null) messageList.addAll(initial);
        setHasStableIds(true); // ✅ Stable ID 사용
    }

    /** 기능: 로그인 사용자 ID 세팅 (필수) */
    public void setMyUserId(int userId) {
        this.myUserId = userId;
        notifyDataSetChanged();
    }

    /** 기능: 히스토리 전체 세팅(재입장 시 사용) */
    public void setMessages(List<ChatMessage> list) {
        messageList.clear();
        if (list != null) messageList.addAll(list);
        notifyDataSetChanged();
    }

    /** 기능: 실시간 수신 1건 추가(중복 방지) */
    public void addMessage(ChatMessage message) {
        if (message == null) return;
        int newId = message.getMessageId();
        for (ChatMessage m : messageList) {
            if (m.getMessageId() == newId) return; // ✅ 중복 방지
        }
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    /** 기능: 사용자 앱 기준으로 'USER' + 내 userId면 내 말풍선 */
    @Override
    public int getItemViewType(int position) {
        ChatMessage m = messageList.get(position);

        String type = m.getSenderType();
        boolean isMine;
        if (type == null || type.isEmpty()) {
            // ✅ 과거 데이터 호환: senderType 누락 시 senderId로만 판별
            isMine = (m.getSenderId() == myUserId);
        } else {
            // ✅ 모델에 상수 있으면 사용, 없으면 "USER" 문자열로 비교
            String userConst = null;
            try {
                userConst = ChatMessage.TYPE_USER; // 모델에 상수 있으면 OK
            } catch (Throwable ignore) {}
            String expected = (userConst != null) ? userConst : "USER";
            isMine = expected.equalsIgnoreCase(type) && m.getSenderId() == myUserId;
        }
        return isMine ? TYPE_ME : TYPE_OTHER;
    }

    /** 기능: Stable ID - messageId를 고유키로 사용 */
    @Override
    public long getItemId(int position) {
        return messageList.get(position).getMessageId();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ME) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_me, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_other, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).tvMessage.setText(message.getMessage());
        } else if (holder instanceof OtherMessageViewHolder) {
            ((OtherMessageViewHolder) holder).tvMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    /** 뷰홀더: 내 메시지 */
    static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

    /** 뷰홀더: 상대 메시지 */
    static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        OtherMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
