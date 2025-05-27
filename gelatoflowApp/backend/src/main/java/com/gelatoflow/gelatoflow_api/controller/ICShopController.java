package com.gelatoflow.gelatoflow_api.controller;

import com.gelatoflow.gelatoflow_api.dto.user.*;
import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.service.ICShopService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;


@RestController
@RequestMapping("/shops")
public class ICShopController {

    private static final Logger logger = LogManager.getLogger(ICShopController.class);

    @Autowired
    private ICShopService icShopService;

    @GetMapping("/{id}")
    public ICShopDto getById(@PathVariable("id") Long id) {
        validateId(id);
        return ICShopDto.toDto(icShopService.getById(id));
    }

    public void validateId(Long id) {
        if (id == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "id"));
        }
    }

    @GetMapping("/all")
    public List<ICShopDto> getAll() {
        return icShopService.getAll()
                .stream()
                .map(ICShopDto::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public Set<ICShopDto> getByName(@RequestParam("name") String name) {
        validateShopName(name);
        logger.info("Correctly validated name for search.");

        Set<ICShopDto> shops = icShopService.getByName(name)
                .stream()
                .map(ICShopDto::toDto)
                .collect(Collectors.toSet());

        if(shops.isEmpty()) {
            throw new ApplicationException(ErrorCode.NO_SHOPS_FOUND_WITH_CRITERIA);
        }

        return shops;
    }

    private void validateShopName(String name) {
        if (name == null || name.isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "name"));
        }

        if (name.length() > 100) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.TOO_LONG_VALUE, "name", name.length(), 100));
        }
    }

    @PostMapping("/admin/create")
    public Long createShop(@RequestBody ICShopCreateDto icShopCreateDto) {
        validateShopCreateDto(icShopCreateDto);

        ICShopData shopData = ICShopCreateDto.toEntity(icShopCreateDto);

        return icShopService.createShop(shopData);
    }

    private void validateShopCreateDto(ICShopCreateDto icShopCreateDto) {
        if (icShopCreateDto.getName() == null || icShopCreateDto.getName().isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "name"));
        }

        if (icShopCreateDto.getName().length() > 100) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.TOO_LONG_VALUE, "name", icShopCreateDto.getName().length(), 100));
        }
    }

    @PutMapping("/admin/update")
    public void updateTeam(@RequestBody ICShopUpdateDto icShopUpdateDto) {
        validateTeamUpdateDto(icShopUpdateDto);
        logger.info("Correctly validated icShopUpdateDto.");

        ICShopData shopData= icShopService.getById(icShopUpdateDto.getId());

        try {
            shopData.setName(icShopUpdateDto.getName());
        } catch (Exception e) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.SHOP_NAME_IS_TAKEN));
        }

        icShopService.updateShop(shopData);
    }

    private void validateTeamUpdateDto(ICShopUpdateDto icShopUpdateDto) {
        if (icShopUpdateDto.getId() == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "id"));
        }
        if (icShopUpdateDto.getName() != null) {
            if (icShopUpdateDto.getName().length() > 100) {
                throw throwAndLogError(logger, new ApplicationException(ErrorCode.TOO_LONG_VALUE, "name", icShopUpdateDto.getName().length(), 100));
            }
        }
    }

    @DeleteMapping("/admin/delete")
    public void deleteTeam(@RequestParam("id") Long id) {
        validateId(id);
        ICShopData shop = icShopService.getById(id);
        icShopService.deleteShop(shop);
    }

    @PostMapping("/addEmployee")
    public ResponseEntity<String> addUserToShop(@RequestBody UserShopDto userShopDto) {
        validateUserShopDto(userShopDto);

        try {
            icShopService.addUserToShop(userShopDto.getShopId(), userShopDto.getUserId());
            return ResponseEntity.ok("User has been successfully added to the shop!");
        } catch(Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while adding user to shop.");
        }
    }

    @DeleteMapping("/removeEmployee")
    public ResponseEntity<Void> removeUserFromShop(@RequestBody UserShopDto userShopDto) {
        validateUserShopDto(userShopDto);
        try {
            icShopService.removeUserFromShop(userShopDto.getShopId(), userShopDto.getUserId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private void validateUserShopDto(UserShopDto userShopDto) {

        if (userShopDto == null || userShopDto.getShopId() == null || userShopDto.getUserId() == null) {
            logger.error("Validation failed. UserShopDto: " + userShopDto);
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_ADD_USER_TO_SHOP));
        }
    }

    @GetMapping("/searchEmployees")
    public List<UserDto> searchTeamMembers(
            @RequestParam("shopId") Long shopId,
            @RequestParam(required = false, name = "firstName") String firstName,
            @RequestParam(required = false, name = "lastName") String lastName,
            @RequestParam(required = false, name = "email") String email) {

        List<UserData> usersInShops = icShopService.searchEmployees(shopId, firstName, lastName, email);
        return usersInShops
                .stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }
}
















