package com.gelatoflow.gelatoflow_api.dto.user;

import com.gelatoflow.gelatoflow_api.entity.UserData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long roleId;

    private UserUpdateDto(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public static UserUpdateDto toDto(UserData userData) {
        return new UserUpdateDto(
                userData.getId(),
                userData.getFirstName(),
                userData.getLastName(),
                userData.getEmail()
        );
    }

    public static UserData toEntity(UserUpdateDto userUpdateDto) {
        UserData userData = new UserData(
                userUpdateDto.getFirstName(),
                userUpdateDto.getLastName(),
                userUpdateDto.getEmail(),
                userUpdateDto.getRoleId()
        );
        userData.setModificationDate(LocalDateTime.now());
        return userData;
    }

}
