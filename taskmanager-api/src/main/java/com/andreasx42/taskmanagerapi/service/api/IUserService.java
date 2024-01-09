package com.andreasx42.taskmanagerapi.service.api;

import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.User;

public interface IUserService extends IService<User, UserDto> {

    UserDto create(UserDto userDto);

    User getByName(String username);

}
