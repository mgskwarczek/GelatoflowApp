package com.gelatoflow.gelatoflow_api.dto.user;

import com.gelatoflow.gelatoflow_api.entity.UserData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String roleName;

    private UserDto(Long id, String firstName, String lastName, String email, String roleName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roleName = roleName;
    }

    public static UserDto toDto(UserData userData) {
        return new UserDto(
                userData.getId(),
                userData.getFirstName(),
                userData.getLastName(),
                userData.getEmail(),
                userData.getRole().getName()
        );
    }
}
