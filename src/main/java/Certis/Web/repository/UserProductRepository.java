package Certis.Web.repository;

import Certis.Web.entity.User;
import Certis.Web.entity.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Long> {
    List<UserProduct> findByUser(User user);
}
