package com.example.NewBuildingFinance.dto.auth;

import com.example.NewBuildingFinance.entities.auth.User;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
    private String name;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
    private String surname;
    @NotEmpty(message = "Must not be empty")
    private String phone;
    @Email(message = "Email must be valid")
    @NotEmpty(message = "Must not be empty")
    private String username; // email
    private String latestUsername;
    private boolean active;
    @NotNull(message = "Choose something")
    private RoleDto role;

    public User build(){
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setSurname(surname);
        user.setPhone(phone);
        user.setUsername(username);
        user.setActive(active);
        if(role != null) {
            user.setRole(role.build());
        }
        return user;
    }
}
