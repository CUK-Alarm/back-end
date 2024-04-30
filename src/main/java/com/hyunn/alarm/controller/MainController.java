package com.hyunn.alarm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

  @GetMapping("/main")
  public String main() {
    return "main";
  }

  @GetMapping("/setting")
  public String setting() {
    return "setting";
  }

  @GetMapping("/withdraw")
  public String withdraw() {
    return "withdraw";
  }

}
