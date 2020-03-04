package com.example.authorizationserver.user.service;

import com.example.authorizationserver.user.dao.UserRepository;
import com.example.authorizationserver.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.IdGenerator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final IdGenerator idGenerator;

  public UserService(UserRepository userRepository, IdGenerator idGenerator) {
    this.userRepository = userRepository;
    this.idGenerator = idGenerator;
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Transactional
  public User save(User entity) {
    if (entity.getIdentifier() == null) {
      entity.setIdentifier(idGenerator.generateId());
    }
    return userRepository.save(entity);
  }

  public Optional<User> findOneByIdentifier(UUID identifier) {
    return userRepository.findOneByIdentifier(identifier);
  }

  public Optional<User> findOneByUsername(String username) {
    return userRepository.findOneByUsername(username);
  }
}
