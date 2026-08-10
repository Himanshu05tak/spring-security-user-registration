package com.himanshuDev.spring_security_client.controller;

import com.himanshuDev.spring_security_client.entity.User;
import com.himanshuDev.spring_security_client.entity.VerificationToken;
import com.himanshuDev.spring_security_client.event.RegistrationCompleteEvent;
import com.himanshuDev.spring_security_client.model.PasswordModel;
import com.himanshuDev.spring_security_client.model.UserModel;
import com.himanshuDev.spring_security_client.registration.RegistrationConstants;
import com.himanshuDev.spring_security_client.registration.RegistrationMessages;
import com.himanshuDev.spring_security_client.registration.VerificationStatus;
import com.himanshuDev.spring_security_client.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping(RegistrationConstants.REGISTER_URL)
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {

        User user = userService.registerUser(userModel);
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return RegistrationMessages.SUCCESS;
    }

    @GetMapping(RegistrationConstants.VERIFY_REGISTRATION)
    public String verifyRegistration(@RequestParam("token") String token) {

        VerificationStatus result = userService.validateVerificationToken(token);

        return switch (result) {
            case VALID -> RegistrationMessages.ACCOUNT_VERIFIED;
            case INVALID -> RegistrationMessages.TOKEN_INVALID;
            case ALREADY_VERIFIED -> RegistrationMessages.ACCOUNT_ALREADY_VERIFIED;
            case EXPIRED -> RegistrationMessages.TOKEN_HAS_EXPIRED;
        };
    }

    @PostMapping(RegistrationConstants.RESEND_VERIFY_TOKEN)
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);

        User user = verificationToken.getUser();
        resendVerificationTokenMail(applicationUrl(request), verificationToken);
        return RegistrationMessages.VERIFICATION_LINK_SEND;
    }

    @PostMapping(RegistrationConstants.RESET_PASSWORD_LINK)
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {

        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";

        if (user != null) {

            String token = UUID.randomUUID().toString();

            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(applicationUrl(request),token);
        }
        return url;
    }

    @PostMapping(RegistrationConstants.SAVE_PASSWORD)
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {

        VerificationStatus result = userService.validatePasswordResetToken(token);

        if(!result.equals(VerificationStatus.VALID)){
            return RegistrationMessages.TOKEN_INVALID;
        }
        Optional<User> optionalUser = userService.getUserByPasswordResetToken(token);

        if(optionalUser.isPresent()){
            userService.changePassword(optionalUser.get(), passwordModel.getNewPassword());
            return RegistrationMessages.PASSWORD_RESET_SUCCESS;
        }
        else {
            return RegistrationMessages.TOKEN_HAS_EXPIRED;
        }

    }

    private String passwordResetTokenMail(String applicationUrl, String token) {

        String url = applicationUrl + "/savePassword?token=" + token;

        //Send VerificationEmail()
        log.info("Click the link to Reset your password: {} ", url);

        return url;
    }

    private void resendVerificationTokenMail(String applicationUrl, VerificationToken verificationToken) {

        String url = applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();

        //Send VerificationEmail()
        log.info("Click the link to verify your account: {} ", url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
    }
}

