package com.example.my_o2o_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_o2o_app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 견적 요청 옵션 어댑터
 * - 단일 선택
 * - 체크박스 표시
 * - 외부에서 선택 상태 복원 가능
 */
public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.OptionViewHolder> {

    private List<String> optionList = new ArrayList<>();
    private int selectedPosition = -1; // 현재 선택된 인덱스
    private final OnOptionSelectListener listener;

    /**
     * 선택 이벤트 콜백
     */
    public interface OnOptionSelectListener {
        void onOptionSelected(int selectedIndex);
    }

    public OptionAdapter(OnOptionSelectListener listener) {
        this.listener = listener;
    }

    /**
     * 옵션 목록 갱신
     */
    public void setOptions(List<String> options) {
        this.optionList = options != null ? options : new ArrayList<>();
        selectedPosition = -1; // 새로운 질문 시 초기화
        notifyDataSetChanged();
    }

    /**
     * 현재 선택된 인덱스 반환
     */
    public int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * 외부에서 선택 상태 복원 시 사용
     */
    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_option, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        holder.checkBox.setText(optionList.get(position));
        holder.checkBox.setChecked(position == selectedPosition);

        holder.checkBox.setOnClickListener(v -> {
            int clickedPos = holder.getAdapterPosition();
            if (clickedPos == RecyclerView.NO_POSITION) return;

            int prevSelected = selectedPosition;
            selectedPosition = clickedPos;

            notifyItemChanged(prevSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onOptionSelected(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    static class OptionViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cbOption);
        }
    }
}
