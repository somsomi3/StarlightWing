package com.minjoo.StarlightWing.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDto {

    private Long id;
    private String title;
    private String content;
    private String auth; // 작성자 이름
    private Timestamp createdDate;
    private Timestamp updatedDate;

    // Board 엔티티에서 DTO로 변환하는 생성자
    public BoardDto(Long id, String title, String content, String auth, Timestamp createdDate, Timestamp updatedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.auth = auth != null ? auth : "익명"; // 작성자 이름 설정
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    // 생성자 오버로딩: UserDto를 auth로 받는 경우
    public BoardDto(Long id, String title, String content, UserDto auth, Timestamp createdDate, Timestamp updatedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.auth = auth != null ? auth.getUsername() : "익명"; // UserDto에서 이름 추출
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
