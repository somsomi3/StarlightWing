package com.minjoo.StarlightWing.web;

import com.minjoo.StarlightWing.dto.ResponseDto;
import com.minjoo.StarlightWing.model.Board;
import com.minjoo.StarlightWing.model.MemberEntity;
import com.minjoo.StarlightWing.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    @PostMapping("/api/board")
    public String saveBoard(
        @RequestParam String title,
        @RequestParam String content,
        @RequestParam String category,
        @AuthenticationPrincipal MemberEntity member) { // 현재 로그인한 사용자 정보를 가져옵니다.

        // Board 객체를 생성하고 작성자 정보를 설정
        Board board = Board.builder()
            .title(title)
            .content(content)
            .category(category)
            .build();

        // boardService에 작성자 정보를 전달
        boardService.writeBoard(board, member);

        return "redirect:/"; // 글 작성 후 메인 페이지로 리다이렉트
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
