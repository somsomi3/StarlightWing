package com.minjoo.StarlightWing.web;

//jsp파일들을 return해주는 BoardController

import com.minjoo.StarlightWing.model.Board;
import com.minjoo.StarlightWing.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class BoardController {

    //    @Autowired
    private final BoardService boardService;

    //index 부분 에서는 게시글들의 목록을 출력해줘야함. 이때 model을 활용한다.
    @GetMapping({"/posts"})
    public String index(Model model,
        @PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        //ui model에서 키와 벨류를 받아서 활용한다.
        model.addAttribute("board", boardService.getBoardList(pageable));
        return "login";
    }

    @GetMapping("/board/saveForm")
    public String saveForm(){
        return "board/saveForm";
    }
    //
//    @GetMapping("/board/{id}")//게시글의 아이디를 get요청으로 받고,
//    public String getBoard(Model model, @PathVariable int id){
//        //각게시글에 해당되는 getBoard는, 게시글의 아이디를 get요청으로 받고, 해당요청을 getBoard요청을 이용햇 조회를 한다.그러고 모델 객체에 매핑함.
//        model.addAttribute("boards",boardService.getBoard(id));//해당요청을 getBoard요청을 이용햇 조회를 한다.그러고 모델객체에 매핑함.
//        return "board/detail";//그러고 마지막으로 detail 페이지로 보내줌.
//    }
    @GetMapping("/board/{id}")
    public String getBoard(Model model, @PathVariable Long id) {
        model.addAttribute("boards", boardService.getBoard(id)); // 올바른 인자 타입
        return "board/detail";
    }

    @GetMapping("/board/{id}/updateForm")//해당아이디의 updateForm을 get메서드로 받앗을때,
    public String updateForm(Model model, @PathVariable Long id) {
        model.addAttribute("boards", boardService.getBoard(id));
        return "board/updateForm";
    }
//    @PreAuthorize("isAuthenticated()")  // isAuthenticated()는 로그인된 사용자만 접근 허용
//    @PostMapping("/api/v1/board")
//    public ResponseEntity<?> createBoard(@RequestBody Board boardDto) {
//        // 게시글 생성 로직
//        return ResponseEntity.ok().build();
//    }


}