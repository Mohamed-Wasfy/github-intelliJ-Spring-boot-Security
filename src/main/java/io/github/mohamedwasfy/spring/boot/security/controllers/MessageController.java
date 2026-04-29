package io.github.mohamedwasfy.spring.boot.security.controllers;

import io.github.mohamedwasfy.spring.boot.security.entities.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @RequestMapping("/hello")
    public Message sayHello() {
        return new Message("Hello World!");
    }
}