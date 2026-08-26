package com.himanshuDev.Oauth_authorization_server.repository;

import com.himanshuDev.Oauth_authorization_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
