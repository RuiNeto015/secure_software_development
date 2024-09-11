package com.desofs.backend.domain.enums;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class Authority implements GrantedAuthority {

    public static final String BUSINESSADMIN = "BUSINESSADMIN";

    public static final String PROPERTYOWNER = "PROPERTYOWNER";

    public static final String CUSTOMER = "CUSTOMER";

    String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}