package com.gelatoflow.gelatoflow_api.entity;


import com.gelatoflow.gelatoflow_api.dto.auditLogValues.LogValueDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="GF_USERS")
@AttributeOverrides({
        @AttributeOverride(name = "creationDate", column = @Column(name = "USR_CRE_DATE", nullable = false, updatable = false)),
        @AttributeOverride(name = "modificationDate", column = @Column(name = "USR_MOD_DATE")),
        @AttributeOverride(name = "deletionDate", column = @Column(name = "USR_DEL_DATE"))
})
public class UserData extends AuditFieldsData<UserData> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_USR_ID_SEQ")
    @SequenceGenerator(name = "PK_USR_ID_SEQ", sequenceName = "PK_USR_ID_SEQ", initialValue = 50)
    @Column(name = "USR_ID")
    private Long id;

    @Column(name = "USR_FIRST_NAME", length = 100)
    private String firstName;

    @Column(name = "USR_LAST_NAME", length = 100)
    private String lastName;

    @Column(name = "USR_EMAIL", nullable = false)
    private String email;

    @Column(name = "USR_PASSWORD", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "USR_ROL_ID", referencedColumnName = "ROL_ID", updatable = false, insertable = false)
    private RoleData role;

    @Column(name = "USR_ROL_ID", nullable = false)
    private Long roleId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "GF_USERS_SHOPS", joinColumns = {@JoinColumn(name = "USH_USR_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USH_ICS_ID")})
    @MapKey(name = "id")
    private Map<Long, ICShopData> shops = new HashMap<>();


    @OneToMany(mappedBy ="owner", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ICShopData> ownedShops;

    @Column(name = "USR_SALT", nullable = false)
    private String salt;

    public UserData(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserData(String firstName, String lastName, String email, String password, Long roleId) {
        this.firstName= firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
    }

    public UserData(String firstName, String lastName, String email, Long roleId) {
        this.firstName= firstName;
        this.lastName = lastName;
        this.email = email;
        this.roleId = roleId;
    }


    @Override
    public List<LogValueDto> compare(UserData updatedUser) {
        List<LogValueDto> changes = new ArrayList<>();
        if (updatedUser.getFirstName() != null && !firstName.equals(updatedUser.getFirstName())) {
            changes.add(new LogValueDto("firstName", firstName, updatedUser.getFirstName()));
        }

        if (updatedUser.getLastName() != null && !lastName.equals(updatedUser.getLastName())) {
            changes.add(new LogValueDto("lastName", lastName, updatedUser.getLastName()));
        }

        if (updatedUser.getEmail() != null && !email.equals(updatedUser.getEmail())) {
            changes.add(new LogValueDto("email", email, updatedUser.getEmail()));
        }

        if (updatedUser.getRoleId() != null && !roleId.equals(updatedUser.getRoleId())) {
            changes.add(new LogValueDto("role", role.getName(), updatedUser.getRole().getName()));
        }

        if (updatedUser.getPassword() != null && !password.equals(updatedUser.getPassword())) {
            changes.add(new LogValueDto("password", "********", "********"));
        }

        if (updatedUser.getShops().size() > shops.size()) {
            for (Map.Entry<Long, ICShopData> team : updatedUser.getShops().entrySet()) {
                if (!shops.containsKey(team.getKey())) {
                    ICShopData teamData = team.getValue();
                    changes.add(new LogValueDto("teams", null, "User added to team " + teamData.getName() + " with id: " + teamData.getId()));
                }
            }
        } else {
            Map<Long, ICShopData> updatedUserShops = updatedUser.getShops();
            for (Map.Entry<Long, ICShopData> team : shops.entrySet()) {
                if (!updatedUserShops.containsKey(team.getKey())) {
                    ICShopData teamData = team.getValue();
                    changes.add(new LogValueDto("teams", this.toString(), "User deleted from team " + teamData.getName() + " with id: " + teamData.getId()));
                }
            }
        }
        return changes;
    }


    @Override
    public String toString() {
        return "Id: "         + id        + " \n" +
                "FirstName: " + firstName + " \n" +
                "LastName: "  + lastName  + " \n" +
                "Email: "     + email     + " \n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return Objects.equals(email, userData.email)
                && Objects.equals(firstName, userData.firstName)
                && Objects.equals(lastName, userData.lastName)
                && Objects.equals(password, userData.password)
                && Objects.equals(roleId, userData.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, password, roleId);
    }


}
