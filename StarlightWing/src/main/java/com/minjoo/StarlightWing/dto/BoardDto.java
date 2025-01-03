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
    private String author; // 작성자 이름
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String image;

    // Board 엔티티에서 DTO로 변환하는 생성자
    public BoardDto(Long id, String title, String content, String author, Timestamp createdAt, Timestamp updatedAt, String image) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author != null ? author : "익명"; // 작성자 이름 설정
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.image = image;
    }

    // 생성자 오버로딩: UserDto를 auth로 받는 경우
    public BoardDto(Long id, String title, String content, UserDto author, Timestamp createdAt, Timestamp updatedAt, String image) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author != null ? author.getUsername() : "익명"; // UserDto에서 이름 추출
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.image = image;
    }
}
