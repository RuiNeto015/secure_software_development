package com.desofs.backend.dtos;

import java.util.Date;

public record ForgotPasswordMetaDto(String email, Date expiryDate) {
}
