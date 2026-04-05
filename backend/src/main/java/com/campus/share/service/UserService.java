package com.campus.share.service;

import com.campus.share.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user);
    User login(String username, String password);
    User loginByPhone(String phone, String password);
    User update(User user);
    void delete(Integer id);
    Optional<User> findById(Integer id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
}
