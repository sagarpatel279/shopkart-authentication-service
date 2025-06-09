package com.shopkart.shopkartauthenticationservice.services;


import com.shopkart.shopkartauthenticationservice.models.Role;
import com.shopkart.shopkartauthenticationservice.models.User;

import java.util.List;

public interface IUserRoleServices {
    Role addRole(Role role);
    Role updateRole(Role role);
    Role removeRole(Role role);
    List<Role> assignRoleToUser(User user);
}
