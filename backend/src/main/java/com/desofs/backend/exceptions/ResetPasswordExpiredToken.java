package com.desofs.backend.exceptions;

public class ResetPasswordExpiredToken extends Exception{
    public ResetPasswordExpiredToken() {
        super("The token to reset the password expired");
    }
}
