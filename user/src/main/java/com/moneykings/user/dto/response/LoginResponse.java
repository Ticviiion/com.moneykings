package com.moneykings.gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class LoginResponse {
    private long userId;
    private String userName;
    private String token;


}
