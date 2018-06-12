package com.sheryv.example.springblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import com.sheryv.example.springblog.Constants;
import com.sheryv.example.springblog.dto.UserDto;
import com.sheryv.example.springblog.model.User;
import com.sheryv.example.springblog.service.NotificationService;
import com.sheryv.example.springblog.service.UserService;
import com.sheryv.example.springblog.validation.EmailExistsException;
import com.sheryv.example.springblog.validation.PasswordMatches;
import com.sheryv.example.springblog.validation.UsernameExistsException;

import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserService userService;

    @RequestMapping("/")
    public ModelAndView home() {
        return new ModelAndView("index");
    }


    @RequestMapping("/progress")
    public ModelAndView progress() {
        return new ModelAndView("progress");
    }

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("account/log");
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationForm(ModelAndView model) {
        UserDto userDto = new UserDto();
        model.addObject("user", userDto);
        model.setViewName("account/register");
        return model;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView registerUserAccount
            (@ModelAttribute("user") @Valid UserDto accountDto,
             BindingResult result, WebRequest request, Errors errors) {
        User registered = new User();
        if (!result.hasErrors()) {
            try {
                registered = userService.registerNewUserAccount(accountDto);
            } catch (EmailExistsException e) {
                result.rejectValue("email", "emailUsed", "Adres email jest zajęty.");
            } catch (UsernameExistsException e) {
                result.rejectValue("username", "usernameUsed", "Nazwa użytkownika jest zajęta.");
            }
        }
        if (result.hasErrors()) {
            for (ObjectError er : result.getGlobalErrors()) {
                if (er.getCode().contains(PasswordMatches.class.getSimpleName())) {
                    result.rejectValue("matchingPassword", er.getCode(), er.getDefaultMessage());
                    break;
                }
            }
            notificationService.addError(Constants.ERROR_INDICATION);
            return new ModelAndView("account/register", "user", accountDto);
        } else {
            notificationService.addSuccess("Zarejestrowno pomyślnie!");
            return new ModelAndView("account/log");
        }
    }


}
