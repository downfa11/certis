package Certis.Web.controller;
import Certis.Web.Service.BoardService;
import Certis.Web.auth.PrincipalDetails;
import Certis.Web.entity.BoardTable;
import Certis.Web.entity.Role;
import Certis.Web.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

@Controller

public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping(value="board/write")
    public String boardWriteForm(BoardTable board){
        if(!AuthPage())
            return "redirect:/";

        return "write";
    }

    @PostMapping(value="/board/do")
    public String boardWriteProcess(BoardTable board, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception {
        if(!AuthPage())
            return "redirect:/";

        User user = principalDetails.getUser();

        board.setOwner(user.getId());
        System.out.println(("제목 : "+board.getTitle()));
        System.out.println(("내용 : "+board.getContent()));
        System.out.println(("작성자 : "+board.getOwner()));
        boardService.Write(board);
        model.addAttribute("message","글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");
        return "message";
    }

    @GetMapping("board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0,size=10, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword){
// sort : 정렬 기준 Column, direction : 정렬 순서 Decrease

        if(!AuthPage())
            return "redirect:/";

        Page<BoardTable> list = null;
        if(searchKeyword==null) {
            list = boardService.boardList(pageable);
        }
        else {
            list = boardService.boardSearchList(searchKeyword,pageable);

        }
        int newPage = list.getPageable().getPageNumber() + 1;
        int startPage =Math.max(newPage-4,1);
        int endPage = Math.min(newPage+5,list.getTotalPages());

        model.addAttribute("list",list);
        model.addAttribute("nowPage",newPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        return "boardList";
    }

    @GetMapping("/board/view")
    public String boardView(Model model,Integer id){
        if(!AuthPage())
            return "redirect:/";

        model.addAttribute("board",boardService.boardView(id));
        return "boardview";
    }
    @GetMapping("board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id,Model model){

        if(!AuthPage())
            return "redirect:/";

        model.addAttribute("board",boardService.boardView(id));
        return "boardmodify";
    }


    @GetMapping("/board/delete")
    public String boardDelete(Integer id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        if(!AuthPage())
            return "redirect:/";

        BoardTable board = boardService.boardView(id);
        Long Owner = board.getOwner();
        User user = principalDetails.getUser();
        if(Owner==user.getId()||user.getRole()== Role.ROLE_MANAGER||user.getRole()== Role.ROLE_ADMIN)
        {
            System.out.println("게시글 권한 확인");
            boardService.boardDelete(id);
        }

        return "redirect:/board/list";
    }

    @PostMapping("board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id,BoardTable board, @AuthenticationPrincipal PrincipalDetails principalDetails)throws Exception{
        if(!AuthPage())
            return "redirect:/";

        BoardTable boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        User user = principalDetails.getUser();
        boardTemp.setOwner(user.getId());

        boardService.Write(boardTemp);
        return "redirect:/board/list";
    }


    public boolean AuthPage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails;
    }

}


// JPA Repository
// findBy(Column name) -> 컬럽에서 키워드를 넣어서 찾는다.
// ex) 김남석의 김남석을 다 입력해야 찾음
// findBy(Column).Containing -> 컬럼에서 키워드가 포함된 것을 찾겠다.
// ex) 김남석의 '김'만 입력해도 됨