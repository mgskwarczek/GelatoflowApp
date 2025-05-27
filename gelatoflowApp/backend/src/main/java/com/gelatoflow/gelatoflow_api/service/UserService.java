package com.gelatoflow.gelatoflow_api.service;

import com.gelatoflow.gelatoflow_api.controller.UserController;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.repository.ICShopRepository;
import com.gelatoflow.gelatoflow_api.repository.UserRepository;
import com.gelatoflow.gelatoflow_api.repository.UsersShopsRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

import static com.gelatoflow.gelatoflow_api.controller.UserController.isValidEmail;
import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ICShopRepository icShopRepository;

    @Autowired
    private UsersShopsRepository usersShopsRepository;

    @Autowired
    private AuditLogHeaderService auditLogHeaderService;

    @Autowired
    private EntityManager entityManager;

    private final Logger logger = LogManager.getLogger(UserController.class);


    @Transactional
    protected void saveUser(UserData user, ApplicationException exception) {
        try {
            userRepository.save(user);
        } catch (ApplicationException e) {
            logger.error("Failed to create user: {}", e.getMessage(), e);
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_CREATE_USER, e));
        }
    }

    public UserData getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.USER_NOT_FOUND, id)));
    }

    public List<UserData> getAll() {
        return userRepository.findAll();
    }


    public Page<UserData> getAllPaginated(org.springframework.data.domain.Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<UserData> getByFirstNameAndLastNameAndEmail(String firstName, String lastName, String email) {
        List<UserData> userData = userRepository.findByFirstNameAndLastNameAndEmail(firstName, lastName, email);

        if (userData.isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NO_USERS_FOUND_WITH_CRITERIA));
        }

        return userData;
    }

    public UserData getByEmail(String email) {
        if (!isValidEmail(email)) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.INVALID_EMAIL_FORMAT, email));
        }

        UserData userData = userRepository.findByEmail(email);

        if (userData == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NO_USERS_FOUND_WITH_CRITERIA));
        }

        return userData;
    }

    @Transactional
    public Long createUser(UserData userData) {
        if (userRepository.findByEmail(userData.getEmail()) != null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.EMAIL_IS_TAKEN, userData.getEmail()));
        }

        userData.setCreationDate(LocalDateTime.now());
        saveUser(userData, new ApplicationException(ErrorCode.FAILED_TO_CREATE_USER));

        logger.info("User with id {} has been successfully created.", userData.getId());
        return userData.getId();
    }

    public void changePassword(UserData updatedUser) {
        updatedUser.setModificationDate(LocalDateTime.now());

        entityManager.detach(updatedUser);
        UserData user = getById(updatedUser.getId());
        auditLogHeaderService.createObjectChange(user, updatedUser, findAdmin(), user.getClass().getSimpleName(), user.getId());
        saveUser(updatedUser, new ApplicationException(ErrorCode.FAILED_TO_CHANGE_PASSWORD));

        logger.info("User password successfully changed.");
    }

    @Transactional
    public void updateUser(UserData updatedUser) {
        updatedUser.setModificationDate(LocalDateTime.now());
        updatedUser.getShops();
        entityManager.detach(updatedUser);

        UserData user = getById(updatedUser.getId());
        auditLogHeaderService.createObjectChange(user, updatedUser, findAdmin(), user.getClass().getSimpleName(), user.getId());

        saveUser(updatedUser, new ApplicationException(ErrorCode.FAILED_TO_UPDATE_USER));

        logger.info("User with id {} has been successfully updated.", updatedUser.getId());
    }

    public void deleteUser(Long id) {
        UserData userData = getById(id);

        userData.setDeletionDate(LocalDateTime.now());
        saveUser(userData, new ApplicationException(ErrorCode.FAILED_TO_DELETE_USER));

        logger.info("User with id {} was successfully deleted.", id);
    }

    public List<UserData> searchUsersNotInShop(Long shopId, String firstName, String lastName, String email) {
        icShopRepository.findById(shopId)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.SHOP_NOT_FOUND, shopId)));

        List<UserData> usersNotInShop = usersShopsRepository.searchUsersNotInShop(shopId, firstName, lastName, email);

        if (usersNotInShop.isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NO_USERS_FOUND_WITH_CRITERIA));
        }

        return usersNotInShop;
    }

    public Page<UserData> searchUsers(
            String firstName, String lastName, String email, String roleName,
            LocalDateTime creationLowerDate, LocalDateTime creationUpperDate,
            LocalDateTime modificationLowerDate, LocalDateTime modificationUpperDate,
            LocalDateTime deletionLowerDate, LocalDateTime deletionUpperDate,
            Pageable pageable
    ) {
        return userRepository.searchUsers(
                firstName, lastName, email, roleName,
                creationLowerDate, creationUpperDate,
                modificationLowerDate, modificationUpperDate,
                deletionLowerDate, deletionUpperDate, pageable);
    }

    public List<UserData> findByRole(String roleName) {
        return userRepository.findByRoleName(roleName.toUpperCase());
    }

    public Long findAdmin() {
        List<UserData> admins = findByRole("admin");
        if (!admins.isEmpty()) {
            return admins.getFirst().getId();
        } else {
            logger.error("Could not find any admin for creatorId.");
            throw new NullPointerException();
        }
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }



}


