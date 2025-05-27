package com.gelatoflow.gelatoflow_api.dto.auth;

import lombok.Data;

@Data
public class AuthResponseDto {

    private String accessToken;
    private String tokenType = "Bearer";


    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
