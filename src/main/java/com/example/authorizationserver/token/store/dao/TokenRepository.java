package com.example.authorizationserver.token.store.dao;

import com.example.authorizationserver.token.store.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository<T extends Token> extends JpaRepository<T, Long> {}
