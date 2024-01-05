package com.example.springproject.service.api;

import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.User;

public interface IUserService extends IService<User, UserDTO> {

    User create(User user);

    User getByName(String username);

}
