package Certis.Web.controller;

import Certis.Web.auth.PrincipalDetails;
import Certis.Web.entity.Role;
import Certis.Web.entity.User;
import Certis.Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class UserController {
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/loginForm")
    public String loginForm(){
        return "/login";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "/join";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute User user){
        user.setRole(Role.ROLE_USER);

        String encodePwd = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodePwd);

        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/logout")
    public String logout() {
        return "index";
    }

    @GetMapping("/user")
    public String user(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2UserPrincipal, Model model){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);

        model.addAttribute("email", attributes.get("email"));
        model.addAttribute("name", attributes.get("name"));

        return "user";
    }

    @GetMapping("/manager")
    public String manager(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2UserPrincipal, Model model){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);

        model.addAttribute("email", attributes.get("email"));
        model.addAttribute("name", attributes.get("name"));

        return "manager";
    }

    @GetMapping("/admin")
    public String admin(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2UserPrincipal, Model model){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);

        model.addAttribute("email", attributes.get("email"));
        model.addAttribute("name", attributes.get("name"));

        return "admin";
    }


    //OAuth로 로그인 시 이 방식대로 하면 CastException 발생함
    @GetMapping("/form/loginInfo")
    @ResponseBody
    public String formLoginInfo(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails){

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        User user = principal.getUser();
        System.out.println(user);

        User user1 = principalDetails.getUser();
        System.out.println(user1);


        return user.toString();
    }

    @GetMapping("/oauth/loginInfo")
    @ResponseBody
    public String oauthLoginInfo(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2UserPrincipal){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);

        Map<String, Object> attributes1 = oAuth2UserPrincipal.getAttributes();

        return attributes.toString();
    }


    @GetMapping("/loginInfo")
    @ResponseBody
    public String loginInfo(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails){
        String result = "";

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if(principal.getUser().getProvider() == null) {
            result = result + "Form 로그인 : " + principal;
        }else{
            result = result + "OAuth2 로그인 : " + principal;
        }
        return result;
    }

    @GetMapping("/board")
    public String board() {
        return "board";
    }


}
