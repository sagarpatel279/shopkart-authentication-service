package com.shopkart.shopkartauthenticationservice.controllers;

import com.shopkart.shopkartauthenticationservice.services.IUserRoleServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class UserRoleController {
    private IUserRoleServices userRoleServices;

    public UserRoleController(IUserRoleServices userRoleServices){
        this.userRoleServices=userRoleServices;
    }

}
