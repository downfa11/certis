package Certis.Web.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private Long coin;


    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp  //자동으로 만들어준다
    private Timestamp createTime;

    private String provider;    // oauth2를 이용할 경우 어떤 플랫폼을 이용하는지
    private String providerId;  // oauth2를 이용할 경우 아이디값

    @OneToMany(mappedBy = "user")
    private List<UserProduct> purchasedProducts = new ArrayList<>();
    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    private List<BoardTable> boards;

    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    @Builder(builderClassName = "UserDetailRegister", builderMethodName = "userDetailRegister")
    public User(String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Builder(builderClassName = "OAuth2Register", builderMethodName = "oauth2Register")
    public User(String username, String password, String email, Role role, String provider, String providerId, Long coin, Long price) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.coin = coin;
    }
}
