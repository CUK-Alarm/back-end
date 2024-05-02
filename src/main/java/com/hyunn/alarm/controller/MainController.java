package com.hyunn.alarm.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  @GetMapping("/setting")
  public String setting(Model model) {
    model.addAttribute("apiKey", xApiKey);
    return "setting";
  }

  @GetMapping("/modify")
  public String modify(Model model) {
    model.addAttribute("apiKey", xApiKey);
    return "modify";
  }

  @GetMapping("/withdraw")
  public String withdraw(Model model) {
    model.addAttribute("apiKey", xApiKey);
    return "withdraw";
  }

}
