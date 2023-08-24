package Certis.Web.Service;

import Certis.Web.entity.BoardTable;
import Certis.Web.entity.Comment;
import Certis.Web.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public void Write(Comment comment) throws Exception
    {
        commentRepository.save(comment);
    }

    public Comment commentView(Integer id)
    {
        return commentRepository.findById(id).get();
    }
    public void Delete(Integer id){
        commentRepository.deleteById(id);
    }
}
