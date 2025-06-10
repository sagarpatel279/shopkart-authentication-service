package com.shopkart.shopkartauthenticationservice.utilities;

import java.security.SecureRandom;
import java.util.UUID;

public class StringUtility {
    public static String random(int count){
        final char[] PASSWORD_CHARACTERS = new char[] {
                // Uppercase A–Z
                'A','B','C','D','E','F','G','H','I','J','K','L','M',
                'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',

                // Lowercase a–z
                'a','b','c','d','e','f','g','h','i','j','k','l','m',
                'n','o','p','q','r','s','t','u','v','w','x','y','z',

                // Digits 0–9
                '0','1','2','3','4','5','6','7','8','9',

                // Common special characters
                '!', '@', '#', '$', '%', '^', '&', '*', '(', ')',
                '-', '_', '=', '+', '[', ']', '{', '}', '\\', '|',
                ';', ':', '\'', '"', ',', '.', '<', '>', '/', '?', '`', '~'
        };

        final StringBuilder builder=new StringBuilder();
        final SecureRandom random=new SecureRandom();
        int n=PASSWORD_CHARACTERS.length;
        while(count-->0){
            int indx=random.nextInt(0,n);
            builder.append(PASSWORD_CHARACTERS[indx]);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
//            System.out.println(random(40));
        System.out.println(UUID.randomUUID());
    }
}
