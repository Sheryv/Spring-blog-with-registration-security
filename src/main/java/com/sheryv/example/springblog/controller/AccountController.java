package com.sheryv.example.springblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.sheryv.example.springblog.model.User;
import com.sheryv.example.springblog.repository.UserRepository;

@Controller
public class AccountController {


    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView addPost() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails u = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(u.getUsername());
        return new ModelAndView("account/profile", "user", user);
    }
}
