package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<UserData, Long> {
    @Query("SELECT u FROM UserData u WHERE LOWER(u.email) = LOWER(:email) AND u.deletionDate IS NULL")
    UserData findByEmail(@Param(value = "email") String email);

    @Query("SELECT u FROM UserData u WHERE "
            + "(:firstName IS NULL OR :firstName = '' OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND "
            + "(:lastName IS NULL OR :lastName = ''  OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND "
            + "(:email IS NULL OR :email = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<UserData> findByFirstNameAndLastNameAndEmail(@Param(value = "firstName") String firstName,
                                                      @Param(value = "lastName") String lastName,
                                                      @Param(value = "email") String email);

    @Query("SELECT u FROM UserData u JOIN u.role r WHERE r.name =:roleName")
    List<UserData> findByRoleName(@Param ("roleName") String roleName);


    @Query("SELECT u FROM UserData u JOIN u.role r WHERE "
            + "(:firstName IS NULL OR :firstName = '' OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND "
            + "(:lastName IS NULL OR :lastName = '' OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND "
            + "(:email IS NULL OR :email = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND "
            + "(:roleName IS NULL OR :roleName = '' OR r.name =: roleName) AND "
            + "(:creationLowerDate IS NULL OR u.creationDate >= :creationLowerDate) AND "
            + "(:creationUpperDate IS NULL OR u.creationDate <= :creationUpperDate) AND "
            + "(:modificationLowerDate IS NULL OR u.modificationDate >= :modificationLowerDate) AND "
            + "(:modificationUpperDate IS NULL OR u.modificationDate <= :modificationUpperDate) AND "
            + "(:deletionLowerDate IS NULL OR u.deletionDate >= :deletionLowerDate) AND "
            + "(:deletionUpperDate IS NULL OR u.deletionDate <= :deletionUpperDate)")
    Page<UserData> searchUsers(@Param("firstName") String firstName,
                               @Param("lastName") String lastName,
                               @Param("email") String email,
                               @Param("roleName") String roleName,
                               @Param("creationLowerDate") LocalDateTime creationLowerDate,
                               @Param("creationUpperDate") LocalDateTime creationUpperDate,
                               @Param("modificationLowerDate") LocalDateTime modificationLowerDate,
                               @Param("modificationUpperDate") LocalDateTime modificationUpperDate,
                               @Param("deletionLowerDate") LocalDateTime deletionLowerDate,
                               @Param("deletionUpperDate") LocalDateTime deletionUpperDate,
                               Pageable pageable);

    boolean existsByEmail(@Param("email") String email);
}
