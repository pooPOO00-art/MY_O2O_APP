package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<ChatRoom> chatList = new ArrayList<>();
    private final OnItemClickListener listener;



    public interface OnItemClickListener {
        void onItemClick(ChatRoom chatRoom);
    }

    public ChatListAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ChatRoom> newList) {
        chatList = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = chatList.get(position);

        holder.tvExpertName.setText(chatRoom.getExpertName());
        holder.tvLastMessage.setText(chatRoom.getLastMessage() != null ? chatRoom.getLastMessage() : "대화를 시작해보세요");
        holder.tvLastTime.setText(chatRoom.getLastTime() != null ? chatRoom.getLastTime() : "");

        // 프로필 이미지 (없으면 기본 이미지)
        if (chatRoom.getProfileImage() != null && !chatRoom.getProfileImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(chatRoom.getProfileImage())
                    .placeholder(R.drawable.ic_placeholder)
                    .circleCrop()
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(chatRoom);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvExpertName, tvLastMessage, tvLastTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvExpertName = itemView.findViewById(R.id.tvExpertName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvLastTime = itemView.findViewById(R.id.tvLastTime);
        }
    }
}
