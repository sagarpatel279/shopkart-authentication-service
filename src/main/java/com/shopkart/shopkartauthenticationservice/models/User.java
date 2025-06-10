package com.shopkart.shopkartauthenticationservice.models;

import com.shopkart.shopkartauthenticationservice.dtos.UserRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity(name = "users")
public class User extends BaseModel{
    @Column(unique = true)
    private String email;
    private String passwordSalt;
    @ManyToMany
    private List<Role> roles;

    public static UserRecord toUserRecord(User user){
        return new UserRecord(user.getUuid().toString(),user.getEmail(),Role.toListOfString(user.getRoles()));
    }
    public static User toUser(Map<String,Object> claims){
        User user=new User();
        user.setUuid(UUID.fromString((String) claims.get("uid")));
        user.setEmail((String) claims.get("email"));
        Object rolesObject = claims.get("roles");
        List<String> roles;
        if (rolesObject instanceof List<?>) {
            roles = ((List<?>) rolesObject).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        } else {
            roles = Collections.emptyList();
        }
        user.setRoles(Role.toListOfRole(roles));
        return user;
    }
    public static Map<String,Object> toClaims(User user){
        Map<String,Object> claims=new HashMap<>();
        claims.put("uid",user.getUuid());
        claims.put("email",user.getEmail());
        claims.put("roles",Role.toListOfString(user.getRoles()));
        return claims;
    }
}
