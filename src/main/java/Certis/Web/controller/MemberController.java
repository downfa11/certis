package Certis.Web.controller;

import Certis.Web.dto.MemberDTO;
import Certis.Web.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("members/new")
    public String createMemberForm() {
        return "members/createMemberForm";
    }

    @PostMapping("members/new")
    public String save(@ModelAttribute MemberDTO memberDTO) {
        System.out.println("MemberController.save");
        System.out.println("memberDTO = " + memberDTO);
        memberService.save(memberDTO);

        return "home";
    }

    @GetMapping("/members/login")
    public String loginForm() {
        return "/members/login";
    }

    @PostMapping("/members/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session) {
        MemberDTO loginResult = memberService.login(memberDTO);
        if(loginResult != null) {
            session.setAttribute("loginEmail", loginResult.getEmail());
            return "main";
        } else {
            return "/members/login";
        }
    }

    @GetMapping("/members/list")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        model.addAttribute("memberList", memberDTOList);
        return "/members/list";

    }
}

