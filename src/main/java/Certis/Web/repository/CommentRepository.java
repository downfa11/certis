package Certis.Web.repository;

import Certis.Web.entity.BoardTable;
import Certis.Web.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {

    List<Comment> findByBoard(BoardTable board);
}
