package com.moneykings.user.dao;

import lombok.Data;

import java.util.List;
@Data
public class UserVO extends User{
    private List<Role> roles;
}
