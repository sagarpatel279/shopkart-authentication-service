package com.shopkart.shopkartauthenticationservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity(name = "sessions")
public class Session extends BaseModel{
    @ManyToOne
    private User user;
    private String token;
    @Enumerated(EnumType.ORDINAL)
    private SessionState sessionState;
}
