package com.gelatoflow.gelatoflow_api.dto.user;

import com.gelatoflow.gelatoflow_api.entity.UserData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreateDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long roleId;
    private String password;

    public static UserData toEntity(UserCreateDto userCreateDto) {
        return new UserData(
                userCreateDto.getFirstName(),
                userCreateDto.getLastName(),
                userCreateDto.getEmail(),
                userCreateDto.getPassword(),
                userCreateDto.getRoleId()
        );
    }
}
