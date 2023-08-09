package Certis.Web.Service;
import Certis.Web.entity.BoardTable;
import Certis.Web.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardrepository;
    public void Write(BoardTable board) throws Exception
    {
        boardrepository.save(board);
    }

    public Page<BoardTable> boardList(Pageable pageable)
    {
        return boardrepository.findAll(pageable);
    }

    public Page<BoardTable> boardSearchList(String searchKeyword, Pageable pageable){
        return boardrepository.findByTitleContaining(searchKeyword,pageable);
    }
    public BoardTable boardView(Integer id)
    {
        return boardrepository.findById(id).get();
    }

    public void boardDelete(Integer id){
        boardrepository.deleteById(id); //일단 지우는데 나중에 관리자 페이지로 옮겨야함
    }
}
