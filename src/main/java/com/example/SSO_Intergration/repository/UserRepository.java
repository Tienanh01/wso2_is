package com.example.SSO_Intergration.repository;

import com.example.SSO_Intergration.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
