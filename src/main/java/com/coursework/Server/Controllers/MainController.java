package com.coursework.Server.Controllers;

import com.coursework.Server.Model.User;
import com.coursework.Server.Repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {
    @ModelAttribute(name = "map")
    public Map<String, String> map(){
        Map<String, String> map = new HashMap<>();
        map.put("главная", "home");
        map.put("операции", "redirect:/services/operations");
        map.put("товары", "redirect:/services/products");
        map.put("ордера", "redirect:/services/orders");
        map.put("партии", "redirect:/services/batches");
        map.put("пик-листы", "redirect:/services/pick-list");
        map.put("отчёты", "redirect:/services/reports");
        map.put("аналитика", "redirect:/analytics");
        map.put("контакты", "contacts");
        map.put("профиль", "redirect:/profile");
        return map;
    }
    @ModelAttribute(name = "user")
    public User user() { return new User(); }
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @GetMapping("/")
    public String home(Model model){
        return "home";
    }
    @GetMapping("/home")
    public String getHome(Model model){
        return "home";
    }
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
    @GetMapping("/contacts")
    public String getContacts() { return "contacts"; }
    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal User user, Model model) {
        user = userRepository.findByUsername(user.getUsername());
        model.addAttribute("profile", user);
        return "profile";
    }
    @GetMapping("/profile/edit")
    public String editProfileGet(@AuthenticationPrincipal User user, Model model) {
        user = userRepository.findByUsername(user.getUsername());
        model.addAttribute("profile", user);
        return "profile-edit";
    }
    @PostMapping("/profile/edit")
    public String editProfilePost(@Valid User editUser, Errors errors, @AuthenticationPrincipal User user, Model model) {
        if(errors.hasErrors()){
            model.addAttribute("error", "Данные введены неверно!");
            model.addAttribute("profile", editUser);
            return "profile-edit";
        }
        User userToEdit = userRepository.findByUsername(user.getUsername());
        userToEdit.setName(editUser.getName());
        userToEdit.setSurname(editUser.getSurname());
        userToEdit.setEmail(editUser.getEmail());
        userToEdit.setTelNum(editUser.getTelNum());
        userRepository.save(userToEdit);
        model.addAttribute("profile", userToEdit);
        return "profile";
    }
    @GetMapping("/profile/change-password")
    public String changePasswordGet() { return "change-password"; }
    @PostMapping("/profile/change-password")
    public String changePasswordPost(@RequestParam(name = "pass") String pass, @RequestParam(name = "repPass") String repPass, @AuthenticationPrincipal User user, Model model) {
        if(!pass.equals(repPass)) {
            model.addAttribute("error", "Пароли не совпадают!");
            return "change-password";
        }
        User editUser = userRepository.findById(user.getUserID()).orElseThrow();
        editUser.setPassword(encoder.encode(pass));
        userRepository.save(editUser);
        model.addAttribute("profile", editUser);
        model.addAttribute("changed", "Пароль был изменён успешно!");
        return "profile";
    }
    @GetMapping("/search")
    public String processSearch(@RequestParam(name = "value") String value, @ModelAttribute(name = "map") HashMap<String, String> map, Model model) {
        value = value.trim().toLowerCase();
        if(map.get(value) == null)
            return "notFound";
        return map.get(value);
    }
}