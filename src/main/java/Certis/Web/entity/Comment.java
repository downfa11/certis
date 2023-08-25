package Certis.Web.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private BoardTable board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

}
