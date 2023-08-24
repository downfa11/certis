package Certis.Web.controller;

import Certis.Web.entity.Product;
import Certis.Web.entity.User;
import Certis.Web.repository.ProductRepository;
import Certis.Web.repository.UserProductRepository;
import Certis.Web.auth.PrincipalDetails;
import Certis.Web.auth.PrincipalDetailsService;
import Certis.Web.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final PrincipalDetailsService principalDetailsService;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserProductRepository userProductRepository;

    public ProductController(PrincipalDetailsService principalDetailsService, ProductService productService) {
        this.principalDetailsService = principalDetailsService;
        this.productService = productService;
    }

    @GetMapping("/store")
    public String openStore(@ModelAttribute Product product, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        User user = principalDetails.getUser();
        model.addAttribute("coin", user.getCoin());

        List<Product> products = productService.findProducts();
        model.addAttribute("products", products);

        return "store";
    }

    @PostMapping("/store")
    public String purchase(@RequestParam("product") String productName, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();

        Product product = productRepository.findByName(productName);
        if (product != null) {
            productService.purchaseProduct(product, user);
        }

        return "redirect:/store";
    }
    @GetMapping("/enroll")
    public String openEnroll() {

        return "enroll";
    }

    @PostMapping("/enroll")
    public String productEnroll(Product product, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();

        product.getOwner(user.getUsername());
        productService.productEnroll(product);

        return "redirect:/enroll";
    }
}
