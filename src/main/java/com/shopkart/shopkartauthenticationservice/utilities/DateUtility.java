package com.shopkart.shopkartauthenticationservice.utilities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtility {
    public static Date setExpirationAfter(Date date, int days){
        LocalDateTime now=date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updated=now.plusDays(days);
        return Date.from(updated.atZone(ZoneId.systemDefault()).toInstant());
    }
    public static Date getCurrentDate(){
        return new Date(System.currentTimeMillis());
    }
}
