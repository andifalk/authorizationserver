package com.example.authorizationserver.admin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminWebController {

  @GetMapping
  public String admin() {
    return "index";
  }

}
