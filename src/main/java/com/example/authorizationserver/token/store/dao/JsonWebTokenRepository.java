package com.example.authorizationserver.token.store.dao;

import com.example.authorizationserver.token.store.model.JsonWebToken;
import com.example.authorizationserver.token.store.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JsonWebTokenRepository extends TokenRepository<JsonWebToken> {

  JsonWebToken findOneByValue(String value);
}
