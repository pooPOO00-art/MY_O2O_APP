package com.example.my_o2o_app.model;

import java.util.List;

/**
 * 견적 질문 Model
 * - 서버에서 질문과 함께 옵션 목록을 반환
 */
public class Question {
    private int question_id;         // 질문 ID
    private String content;          // 질문 내용
    private List<QuestionOption> options; // 해당 질문의 옵션 목록

    public int getQuestion_id() { return question_id; }
    public String getContent() { return content; }
    public List<QuestionOption> getOptions() { return options; }
}
