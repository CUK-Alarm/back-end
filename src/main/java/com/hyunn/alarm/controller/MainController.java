package com.hyunn.alarm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

  @GetMapping("/setting")
  public String setting() {
    return "setting";
  }

  @GetMapping("/modify")
  public String modify() {
    return "modify";
  }

  @GetMapping("/withdraw")
  public String withdraw() {
    return "withdraw";
  }

}
