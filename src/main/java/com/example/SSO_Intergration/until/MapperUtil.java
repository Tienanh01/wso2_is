package com.example.SSO_Intergration.until;

import com.example.SSO_Intergration.cache.UserCacheEntry;
import com.example.SSO_Intergration.modal.User;

public class MapperUtil {

    public static UserCacheEntry modelToUserCache(User user) {
        UserCacheEntry userCacheEntry = new UserCacheEntry();
        try {
            userCacheEntry.setEmailAddress(user.getEmailAddress());
            userCacheEntry.setLastLoginDate(user.getLastLoginDate());
            userCacheEntry.setLastLoginIP(user.getLastLoginIP());
            userCacheEntry.setStatus(user.getStatus() == 0 ? true :  false );
            userCacheEntry.setUserId(user.getUserId());
            userCacheEntry.setUsername(user.getUsername());
            userCacheEntry.setDisplayName(user.getDisplayName());


            userCacheEntry.setOnSso(user.getSso());
        } catch (Exception e) {

        }

        return userCacheEntry;
    }
}
