package com.coursework.Server.Controllers;

import com.coursework.Server.Model.User;
import com.coursework.Server.Repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @ModelAttribute(name = "user")
    public User user() { return new User(); }
    private UserRepository userRepository;
    private PasswordEncoder encoder;
    public RegistrationController(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }
    @PostMapping
    public String processRegistration(@Valid User user, Errors errors, Model model) {
        if(errors.hasErrors())
            return "login";
        if(userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("exists", "Пользователь с таким логином уже зарегистрирован!");
            return "login";
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("user");
        user.setBlocked(false);
        user.setActivity(0);
        userRepository.save(user);
        return "redirect:/login";
    }
}
