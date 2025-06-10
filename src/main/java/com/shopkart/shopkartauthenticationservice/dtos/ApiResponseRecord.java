package com.shopkart.shopkartauthenticationservice.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public record ApiResponseRecord<T>
        (T data,
         String message,
         int status) {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss z")
    private static final ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.systemDefault());
}
