package com.example.SSO_Intergration.cache;

import lombok.Data;

import java.util.Date;

@Data
public class UserCacheEntry {
    private long userId;
    private String emailAddress;
    private String username;

    private boolean status;
    private String displayName;
    private String lastLoginIP;
    private Date lastLoginDate;
    private boolean isOnSso;
}
