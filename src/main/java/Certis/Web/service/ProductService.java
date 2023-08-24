package Certis.Web.service;

import Certis.Web.entity.Product;
import Certis.Web.entity.User;
import Certis.Web.entity.UserProduct;
import Certis.Web.repository.ProductRepository;
import Certis.Web.repository.UserProductRepository;
import Certis.Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserProductRepository userProductRepository;
    @Autowired
    public ProductService(ProductRepository productRepository, UserRepository userRepository, UserProductRepository userProductRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.userProductRepository = userProductRepository;
    }

    public void productEnroll(Product product) {
        validateDuplicateProduct(product);
        productRepository.save(product);
    }

    public List<Product> findProducts() {
        return productRepository.findAll();
    }

    private void validateDuplicateProduct(Product product) {
        Product name = productRepository.findByName(product.getName());
        if(name != null) {
            throw new IllegalStateException("이미 존재하는 상품입니다.");
        }
    }

    @Transactional
    public void purchaseProduct(Product product, User user) {
        Long coin = user.getCoin();
        Long price = product.getPrice();
        Long res = coin - price;

        Long number = product.getNumber();

        if (res < 0) {
            System.out.println("coin 부족");
        } else {
            user.setCoin(res);
            product.setNumber(number - 1);
            userRepository.save(user);
            productRepository.save(product);

            UserProduct userProduct = new UserProduct();
            userProduct.setUser(user);
            userProduct.setProduct(product);
            userProductRepository.save(userProduct);
        }

    }

}

