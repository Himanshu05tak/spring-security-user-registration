package com.himanshuDev.spring_security_client.service;

import com.himanshuDev.spring_security_client.entity.User;
import com.himanshuDev.spring_security_client.entity.VerificationToken;
import com.himanshuDev.spring_security_client.model.UserModel;
import com.himanshuDev.spring_security_client.registration.VerificationStatus;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    VerificationStatus validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);
}
