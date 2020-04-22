package com.example.authorizationserver.token.store.dao;

import com.example.authorizationserver.token.store.model.JsonWebToken;

public interface JsonWebTokenRepository extends TokenRepository<JsonWebToken> {

  JsonWebToken findOneByValue(String value);

  JsonWebToken findOneByValueAndAccessToken(String value, boolean accessToken);
}
