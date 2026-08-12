package com.himanshuDev.spring_security_client.registration;

public final class RegistrationConstants {

    private RegistrationConstants() {
        // Prevent instantiation
    }

    public static final String VERIFY_REGISTRATION = "/verifyRegistration";
    public static final String REGISTER_URL = "/register";
    public static final String RESEND_VERIFY_TOKEN = "/resendVerificationToken";
    public static final String RESET_PASSWORD_LINK = "/resetPassword";
    public static final String SAVE_PASSWORD = "/savePassword";
    public static final String CHANGE_PASSWORD = "/changePassword";

}