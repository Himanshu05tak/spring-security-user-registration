package com.himanshuDev.spring_security_client.service;

import com.himanshuDev.spring_security_client.entity.PasswordResetToken;
import com.himanshuDev.spring_security_client.entity.User;
import com.himanshuDev.spring_security_client.entity.VerificationToken;
import com.himanshuDev.spring_security_client.model.UserModel;
import com.himanshuDev.spring_security_client.registration.VerificationStatus;
import com.himanshuDev.spring_security_client.repository.PasswordResetTokenRepository;
import com.himanshuDev.spring_security_client.repository.UserRepository;
import com.himanshuDev.spring_security_client.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));

        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(user,token);

        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationStatus validateVerificationToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if(verificationToken == null){
            return VerificationStatus.INVALID;
        }
        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if(verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
            verificationTokenRepository.delete(verificationToken);
            return VerificationStatus.EXPIRED;
        }

        user.setEnabled(true);
        userRepository.save(user);

        return VerificationStatus.VALID;
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {

        PasswordResetToken  passwordResetToken = new PasswordResetToken(user,token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public VerificationStatus validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if(passwordResetToken == null){
            return VerificationStatus.INVALID;
        }
        //User user = passwordResetToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if(passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
            passwordResetTokenRepository.delete(passwordResetToken);
            return VerificationStatus.EXPIRED;
        }

        return VerificationStatus.VALID;
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User userExist, String oldPassword) {
        return passwordEncoder.matches(oldPassword, userExist.getPassword());
    }
}
