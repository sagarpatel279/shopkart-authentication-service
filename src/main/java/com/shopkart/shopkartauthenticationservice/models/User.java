package com.shopkart.shopkartauthenticationservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "users")
public class User extends BaseModel{
    private String email;
    private String passwordSalt;
    @ManyToMany
    private List<Role> roles;
}
