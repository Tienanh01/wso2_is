package com.example.SSO_Intergration.service;

import com.example.SSO_Intergration.modal.User;
import com.example.SSO_Intergration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface UserService  {
    User findByUsername(String username);
    User saveUser(User user);
}
