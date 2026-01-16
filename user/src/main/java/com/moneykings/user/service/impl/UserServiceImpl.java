package com.moneykings.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moneykings.user.dao.User;
import com.moneykings.user.dao.UserVO;
import com.moneykings.user.mapper.UserMapper;
import com.moneykings.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService  {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


}
