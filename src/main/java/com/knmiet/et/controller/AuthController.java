package com.knmiet.et.controller;
import com.knmiet.et.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Show login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Show signup page
    @GetMapping("/signup")
    public String signup()
    {
        System.out.println("Get Sign Up Page");
        return "signup";
    }

    // Handle signup
    @PostMapping("/signup")
    public String register(@RequestParam String username,
                           @RequestParam String password) {

        System.out.println(username);
        System.out.println(password);

        userService.saveUser(username, password);
        return "redirect:/login";
    }

    // Home page
    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
