package com.gelatoflow.gelatoflow_api.dto.user;

import com.gelatoflow.gelatoflow_api.entity.UserData;

public record UserSendDto(
        String firstName,
        String lastName,
        String email) {

    public static UserSendDto toDto(UserData user) {
        return new UserSendDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public static UserData toEntity(UserSendDto userSendDto) {
        return new UserData(
                userSendDto.firstName(),
                userSendDto.lastName(),
                userSendDto.email()
        );
    }
}