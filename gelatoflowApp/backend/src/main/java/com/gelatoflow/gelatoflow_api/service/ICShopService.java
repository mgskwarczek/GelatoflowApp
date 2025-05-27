package com.gelatoflow.gelatoflow_api.service;

import com.gelatoflow.gelatoflow_api.dto.user.ICShopDto;
import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import com.gelatoflow.gelatoflow_api.entity.UsersShopsData;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.repository.ICShopRepository;
import com.gelatoflow.gelatoflow_api.repository.UsersShopsRepository;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

@Service
public class ICShopService {
    @Autowired
    private AuditLogHeaderService auditLogHeaderService;

    private final Logger logger = LogManager.getLogger(ICShopService.class);

    @Autowired
    private ICShopRepository icShopRepository;

    @Autowired
    private UsersShopsRepository usersShopsRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    public ICShopData getById(Long id) {
        return icShopRepository.findById(id)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.SHOP_NOT_FOUND, id)));
    }

    public List<ICShopData> getAll() {
        return icShopRepository.findAllActiveShops();
    }

    public Set<ICShopData> getByName(String name) {
        return icShopRepository.findByName(name);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_LEADER')")
    public Long createShop(ICShopData icShopData) {
        if (!getByName(icShopData.getName()).isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.SHOP_NAME_IS_TAKEN, icShopData.getName()));
        }

        icShopData.setCreationDate(LocalDateTime.now());

        try {
            icShopRepository.save(icShopData);
        } catch (Exception e) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_CREATE_SHOP));
        }

        logger.info("Shop {} successfully created.", icShopData.getName());
        return icShopData.getId();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_LEADER')")
    public void updateShop(ICShopData icShopData) {
        icShopData.setModificationDate(LocalDateTime.now());

        try {
            icShopRepository.save(icShopData);
        } catch (Exception e) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_UPDATE_SHOP));
        }

        logger.info("Shop {} successfully updated.", icShopData.getName());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_LEADER')")
    public void deleteShop(ICShopData icShopData) {
        icShopData.setDeletionDate(LocalDateTime.now());

        try {
            icShopRepository.save(icShopData);
        } catch (ApplicationException e) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_DELETE_SHOP));
        }

        logger.info("Shop {} with id {} successfully deleted.", icShopData.getName(), icShopData.getId());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_LEADER')")
    public void addUserToShop(Long shopId, Long userId) {
        ICShopData icShopData = getById(shopId);

        UserData updatedUser = userService.getById(userId);

        boolean userInShop = usersShopsRepository.existsByUserIdAndShopId(userId, shopId);
        if (userInShop) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.USER_ALREADY_IN_SHOP));
        }

        UsersShopsData usersShopsData = new UsersShopsData();
        usersShopsData.setUser(updatedUser);
        usersShopsData.setShop(icShopData);

        try {
            updatedUser.getShops().put(icShopData.getId(), icShopData);
            entityManager.detach(updatedUser);

            UserData user = userService.getById(userId);
            auditLogHeaderService.createObjectChange(user, updatedUser, userService.findAdmin(), user.getClass().getSimpleName(), userId);
            usersShopsRepository.save(usersShopsData);
        } catch (ApplicationException e) {
            logger.error("Error occurred while adding user to shop", e);
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_ADD_USER_TO_SHOP));
        }

        logger.info("User {} with id {} was successfully added to shop with id {}.", updatedUser.getFirstName() + " " + updatedUser.getLastName(), updatedUser.getId(), icShopData.getId());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_LEADER')")
    public void removeUserFromShop(Long shopId, Long userId) {
        ICShopData icShopData = getById(shopId);
        UserData deletedUser = userService.getById(userId);
        UsersShopsData usersShopsData = usersShopsRepository.findByShopAndUser(icShopData, deletedUser);

        try {
            deletedUser.getShops().remove(shopId);
            entityManager.detach(deletedUser);

            UserData user = userService.getById(userId);
            auditLogHeaderService.createObjectChange(user, deletedUser, userService.findAdmin(), user.getClass().getSimpleName(), user.getId());

            usersShopsRepository.delete(usersShopsData);
        } catch (ApplicationException e) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_REMOVE_USER_FROM_SHOP));
        }

        logger.info("User with id {} was successfully removed from shop with id {}.", deletedUser.getId(), icShopData.getId());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_LEADER')")
    public List<UserData> searchEmployees(Long shopId, String firstName, String lastName, String email) {
        getById(shopId);

        List<UserData> usersInShops = usersShopsRepository.searchEmployees(shopId, firstName, lastName, email);

        if (usersInShops.isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NO_USERS_FOUND_WITH_CRITERIA));
        }

        return usersInShops;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_LEADER')")
    public List<ICShopDto> findShopsByUserId(Long userId) {
        List<ICShopData> shops = usersShopsRepository.findShopsByUserId(userId);
        return shops
                .stream()
                .map(ICShopDto::toDto)
                .collect(Collectors.toList());
    }

}
