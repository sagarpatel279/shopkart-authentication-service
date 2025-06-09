package com.shopkart.shopkartauthenticationservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity(name = "sessions")
public class Session extends BaseModel{
    private String token;
    @ManyToOne
    private User user;
    @Enumerated(EnumType.ORDINAL)
    private SessionState sessionState;
}
