package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.models.Role;
import com.shopkart.shopkartauthenticationservice.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class UserRoleServiceImpl implements IUserRoleServices{
    @Override
    public Role addRole(Role role) {
        return null;
    }

    @Override
    public Role updateRole(Role role) {
        return null;
    }

    @Override
    public Role removeRole(Role role) {
        return null;
    }

    @Override
    public List<Role> assignRoleToUser(User user) {
        return List.of();
    }
}
