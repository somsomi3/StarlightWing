package com.minjoo.StarlightWing.web;

import com.minjoo.StarlightWing.dto.DiaryDto.DiaryRequest;
import com.minjoo.StarlightWing.dto.DiaryDto.DiaryResponse;
import com.minjoo.StarlightWing.model.Diary;
import com.minjoo.StarlightWing.service.DiaryService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    // 일기 저장
    @PostMapping
    public ResponseEntity<DiaryResponse> saveDiary(@RequestBody DiaryRequest request) {
        DiaryResponse response = diaryService.saveDiary(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/diary")
    public ResponseEntity<List<DiaryResponse>> getDiaryContents() {
        List<DiaryResponse> responses = diaryService.getDiaryContents();
        return ResponseEntity.ok(responses);
    }

    // 특정 날짜의 일기 조회
    @GetMapping("/{date}")
    public ResponseEntity<List<DiaryResponse>> getDiaryByDate(@PathVariable String date) {
        LocalDate selectedDate = LocalDate.parse(date);
        List<DiaryResponse> responses = diaryService.getDiaryByDate(selectedDate);
        return ResponseEntity.ok(responses);
    }
}
