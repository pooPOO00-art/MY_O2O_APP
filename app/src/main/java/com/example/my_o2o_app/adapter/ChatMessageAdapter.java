// 파일 기능: 채팅 RecyclerView 어댑터
// - 메시지 목록에 날짜 헤더를 자동 삽입해 가독성을 높임
// - 각 말풍선에 '오전/오후 h:mm' 시간 표시
// - 상대방 메시지에는 원형 프로필 아바타 표시(Glide circleCrop)
// - 서버 타임스탬프 포맷이 다양한 경우(초/밀리초/ISO/UTC)에도 안전하게 파싱
//
// 개발자 관점 체크포인트:
// 1) SimpleDateFormat은 스레드-세이프하지 않음 → 본 어댑터는 메인 스레드에서만 사용 가정(문제 없음).
// 2) 날짜 헤더는 "yyyy년 M월 d일 EEEE" 기준. 필요 시 strings.xml/Locale 전략 변경.
// 3) 프로필 URL이 없으면 ic_placeholder 사용. 리소스 존재 필수.
// 4) 레이아웃 전제: item_chat_me(tvMessage, tvTime) / item_chat_other(ivAvatar, tvMessage, tvTime) / item_chat_date_header(tvDate)
// 5) 성능: submitMessages()는 전체 리셋(notifyDataSetChanged), 실시간 addMessage()는 단일 notifyItemInserted 사용.

package com.example.my_o2o_app.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_o2o_app.R;
import com.example.my_o2o_app.model.ChatMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // === View Types ===
    private static final int TYPE_DATE  = 0; // 날짜 헤더
    private static final int TYPE_ME    = 1; // 내 메시지
    private static final int TYPE_OTHER = 2; // 상대 메시지

    // 내 사용자 ID / 상대 프로필 URL
    private int myUserId = 1;
    private String otherProfileUrl = null;

    // 렌더링용 리스트 (메시지 + 날짜헤더 혼합)
    private final List<Item> items = new ArrayList<>();

    // === 내부 아이템 모델: 메시지 or 날짜헤더 ===
    private static class Item {
        final int type;           // TYPE_DATE / TYPE_ME / TYPE_OTHER
        final ChatMessage msg;    // 메시지인 경우
        final String dateText;    // 날짜헤더인 경우
        Item(int type, ChatMessage m, String d) {
            this.type = type;
            this.msg = m;
            this.dateText = d;
        }
    }

    // ====== 생성자 & 설정자 ======

    /** 기능: 초기 메시지 목록을 받아 어댑터 구성 (null 허용) */
    public ChatMessageAdapter(List<ChatMessage> initial) {
        submitMessages(initial);
    }

    /** 기능: 내 사용자 ID 설정 (내/상대 판별에 사용) */
    public void setMyUserId(int userId) { this.myUserId = userId; }

    /** 기능: 상대 프로필 URL 설정 (상대 말풍선 아바타에 표시) */
    public void setOtherProfileUrl(String url) { this.otherProfileUrl = url; }

    // ====== 목록 구성 로직 ======

    /** 기능: 서버에서 받은 메시지 리스트를 받아 날짜 헤더를 끼워 넣고 렌더링 리스트로 만든다. */
    public void submitMessages(List<ChatMessage> list) {
        items.clear();
        if (list == null) list = new ArrayList<>();

        String lastDate = null;
        for (ChatMessage m : list) {
            String date = formatKoreanDate(m.getTimestamp()); // 예: "2025년 8월 12일 화요일"
            if (!TextUtils.isEmpty(date) && !date.equals(lastDate)) {
                items.add(new Item(TYPE_DATE, null, date));
                lastDate = date;
            }
            boolean mine = isMine(m);
            items.add(new Item(mine ? TYPE_ME : TYPE_OTHER, m, null));
        }
        notifyDataSetChanged();
    }

    /** 기능: 단일 메시지 추가(실시간 수신). 타임스탬프가 없으면 현재시간 사용 */
    public void addMessage(ChatMessage m) {

        // 날짜 헤더 필요 여부 체크
        String date = formatKoreanDate(m.getTimestamp());
        String lastDate = findLastDateInItems();
        if (!TextUtils.isEmpty(date) && !date.equals(lastDate)) {
            items.add(new Item(TYPE_DATE, null, date));
        }
        items.add(new Item(isMine(m) ? TYPE_ME : TYPE_OTHER, m, null));
        notifyItemInserted(items.size() - 1);
    }

    /** 기능: 해당 메시지가 '내' 메시지인지 판별 */
    private boolean isMine(ChatMessage m) {
        String type = m.getSenderType();
        if (type == null || type.isEmpty()) {
            return m.getSenderId() == myUserId;
        }
        // 서버가 "USER"/"EXPERT"로 내려줄 때: USER + senderId == myUserId
        return "USER".equalsIgnoreCase(type) && m.getSenderId() == myUserId;
    }

    /** 기능: items의 마지막 날짜 헤더 텍스트를 찾는다(없으면 null) */
    private String findLastDateInItems() {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).type == TYPE_DATE) return items.get(i).dateText;
        }
        return null;
    }

    // ====== 시간/날짜 포맷 ======
    // 출력 포맷
    // 파일 기능: 채팅 RecyclerView 어댑터 (발췌)
// 개선: 히스토리/실시간 서로 다른 createdAt 포맷을 모두 파싱하도록 확장
//      - ISO 'T' 형식(타임존 없음) 추가
//      - 파싱 실패 시 원문 시간을 그대로 보여주는 폴백

    // ====== 시간/날짜 포맷 ======
    private static final SimpleDateFormat DATE_OUT =
            new SimpleDateFormat("yyyy년 M월 d일 EEEE", Locale.KOREA);
    private static final SimpleDateFormat TIME_OUT =
            new SimpleDateFormat("a h:mm", Locale.KOREA); // 오전/오후 h:mm

    // 입력 포맷(다중 후보): 초/밀리초/ISO/UTC/로컬 'T'까지 커버
    private static final List<SimpleDateFormat> SRC_FORMATS = new ArrayList<>(Arrays.asList(
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA),        // 실시간(서버 DATE_FORMAT)
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA),    // 밀리초
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.KOREA), // ✅ 마이크로초

            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),      // ISO Z(UTC)
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),  // ISO Z+밀리초
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US),      // ISO +09:00
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US),  // ISO 밀리초 + 오프셋
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),         // ✅ ISO 'T' (타임존 없음)
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US),      // ✅ ISO 'T' 밀리초 (타임존 없음)
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US), // ✅ RFC1123 (Tue, 12 Aug 2025 15:24:00 GMT)
            new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)   // ✅ Java Date.toString()
    ));

    static {
        for (SimpleDateFormat f : SRC_FORMATS) {
            String p = f.toPattern();
            if (p.contains("'Z'") || p.contains("XXX") || p.contains("zzz")) {
                f.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC/오프셋 표기 포함 포맷은 UTC 기준 파싱
            }
        }
    }


    /** 기능: 한국어 시간 문자열로 변환 (파싱 실패 시 원문 일부라도 노출) */
    // 기능: 한국어 시간 문자열로 변환
// - 지원 포맷에 맞게 파싱되면 "오전/오후 h:mm"으로 출력
// - 파싱 실패 시 빈 문자열 반환(엉뚱한 텍스트 노출 방지)
    private static String formatKoreanTime(String ts) {
        Date d = parse(ts);
        return (d == null) ? "" : TIME_OUT.format(d);
    }

    // 기능: 한국어 날짜 문자열로 변환(날짜 헤더용)
// - 파싱 실패 시 null 반환(헤더 추가 안 함)
    private static String formatKoreanDate(String ts) {
        Date d = parse(ts);
        return (d == null) ? null : DATE_OUT.format(d);
    }



    /** 기능: 다양한 입력 포맷 후보로 타임스탬프 문자열을 파싱 */
    private static Date parse(String ts) {
        if (ts == null || ts.isEmpty()) return null;
        for (SimpleDateFormat f : SRC_FORMATS) {
            try { return f.parse(ts); } catch (ParseException ignore) {}
        }
        return null; // 전부 실패 시 null
    }

    /** 기능: 현재 시간을 기본 입력 포맷(초) 문자열로 반환 */
    private static String nowString() {
        // 기본값은 초 단위 포맷으로 통일
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date());
    }

    // ====== RecyclerView.Adapter 구현 ======

    @Override
    public int getItemViewType(int position) { return items.get(position).type; }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_DATE) {
            View v = inf.inflate(R.layout.item_chat_date_header, parent, false);
            return new DateVH(v);
        } else if (viewType == TYPE_ME) {
            View v = inf.inflate(R.layout.item_chat_me, parent, false);
            return new MeVH(v);
        } else {
            View v = inf.inflate(R.layout.item_chat_other, parent, false);
            return new OtherVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        Item item = items.get(pos);
        if (h instanceof DateVH) {
            ((DateVH) h).tvDate.setText(item.dateText);
            return;
        }

        if (item.msg == null) return; // 방어: 이 케이스는 TYPE_DATE 외엔 발생하지 않아야 함

        if (h instanceof MeVH) {
            ((MeVH) h).tvMsg.setText(item.msg.getMessage());
            ((MeVH) h).tvTime.setText(formatKoreanTime(item.msg.getTimestamp()));
        } else if (h instanceof OtherVH) {
            OtherVH vh = (OtherVH) h;
            vh.tvMsg.setText(item.msg.getMessage());
            vh.tvTime.setText(formatKoreanTime(item.msg.getTimestamp()));

            ImageView iv = vh.ivAvatar;
            if (!TextUtils.isEmpty(otherProfileUrl)) {
                Glide.with(iv.getContext())
                        .load(otherProfileUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_placeholder)
                        .into(iv);
            } else {
                iv.setImageResource(R.drawable.ic_placeholder);
            }
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ====== ViewHolders ======
    static class DateVH extends RecyclerView.ViewHolder {
        final TextView tvDate;
        DateVH(@NonNull View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvDate);
        }
    }

    static class MeVH extends RecyclerView.ViewHolder {
        final TextView tvMsg;
        final TextView tvTime;
        MeVH(@NonNull View v) {
            super(v);
            tvMsg  = v.findViewById(R.id.tvMessage);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }

    static class OtherVH extends RecyclerView.ViewHolder {
        final ImageView ivAvatar;
        final TextView tvMsg;
        final TextView tvTime;
        OtherVH(@NonNull View v) {
            super(v);
            ivAvatar = v.findViewById(R.id.ivAvatar);
            tvMsg    = v.findViewById(R.id.tvMessage);
            tvTime   = v.findViewById(R.id.tvTime);
        }
    }
}
