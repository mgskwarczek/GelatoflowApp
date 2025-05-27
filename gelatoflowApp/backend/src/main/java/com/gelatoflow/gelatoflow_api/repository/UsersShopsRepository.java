package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import com.gelatoflow.gelatoflow_api.entity.UsersShopsData;
import com.gelatoflow.gelatoflow_api.service.UserService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersShopsRepository extends JpaRepository<UsersShopsData, Long> {

    UsersShopsData findByShopAndUser(ICShopData shop, UserData user);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM UsersShopsData u " +
            "WHERE u.user.id = :userId AND u.shop.id = :shopId")
    boolean existsByUserIdAndShopId(@Param("userId") Long userId, @Param("shopId") Long shopId);

    @Query("SELECT u FROM UserData u JOIN UsersShopsData us ON u.id = us.user.id " +
            "WHERE us.shop.id = :shopId " +
            "AND (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
            "AND (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<UserData> searchEmployees (@Param("shopId") Long shopId,
                                    @Param("firstName") String firstName,
                                    @Param("lastName") String lastName,
                                    @Param("email") String email);

    @Query("SELECT t FROM ICShopData t JOIN UsersShopsData us ON t.id = us.shop.id WHERE us.user.id = :userId")
    List<ICShopData> findShopsByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM UserData u WHERE u.id NOT IN (" +
            "SELECT us.user.id FROM UsersShopsData us WHERE us.shop.id = :shopId)" +
            "AND (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
            "AND (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<UserData> searchUsersNotInShop(@Param("shopId")Long shopId,
                                        @Param("firstName") String firstName,
                                        @Param("lastName") String lastName,
                                        @Param("email") String email);
}
