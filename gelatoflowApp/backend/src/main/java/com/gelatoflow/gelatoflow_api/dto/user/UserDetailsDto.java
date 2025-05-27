package com.gelatoflow.gelatoflow_api.dto.user;

import com.gelatoflow.gelatoflow_api.entity.UserData;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDetailsDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String roleName;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private LocalDateTime deletionDate;

    private UserDetailsDto(Long id, String firstName, String lastName, String email, String roleName, LocalDateTime creationDate, LocalDateTime modificationDate, LocalDateTime deletionDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roleName = roleName;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.deletionDate = deletionDate;
    }


    public static UserDetailsDto toDto(UserData userData) {
        return new UserDetailsDto(
                userData.getId(),
                userData.getFirstName(),
                userData.getLastName(),
                userData.getEmail(),
                userData.getRole().getName(),
                userData.getCreationDate(),
                userData.getModificationDate(),
                userData.getDeletionDate()
        );
    }
}