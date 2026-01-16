package com.moneykings.gateway.service;


import com.moneykings.gateway.entity.User;

public interface UserService {
    public User findByUserName(String email);
}
