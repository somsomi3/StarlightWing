package com.minjoo.StarlightWing.service;


import com.minjoo.StarlightWing.dto.DiaryDto.DiaryRequest;
import com.minjoo.StarlightWing.dto.DiaryDto.DiaryResponse;
import com.minjoo.StarlightWing.model.Diary;
import com.minjoo.StarlightWing.persist.DiaryRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    // 일기 저장
    public DiaryResponse saveDiary(DiaryRequest request) {
//        LocalDate selectedDate = LocalDate.from(request.getSelectedDate());

        Diary diary = Diary.builder()
            .selectedDate(request.getSelectedDate())
//            .title(request.getTitle())
            .content(request.getContent())
            .weather(request.getWeather())
            .createdAt(request.getCreatedAt())
            .build();

        Diary savedDiary = diaryRepository.save(diary);

        return mapToResponse(savedDiary);
    }

    // 특정 날짜의 일기 조회
    public List<DiaryResponse> getDiaryByDate(LocalDate selectedDate) {
        List<Diary> diaries = diaryRepository.findBySelectedDate(
            String.valueOf(selectedDate.atStartOfDay()));
        return diaries.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // 엔티티 -> 응답 DTO 변환
    private DiaryResponse mapToResponse(Diary diary) {
        return DiaryResponse.builder()
            .id(diary.getId())
            .selectedDate(String.valueOf(LocalDateTime.parse(diary.getSelectedDate())))
//            .title(diary.getTitle())
            .content(diary.getContent())
            .weather(diary.getWeather())
            .createdAt(Timestamp.valueOf(diary.getCreatedAt().toLocalDateTime()))
            .build();
    }

    public List<DiaryResponse> getDiaryContents() {
        // 모든 다이어리 가져오기
        List<Diary> diaries = diaryRepository.findAll();

        // 날짜 형식 변환용 포맷터 생성
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("Fetched Diaries: " + diaries);

        // Diary 데이터를 DiaryResponse로 변환
        List<DiaryResponse> responses = diaries.stream()
            .map(diary -> DiaryResponse.builder()
                .id(diary.getId())
                .content(diary.getContent())
                .createdAt(diary.getCreatedAt()) // Timestamp 그대로 유지
                .selectedDate(
                    LocalDate.parse(diary.getSelectedDate(), inputFormatter)
                        .format(outputFormatter)) // 문자열을 LocalDate로 변환 후 다시 포맷
                .build())
            .collect(Collectors.toList());

        System.out.println("Diary Responses: " + responses);
        return responses;
    }


}
