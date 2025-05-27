package com.gelatoflow.gelatoflow_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDto {

    private Long userId;
    private String currentPassword;
    private String newPassword;

}
