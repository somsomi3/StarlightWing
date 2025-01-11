package com.minjoo.StarlightWing.persist;


import com.minjoo.StarlightWing.model.Diary;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 특정 날짜의 일기 조회
    List<Diary> findBySelectedDate(String selectedDate);
}
