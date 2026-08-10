package com.himanshuDev.spring_security_client.service;

import com.himanshuDev.spring_security_client.entity.User;
import com.himanshuDev.spring_security_client.entity.VerificationToken;
import com.himanshuDev.spring_security_client.model.UserModel;
import com.himanshuDev.spring_security_client.registration.VerificationStatus;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    VerificationStatus validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    VerificationStatus validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);
}
