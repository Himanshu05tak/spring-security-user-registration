package com.himanshuDev.spring_security_client.controller;

import com.himanshuDev.spring_security_client.entity.User;
import com.himanshuDev.spring_security_client.entity.VerificationToken;
import com.himanshuDev.spring_security_client.event.RegistrationCompleteEvent;
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

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping(RegistrationConstants.REGISTER_URL)
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {

        User user = userService.registerUser(userModel);
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(request)));
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

    @GetMapping(RegistrationConstants.RESEND_VERIFY_TOKEN)
    public String resendVerificationCode(@RequestParam("token") String oldToken, HttpServletRequest request) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);

        User user = verificationToken.getUser();
        resendVerificationTokenMail(user,applicationUrl(request),verificationToken);
        return RegistrationMessages.VERIFICATION_LINK_SEND;
    }

    private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {

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

