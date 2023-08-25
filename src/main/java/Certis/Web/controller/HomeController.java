package Certis.Web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/loginForm")
    public String loginForm(){
        return "login";
    }

    @GetMapping("/error")
    public String error(){
        return "error";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "join";
    }

    @GetMapping("/logout")
    public String logout() {
        return "index";
    }

    @GetMapping("/board")
    public String board() {
        return "board";
    }
}

