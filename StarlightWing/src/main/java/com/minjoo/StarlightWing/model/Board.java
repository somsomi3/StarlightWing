package com.minjoo.StarlightWing.model;

import com.minjoo.StarlightWing.dto.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String title;

    //    @Lob //대용량 데이터 저장을 위한 것으로, 데이터베이스 설정 및 매핑 과정에서 예상치 못한 동작을 유발할 가능성이 있습니다.
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

//    @Column(nullable = false, length =20)
//    private String category;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private UserDto author;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt; // 수정 시간 필드 추가

    private String image; // 필드 추가

//    public Object getSomeLazyField() {
//    }
}
