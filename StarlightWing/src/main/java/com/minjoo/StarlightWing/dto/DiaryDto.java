package com.minjoo.StarlightWing.dto;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

public class DiaryDto {

    // 요청 DTO
    @Data
    public static class DiaryRequest {

        private Timestamp createdAt; // 생성일
        private String selectedDate; // 작성 날짜
//        private String title; // 제목
        private String content; // 내용
        private String weather; // 날씨 정보
    }

    // 응답 DTO
    @Data
    @Builder
    public static class DiaryResponse {
        private Long id; // 고유 ID
        private String selectedDate; // 작성 날짜
//        private String title; // 제목
        private String content; // 내용
        private String weather; // 날씨 정보
        private Timestamp createdAt; // 생성일
    }
}