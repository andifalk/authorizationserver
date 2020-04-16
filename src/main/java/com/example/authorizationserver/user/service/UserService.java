package com.example.authorizationserver.user.service;

import com.example.authorizationserver.user.dao.UserRepository;
import com.example.authorizationserver.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.IdGenerator;

import java.time.LocalDateTime;
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
  public User create(User entity) {
    if (entity.getIdentifier() == null) {
      entity.setIdentifier(idGenerator.generateId());
    }
    entity.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(entity);
  }

  @Transactional
  public Optional<User> update(UUID userId, User userForUpdate) {
    return findOneByIdentifier(userId).map(u -> {
      u.setAddress(userForUpdate.getAddress());
      u.setEmail(userForUpdate.getEmail());
      u.setFirstName(userForUpdate.getFirstName());
      u.setLastName(userForUpdate.getLastName());
      u.setGender(userForUpdate.getGender());
      u.setGroups(userForUpdate.getGroups());
      u.setPassword(userForUpdate.getPassword());
      u.setPhone(userForUpdate.getPhone());
      u.setUsername(userForUpdate.getUsername());
      u.setUpdatedAt(LocalDateTime.now());
      return Optional.of(userRepository.save(u));
    }).orElse(Optional.empty());

  }

  public Optional<User> findOneByIdentifier(UUID identifier) {
    return userRepository.findOneByIdentifier(identifier);
  }

  public Optional<User> findOneByUsername(String username) {
    return userRepository.findOneByUsername(username);
  }

  @Transactional
  public void deleteOneByIdentifier(UUID identifier) {
    userRepository.deleteOneByIdentifier(identifier);
  }
}
