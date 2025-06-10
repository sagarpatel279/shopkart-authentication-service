package com.shopkart.shopkartauthenticationservice.models;

import jakarta.persistence.*;
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
    @Column(length = 2000)
    private String token;
    @Enumerated(EnumType.ORDINAL)
    private SessionState sessionState;
}
