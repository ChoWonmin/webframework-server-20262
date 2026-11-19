package com.example.webframework.hello;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {
    // Hello 컨트롤러 만들고
    // localhost:8080/hello
    // 브라우저 접속하면 자기 이름 나오도록.

    private final HelloService helloService;

    @GetMapping("/hello")
    public String hello() {
        String abc = " abc     ";
        abc.trim(); // "abc"

        return "hello! 조원민님";
    }

    @GetMapping("/put-avatar")
    public String putAvatar() {
        return helloService.putAvatar();
    }
}
