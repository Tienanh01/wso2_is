package com.example.SSO_Intergration.cache;

import com.example.SSO_Intergration.modal.User;
import com.example.SSO_Intergration.repository.UserRepository;
import com.example.SSO_Intergration.until.MapperUtil;
import com.example.SSO_Intergration.until.MemcachedKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCacheService {
    @Autowired
    private UserRepository userRepository;

    public UserCacheEntry findByUsername(String username) {
        UserCacheEntry userCacheEntry;
        String cachedKey = MemcachedKey.getKey(username, MemcachedKey.DOCUMENT_KEY);
        MemcachedUtil.getInstance().delete(cachedKey);
        userCacheEntry = (UserCacheEntry) MemcachedUtil.getInstance().read(cachedKey);
        if (userCacheEntry == null) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                userCacheEntry = MapperUtil.modelToUserCache(user);
                MemcachedUtil.getInstance().create(cachedKey, MemcachedKey.SEND_DOCUMENT_TIME_LIFE, userCacheEntry);
            }
        }
        return userCacheEntry;
    }

}
