package com.sheryv.example.springblog.controller;

import com.sheryv.example.springblog.repository.CommentRepository;
import com.sheryv.example.springblog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.sheryv.example.springblog.Constants;
import com.sheryv.example.springblog.dto.CommentDto;
import com.sheryv.example.springblog.model.Comment;
import com.sheryv.example.springblog.model.Post;
import com.sheryv.example.springblog.model.User;
import com.sheryv.example.springblog.repository.UserRepository;
import com.sheryv.example.springblog.service.NotificationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class BlogController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/blog")
    public ModelAndView blog(@RequestParam(name = "page", required = false) String pageName) {
        int page = 0;
        if (pageName != null) {
            try {
                page = Integer.parseInt(pageName);
            } catch (NumberFormatException e) {
            }
        }
        Page<Post> all = postRepository.findAll(PageRequest.of(page, 10, Sort.by("publishedAt").descending()));
        ModelAndView modelAndView = new ModelAndView("blog");
        modelAndView.addObject("page", page);
        modelAndView.addObject("pageCount", all.getTotalPages()-1);
        modelAndView.addObject("posts", all.getContent());
        return modelAndView;
    }


    @RequestMapping(value = "/postview", method = RequestMethod.GET)
    public ModelAndView view(@RequestParam(name = "id") Long id, HttpServletRequest request) {
        Post post = postRepository.findById(id).get();
        ModelAndView modelAndView = new ModelAndView("blog/post");
        post.setComments(post.getComments().stream().sorted().collect(Collectors.toList()));
        modelAndView.addObject("post", post);
        CommentDto commentDto = new CommentDto(id);
        modelAndView.addObject("comment", commentDto);
        return modelAndView;
    }

    @RequestMapping(value = "/postview", method = RequestMethod.POST)
    public ModelAndView commentAdd(@ModelAttribute("comment") @Valid CommentDto comment,
                                   BindingResult result, HttpServletRequest request) {
        Post post = postRepository.findById(comment.getId()).get();
        ModelAndView modelAndView = new ModelAndView("blog/post");
        if (!result.hasErrors()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails u = (UserDetails) authentication.getPrincipal();
            Comment c = new Comment(post, comment.getContent(), userRepository.findByUsername(u.getUsername()));
            post.getComments().add(c);
            commentRepository.save(c);
            postRepository.save(post);
            notificationService.addSuccess("Pomyślnie dodano komentarz!");
            modelAndView = new ModelAndView("redirect:" + "/postview?id=" + post.getId());
            return modelAndView;
        } else {
            notificationService.addError(Constants.ERROR_INDICATION);
            modelAndView.addObject("comment", comment);
        }
        modelAndView.addObject("post", post);
        return modelAndView;
    }


    @RequestMapping(value = "/postcommentedit", method = RequestMethod.GET)
    public ModelAndView commentEditView(@RequestParam(name = "id") Long id) {
        Comment comment = commentRepository.findById(id).get();
        ModelAndView modelAndView = new ModelAndView("blog/comment_edit");
        CommentDto commentDto = new CommentDto(id);
        commentDto.setContent(comment.getContent());
        modelAndView.addObject("comment", commentDto);
        return modelAndView;
    }


    @RequestMapping(value = "/postcommentedit", method = RequestMethod.POST)
    public ModelAndView commentEdit(@ModelAttribute("comment") @Valid CommentDto comment,
                                    BindingResult result, HttpServletRequest request) {
        Optional<Comment> com = commentRepository.findById(comment.getId());
        if (!com.isPresent()) {
            notificationService.addError(Constants.NO_ACCESS_TO_EDIT_COMMENT);
            return new ModelAndView("redirect:" + "/blog");
        }
        Comment c = com.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails u = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(u.getUsername());
        ModelAndView modelAndView = null;
        Post post = c.getPost();
        modelAndView = new ModelAndView("redirect:" + "/postview?id=" + post.getId());
        if (c.getAuthor() != user && !user.isAdmin()) {
            notificationService.addError(Constants.NO_ACCESS_TO_EDIT_COMMENT);
            return modelAndView;
        }
        if (!result.hasErrors()) {
            c.setContent(comment.getContent());
            commentRepository.save(c);
            notificationService.addSuccess("Pomyślnie zmodyfikowano komentarz!");
            return modelAndView;
        } else {
            modelAndView = new ModelAndView("blog/comment_edit");
            notificationService.addError(Constants.ERROR_INDICATION);
            modelAndView.addObject("comment", comment);
        }
        return modelAndView;
    }


    @RequestMapping(value = "/postcommentdelete", method = RequestMethod.GET)
    public ModelAndView commentDelete(@RequestParam(name = "id") Long id, @RequestParam(name = "post_id") Long postId) {
        Optional<Comment> com = commentRepository.findById(id);
        ModelAndView modelAndView = new ModelAndView("redirect:" + "/postview?id=" + postId);
        if (!com.isPresent()) {
            notificationService.addError(Constants.NO_ACCESS_TO_DELETE_COMMENT);
            return modelAndView;
        }
        Comment c = com.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails u = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(u.getUsername());
        if (c.getAuthor() != user && !user.isAdmin()) {
            notificationService.addError(Constants.NO_ACCESS_TO_DELETE_COMMENT);
            return modelAndView;
        }
        notificationService.addSuccess("Pomyślnie usunięto komentarz!");
        commentRepository.deleteById(id);
        return modelAndView;
    }
}
