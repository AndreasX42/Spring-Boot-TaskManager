package com.example.springproject.service.api;

import com.example.springproject.dto.UserDto;
import com.example.springproject.entity.User;

public interface IUserService extends IService<User, UserDto> {

    UserDto create(UserDto userDto);

    User getByName(String username);

}
