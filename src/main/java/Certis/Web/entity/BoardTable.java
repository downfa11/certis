package Certis.Web.entity;

import lombok.Data;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
public class BoardTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @OneToMany(mappedBy = "author")
    private List<Comment> comments = new ArrayList<>();

    public BoardTable() {
        this.comments = new ArrayList<>();
    }
}