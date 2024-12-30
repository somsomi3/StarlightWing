package com.minjoo.StarlightWing.web;
import com.minjoo.StarlightWing.dto.UserDto;

import com.minjoo.StarlightWing.dto.ResponseDto;
import com.minjoo.StarlightWing.model.Board;
import com.minjoo.StarlightWing.service.BoardService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    //Dto: 요청에 대한 응답을 반환할때 사용하는 클래스로 , 모든 컨트롤러에서 공통으로 사용된다.

    //    @PostMapping("/api/board")
//    public ResponseDto<Integer> save(@RequestBody Board board, @AuthenticationPrincipal MemberEntity member) {
//        System.out.println("Board content: " + board.getContent()); // 디버깅
//        boardService.writeBoard(board, member);
//        return new ResponseDto<>(HttpStatus.OK, 1);
//    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/board")
    public ResponseEntity<?> saveBoard(
        @RequestParam(required = false, defaultValue = "제목 없음") String title,
        @RequestParam(required = false, defaultValue = "내용 없음") String content,
        @RequestParam(required = false, defaultValue = "기본 카테고리") String category,
        @RequestParam(value = "image", required = false) MultipartFile image,
        @AuthenticationPrincipal UserDto member) {

        if (member == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // 이미지 파일 저장 로직 (예: 로컬 파일 시스템 또는 클라우드 저장소)
        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            try {
                // 예시: 이미지를 로컬 디렉토리에 저장
                String uploadDir = "/uploads/";
                String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path filepath = Paths.get(uploadDir, filename);
                Files.copy(image.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = filepath.toString();
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 실패");
            }
        }

        // Board 객체 생성
        Board board = Board.builder()
            .title(title)
            .content(content)
//            .category(category)
            .imagePath(imagePath)
            .build();

        // BoardService 호출
        boardService.writeBoard(board, member);

        return ResponseEntity.ok().body(Map.of("message", "글이 성공적으로 작성되었습니다."));
    }



    @DeleteMapping("/api/board/{id}")
    public ResponseDto<Integer> deleteById(@PathVariable int id){
        boardService.deleteBoard(id);
        return new ResponseDto<>(HttpStatus.OK, 1);
    }

    @PutMapping("/api/board/{id}")
    public ResponseDto<Integer> update(@PathVariable int id, @RequestBody Board board) {
        boardService.updateBoard(id, board);
        return new ResponseDto<>(HttpStatus.OK, 1);
    }
}
