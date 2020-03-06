package com.example.authorizationserver.token.store.dao;

import com.example.authorizationserver.token.store.model.OpaqueToken;

public interface OpaqueTokenRepository extends TokenRepository<OpaqueToken> {

  OpaqueToken findOneByValue(String value);
}
