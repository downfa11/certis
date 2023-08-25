package Certis.Web.controller;
import Certis.Web.Service.BoardService;
import Certis.Web.Service.CommentService;
import Certis.Web.auth.PrincipalDetails;
import Certis.Web.entity.BoardTable;
import Certis.Web.entity.Comment;
import Certis.Web.entity.Role;
import Certis.Web.entity.User;
import Certis.Web.repository.BoardRepository;
import Certis.Web.repository.CommentRepository;
import Certis.Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller

public class BoardController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private BoardRepository boardRepository;

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

        board.setAuthor(user);
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
    public String boardView(Model model,Integer id,@AuthenticationPrincipal PrincipalDetails principalDetails){
        if(!AuthPage())
            return "redirect:/";
        BoardTable board = boardService.boardView(id);
        model.addAttribute("board",board);
        List<Comment> comments = commentRepository.findByBoard(board);
        model.addAttribute("comments", comments);
        model.addAttribute("curComment",null);

        Long author = board.getAuthor().getId();
        User user = principalDetails.getUser();
        boolean isAuthor = author==user.getId()||user.getRole()== Role.ROLE_MANAGER||user.getRole()== Role.ROLE_ADMIN;
        model.addAttribute("isAuthor", isAuthor);

        Map<Integer, Boolean> commentAuthorMap = new HashMap<>();
        for (Comment comment : comments) {
            commentAuthorMap.put(comment.getId(), author==user.getId());
        }
        model.addAttribute("commentAuthorMap", commentAuthorMap);

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
        Long author = board.getAuthor().getId();
        User user = principalDetails.getUser();
        if(author==user.getId()||user.getRole()== Role.ROLE_MANAGER||user.getRole()== Role.ROLE_ADMIN)
        {
            List<Comment> comments = commentRepository.findByBoard(board);
            for (Comment comment : comments) {
                commentRepository.delete(comment);
            }
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
        boardTemp.setAuthor(user);

        boardService.Write(boardTemp);
        return "redirect:/board/list";
    }


    public boolean AuthPage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails;
    }

    @PostMapping("board/view/{id}/post-comment")
    public String addComment(@PathVariable Integer id, @RequestParam String content,@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Optional<BoardTable> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            BoardTable board = boardOptional.get();
            User user = principalDetails.getUser();
            Comment comment = new Comment();
            comment.setBoard(board);
            comment.setContent(content);
            comment.setAuthor(user);
            commentRepository.save(comment);
        }
        return "redirect:/board/view?id=" + id;
    }
    @GetMapping("board/view/{id}/modify-comment/{commentId}")
    public String ReceiveComment( // 좀 지저분함
            @PathVariable Integer id,
            @PathVariable Integer commentId,
            Model model,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        BoardTable board = boardService.boardView(id);
        model.addAttribute("board",board);
        List<Comment> comments = commentRepository.findByBoard(board);
        model.addAttribute("comments", comments);
        model.addAttribute("curComment",null);

        Long author = board.getAuthor().getId();
        User user = principalDetails.getUser();
        boolean isAuthor = author==user.getId()||user.getRole()== Role.ROLE_MANAGER||user.getRole()== Role.ROLE_ADMIN;
        model.addAttribute("isAuthor", isAuthor);

        Map<Integer, Boolean> commentAuthorMap = new HashMap<>();
        for (Comment comment : comments) {
            commentAuthorMap.put(comment.getId(), author==user.getId());
        }

        model.addAttribute("commentAuthorMap", commentAuthorMap);
        Comment comment = commentService.commentView(commentId);
        model.addAttribute("curComment",isAuthor ?comment :null);


        System.out.println(isAuthor);
        return "boardview";
    }
    @PostMapping("board/view/{id}/modify-comment/{commentId}")
    public String modifyComment(
            @PathVariable Integer id,
            @PathVariable Integer commentId,
            @RequestParam String content,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Comment comment = commentService.commentView(commentId);

        if (comment != null) {
            User user = principalDetails.getUser();
            Long author = comment.getAuthor().getId();
            if (author.equals(user.getId()) || user.getRole() == Role.ROLE_MANAGER || user.getRole() == Role.ROLE_ADMIN) {
                comment.setContent(content);
                commentRepository.save(comment);
            }
        }

        return "redirect:/board/view?id=" + id;
    }
    @GetMapping("board/view/{id}/delete-comment/{commentId}")
    public String deleteComment(@PathVariable Integer id, @PathVariable Integer commentId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Comment comment = commentService.commentView(commentId);

        if (comment != null) {
            Long authorId = comment.getAuthor().getId();
            User currentUser = principalDetails.getUser();

            if (authorId.equals(currentUser.getId()) || currentUser.getRole() == Role.ROLE_MANAGER || currentUser.getRole() == Role.ROLE_ADMIN) {
                commentRepository.delete(comment);
            }
        }

        return "redirect:/board/view?id=" + id;
    }
}


// JPA Repository
// findBy(Column name) -> 컬럽에서 키워드를 넣어서 찾는다.
// ex) 김남석의 김남석을 다 입력해야 찾음
// findBy(Column).Containing -> 컬럼에서 키워드가 포함된 것을 찾겠다.
// ex) 김남석의 '김'만 입력해도 됨