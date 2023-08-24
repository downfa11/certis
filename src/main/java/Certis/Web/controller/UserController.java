package Certis.Web.controller;

import Certis.Web.auth.PrincipalDetails;
import Certis.Web.entity.BoardTable;
import Certis.Web.entity.Comment;
import Certis.Web.entity.Role;
import Certis.Web.entity.User;
import Certis.Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;
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
        return "/home";
    }

    @GetMapping("/user")
    @ResponseBody
    public String user(){
        return "user";
    }

    @GetMapping("/manager")
    @ResponseBody
    public String manager(){
        return "manager";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin(){
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

    @GetMapping("/manage")
    public String getUserList(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        /*User user = principalDetails.getUser();

        if (user.getRole() != Role.ROLE_MANAGER && user.getRole() != Role.ROLE_ADMIN) {
            System.out.println("권한 : "+user.getRole());
            return "redirect:/";
        }*/
        List<User> users = userRepository.findAll();
        for(User user : users){
           List<BoardTable> boards = user.getBoards();
            model.addAttribute("boards", boards);

            List<Comment> comments = user.getComments();
            model.addAttribute("comments", comments);
           for(Comment comment : comments){
               System.out.println("content : "+comment.getContent());
           }
        }

        model.addAttribute("users", users);
        return "manage";
    }

    @PostMapping("/manage/toggle-admin/{userId}")
    public ResponseEntity<?> toggleAdmin(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getRole() == Role.ROLE_ADMIN)
                return ResponseEntity.notFound().build();

            user.setRole(user.getRole() == Role.ROLE_USER ? Role.ROLE_MANAGER : Role.ROLE_USER); // 권한 토글
            userRepository.save(user);
            System.out.println("권한 : " + user.getRole());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
