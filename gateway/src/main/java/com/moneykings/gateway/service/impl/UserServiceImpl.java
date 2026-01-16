package com.moneykings.gateway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moneykings.gateway.entity.User;
import com.moneykings.gateway.mapper.UserMapper;
import com.moneykings.gateway.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User findByUserName(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        try {
            return userMapper.selectOne(wrapper);
        }catch (Exception e){
            return new User(null,null,null);
        }
    }
}
