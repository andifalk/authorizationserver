package com.example.authorizationserver.scim.service;

import java.util.UUID;

public class ScimUserNotFoundException extends RuntimeException {

    public ScimUserNotFoundException(UUID userIdentifier) {
        super(String.format("No user found with identifier %s", userIdentifier));
    }
}
