package com.sheryv.example.springblog.service;

import com.sheryv.example.springblog.dto.UserDto;
import com.sheryv.example.springblog.model.User;
import com.sheryv.example.springblog.validation.UsernameExistsException;


public interface UserService {

    User registerNewUserAccount(UserDto accountDto) throws UsernameExistsException;

}
