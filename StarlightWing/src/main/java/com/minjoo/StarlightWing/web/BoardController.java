package com.minjoo.StarlightWing.web;

//jsp파일들을 return해주는 BoardController

import com.minjoo.StarlightWing.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BoardController {

    //    @Autowired
    private final BoardService boardService;

    //index 부분 에서는 게시글들의 목록을 출력해줘야함. 이때 model을 활용한다.
    @GetMapping({"","/"})
    public String index(Model model,
        @PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        //ui model에서 키와 벨류를 받아서 활용한다.
        model.addAttribute("board", boardService.getBoardList(pageable));
        return "index";
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


}