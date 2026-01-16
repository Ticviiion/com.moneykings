package com.moneykings.gateway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.ASSIGN_ID) // 雪花ID
    private Long loginId;

    private String email;

    private String password;
}
