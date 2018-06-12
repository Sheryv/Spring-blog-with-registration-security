package com.sheryv.example.springblog.controller;

import com.sheryv.example.springblog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.sheryv.example.springblog.Constants;
import com.sheryv.example.springblog.dto.PostDto;
import com.sheryv.example.springblog.model.Post;
import com.sheryv.example.springblog.model.User;
import com.sheryv.example.springblog.repository.UserRepository;
import com.sheryv.example.springblog.service.NotificationService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    NotificationService notificationService;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/postadd", method = RequestMethod.GET)
    public ModelAndView createView() {
        PostDto post = new PostDto();
        ModelAndView modelAndView = new ModelAndView("admin/create");
        modelAndView.addObject("post", post);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/postedit", method = RequestMethod.GET)
    public ModelAndView editView(@RequestParam long id) {
        Optional<Post> byId = postRepository.findById(id);
        PostDto post = new PostDto();
        if (byId.isPresent()) {
            Post p = byId.get();
            post.setId(id);
            post.setContent(p.getContent());
            post.setSummary(p.getSummary());
            post.setTitle(p.getTitle());
        }
        ModelAndView modelAndView = new ModelAndView("admin/edit");
        modelAndView.addObject("post", post);
        return modelAndView;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/postlist", method = RequestMethod.GET)
    public ModelAndView postlist() {
        List<Post> posts = postRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("admin/view");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/userlist", method = RequestMethod.GET)
    public ModelAndView userlist() {
        List<User> users= userRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("admin/users");
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/postdelete")
    public ModelAndView postldelete(@RequestParam long id) {
        postRepository.deleteById(id);
        notificationService.addSuccess("Pomyślnie usunięto wpis");
        return new ModelAndView("redirect:" + "/postlist");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/postadd", method = RequestMethod.POST)
    public ModelAndView create
            (@ModelAttribute("post") @Valid PostDto post,
             BindingResult result, Errors errors) {

        Post p = new Post();
        if (!result.hasErrors()) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UserDetails u = (UserDetails) authentication.getPrincipal();
                p.setTitle(post.getTitle());
                p.setSummary(post.getSummary());
                p.setContent(post.getContent());
                p.setAuthor(userRepository.findByUsername(u.getUsername()));
                p = postRepository.save(p);
                notificationService.addSuccess("Utworzono pomyślnie post: " + p.getTitle());
                return new ModelAndView("redirect:" + "/postlist");
            } catch (Exception e) {
                notificationService.addError("Wystąpił błąd. Spróbuj ponownie!");
                e.printStackTrace();
            }
        } else {
            notificationService.addError(Constants.ERROR_INDICATION);
        }
        return new ModelAndView("admin/create", "post", post);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/postedit", method = RequestMethod.POST)
    public ModelAndView edit(@ModelAttribute("post") @Valid PostDto post,
                             BindingResult result, Errors errors) {
        if (!result.hasErrors()) {
            try {
                Optional<Post> byId = postRepository.findById(post.getId());
                if (byId.isPresent()) {
                    Post p = byId.get();
                    p.setTitle(post.getTitle());
                    p.setSummary(post.getSummary());
                    p.setContent(post.getContent());
                    p = postRepository.save(p);
                    notificationService.addSuccess("Zmodyfikowano pomyślnie post: " + p.getTitle());
                    return new ModelAndView("redirect:" + "/postlist");
                } else {
                    notificationService.addError("Nie znaleziono wpisu o podanym id. Spróbuj ponownie!");
                }
            } catch (Exception e) {
                notificationService.addError("Wystąpił błąd. Spróbuj ponownie!");
                e.printStackTrace();
            }
        } else {
            notificationService.addError(Constants.ERROR_INDICATION);
        }
        return new ModelAndView("admin/edit", "post", post);
    }

}
