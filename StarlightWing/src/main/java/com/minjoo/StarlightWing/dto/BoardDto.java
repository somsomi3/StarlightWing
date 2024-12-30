package com.minjoo.StarlightWing.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDto {

    // Getters and Setters
    private Long id;
    private String title;
    private String content;
    private String auth; // Optional, depends on your use case
    private Timestamp createdDate;
    private Timestamp updatedDate;

    // Constructor
    public BoardDto(Long id, String title, String content, String auth, Timestamp createdDate, Timestamp updateDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.auth = auth;
        this.createdDate = createdDate;
    }

}
