package com.gelatoflow.gelatoflow_api.controller;

import com.gelatoflow.gelatoflow_api.dto.auth.AuthResponseDto;
import com.gelatoflow.gelatoflow_api.dto.user.LoginRequest;
import com.gelatoflow.gelatoflow_api.dto.user.UserCreateDto;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.security.JWTGenerator;
import com.gelatoflow.gelatoflow_api.service.PasswordService;
import com.gelatoflow.gelatoflow_api.service.PasswordUtil;
import com.gelatoflow.gelatoflow_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.gelatoflow.gelatoflow_api.controller.UserController.isValidEmail;
import static com.gelatoflow.gelatoflow_api.exception.ErrorCode.USER_NOT_FOUND;
import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

@RestController
@RequestMapping("/auth") public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private JWTGenerator jwtGenerator;

    private final Logger logger = LogManager.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = verifyLogging(loginRequest.getEmail(), loginRequest.getPassword());

        if (authentication != null) {
            logger.info("Login successful for email: {}", loginRequest.getEmail());

            String token = jwtGenerator.generateToken(authentication);
            return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
        } else {
            logger.warn("Invalid email or password for email: {}", loginRequest.getEmail());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    private Authentication verifyLogging(String email, String password) {
        UserData user = userService.getByEmail(email);

        if (user != null) {
            String storedSalt = user.getSalt();
            String storedHash = user.getPassword();
            try {
                String hashToCheck = PasswordUtil.hashPassword(password, storedSalt);
                if (hashToCheck.equals(storedHash)) {
                    Authentication authentication = authenticationManager.authenticate
                            (new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    return authentication;
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                logger.error("Hashing failed: {} - {}", e.getClass().getName(), e.getMessage(), e);
                throw throwAndLogError(logger, new ApplicationException(ErrorCode.ERROR_DURING_HASHING, e));
            }
        }
        return null;
    }



    @PostMapping("/register")
    public ResponseEntity<ErrorCode> register(@RequestBody UserCreateDto userCreateDto) throws NoSuchAlgorithmException, InvalidKeySpecException {

        validateUserCreateDto(userCreateDto);

        if (userService.existsByEmail(userCreateDto.getEmail())) {
            throw throwAndLogError(logger, new ApplicationException(USER_NOT_FOUND, userCreateDto.getId()));
        }
        logger.info("UserCreateDto successfully validated.");
        if (!isValidEmail(userCreateDto.getEmail())) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.INVALID_EMAIL_FORMAT, userCreateDto.getEmail()));
        }

        passwordService.validatePassword(userCreateDto.getPassword());
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(userCreateDto.getPassword(), salt);

        UserData userData = UserCreateDto.toEntity(userCreateDto);

        userData.setPassword(hash);
        userData.setSalt(salt);

        userData.setRoleId(userCreateDto.getRoleId());

        userService.createUser(userData);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateUserCreateDto(UserCreateDto userCreateDto) {
        if (userCreateDto.getFirstName() == null || userCreateDto.getFirstName().isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "firstName"));
        }

        if (userCreateDto.getFirstName().length() > 100) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.TOO_LONG_VALUE, "firstName", userCreateDto.getFirstName().length(), 100));
        }

        if (userCreateDto.getLastName() == null || userCreateDto.getLastName().isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "lastName"));
        }

        if (userCreateDto.getLastName().length() > 100) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.TOO_LONG_VALUE, "lastName", userCreateDto.getLastName().length(), 100));
        }

        if (userCreateDto.getEmail() == null || userCreateDto.getEmail().isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "email"));
        }
        if (!isValidEmail(userCreateDto.getEmail())) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.INVALID_EMAIL_FORMAT, "email"));
        }
    }
}



