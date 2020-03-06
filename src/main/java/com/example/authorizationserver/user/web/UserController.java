package com.example.authorizationserver.user.web;

import com.example.authorizationserver.user.api.resource.UserResource;
import com.example.authorizationserver.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @ModelAttribute("allUsers")
  public List<UserResource> populateUsers() {
    return this.userService.findAll().stream().map(UserResource::new).collect(Collectors.toList());
  }

  @GetMapping("/admin/userlist")
  public String findAll() {
    return "userlist";
  }
}
