package com.shopkart.shopkartauthenticationservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "roles")
@NoArgsConstructor
public class Role extends BaseModel {
    private String roleName;

    public Role(String name) {
        this.roleName=name;
    }
    public static List<String> toListOfString(List<Role> roles){
        return roles.stream().map(Role::getRoleName).toList();
    }

    public static List<Role> toListOfRole(List<String> roleNames){
        return roleNames.stream().map(Role::new).toList();
    }
}
