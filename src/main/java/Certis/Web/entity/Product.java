package Certis.Web.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long price;
    private Long number;
    private String owner;

    @CreationTimestamp
    private Timestamp regDate;

    @OneToMany(mappedBy = "product")
    private List<UserProduct> purchasedByUsers = new ArrayList<>();

    @Builder(builderClassName = "ProductRegister", builderMethodName = "productRegister")
    public Product(String name, Long price, Long number, String owner) {
        this.name = name;
        this.price = price;
        this.number = number;
        this.owner = owner;
    }

    public String getOwner(String username) {
        this.owner = username;
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
