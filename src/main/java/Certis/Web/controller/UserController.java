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

        model.addAttribute("email", user.getEmail());
        model.addAttribute("name", user.getUsername());
        model.addAttribute("coin", user.getCoin());


        return "manager";
    }

    @GetMapping("/admin")
    public String admin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model){
        User user = principalDetails.getUser();

        model.addAttribute("email", user.getEmail());
        model.addAttribute("name", user.getUsername());
        model.addAttribute("coin", user.getCoin());

        return "admin";
    }

}
