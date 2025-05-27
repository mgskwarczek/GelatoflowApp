package com.gelatoflow.gelatoflow_api.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String password;
    private String email;

}