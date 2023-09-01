package Certis.Web.controller;

import Certis.Web.entity.UserProduct;
import Certis.Web.repository.UserProductRepository;
import Certis.Web.auth.PrincipalDetails;
import Certis.Web.auth.PrincipalDetailsService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired private UserRepository userRepository;
    @Autowired private UserProductRepository userProductRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PrincipalDetailsService principalDetailsService;

    @Autowired
    public UserController(PrincipalDetailsService principalDetailsService) {
        this.principalDetailsService = principalDetailsService;
    }

    @PostMapping("/join")
    public String join(@ModelAttribute User user){
        user.setRole(Role.ROLE_USER);

        String encodePwd = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodePwd);
        userRepository.save(user);
        return "redirect:loginForm";
    }



    @GetMapping("/user")
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model){
        User user = principalDetails.getUser();

        model.addAttribute("email", user.getEmail());
        model.addAttribute("name", user.getUsername());
        model.addAttribute("coin", user.getCoin());

        List<UserProduct> purchasedProducts = userProductRepository.findByUser(user);
        model.addAttribute("purchasedProducts", purchasedProducts);

        return "user";
    }

    @GetMapping("/manager")
    public String manager(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model){
        User user = principalDetails.getUser();
        if (user.getRole() != Role.ROLE_MANAGER && user.getRole() != Role.ROLE_ADMIN) {
            System.out.println("권한 : "+user.getRole());
            return "redirect:/";
        }

        model.addAttribute("email", user.getEmail());
        model.addAttribute("name", user.getUsername());
        model.addAttribute("coin", user.getCoin());

        List<User> users = userRepository.findAll();
        for(User user_ : users){
            List<BoardTable> boards = user_.getBoards();
            model.addAttribute("boards", boards);

            /*List<Comment> comments = user_.getComments();
            model.addAttribute("comments", comments);
            for(Comment comment : comments){
                //System.out.println("content : "+comment.getContent());
            }*/
        }

        model.addAttribute("users", users);
        return "manage";
    }

    @GetMapping("/admin")
    public String admin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model){
        User user = principalDetails.getUser();

        model.addAttribute("email", user.getEmail());
        model.addAttribute("name", user.getUsername());
        model.addAttribute("coin", user.getCoin());

        return "admin";
    }
    @PostMapping("/manage/toggle-admin/{userId}")
    public ResponseEntity<?> toggleAdmin(@PathVariable Long userId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
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
