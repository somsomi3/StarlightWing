package com.minjoo.StarlightWing.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String index() {
        return "index"; // templates 디렉터리의 index.html 파일을 렌더링
    }
}
