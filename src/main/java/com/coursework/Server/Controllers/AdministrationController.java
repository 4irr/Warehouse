package com.coursework.Server.Controllers;

import com.coursework.Server.Model.Operation;
import com.coursework.Server.Model.Order;
import com.coursework.Server.Model.Report;
import com.coursework.Server.Model.User;
import com.coursework.Server.Repositories.OperationRepository;
import com.coursework.Server.Repositories.OrderRepository;
import com.coursework.Server.Repositories.ReportRepository;
import com.coursework.Server.Repositories.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/administration")
public class AdministrationController {
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping
    public String getAdministration(@AuthenticationPrincipal User user) {
        if(!user.getRole().equals("admin"))
            return "denied";
        return "administration";
    }
    @GetMapping("/users")
    public String getUsers(@AuthenticationPrincipal User user, Model model) {
        User profile = userRepository.findById(user.getUserID()).orElseThrow();
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        users.remove(profile);
        model.addAttribute("users", users);
        return "users";
    }
    @GetMapping("/users/{id}/edit")
    public String editUserGet(@PathVariable(name = "id") Long id, Model model) {
        if(!userRepository.existsById(id))
            return "redirect:/administration/users";
        User editUser = userRepository.findById(id).orElseThrow();
        model.addAttribute("res", editUser);
        return "user-edit";
    }
    @PostMapping("/users/{id}/edit")
    public String editUserPost(@PathVariable(name = "id") Long id, @Valid User user, Errors errors, Model model) {
        if(errors.hasErrors()){
            User editUser = userRepository.findById(id).orElseThrow();
            model.addAttribute("error", "Данные введены неверно!");
            model.addAttribute("res", editUser);
            return "user-edit";
        }
        User userToEdit = userRepository.findById(id).orElseThrow();
        userToEdit.setName(user.getName());
        userToEdit.setSurname(user.getSurname());
        userToEdit.setEmail(user.getEmail());
        userToEdit.setTelNum(user.getTelNum());
        userRepository.save(userToEdit);
        return "redirect:/administration/users";
    }
    @PostMapping("/users/{id}/remove")
    public String removeUser(@PathVariable(name = "id") Long id, @AuthenticationPrincipal User user, Model model) {
        User profile = userRepository.findById(user.getUserID()).orElseThrow();
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        users.remove(profile);
        model.addAttribute("users", users);
        model.addAttribute("exists", "Перед удалением пользователя необходимо удалить данные о связанных операциях, ордерах и отчётах!");
        List<Operation> operations = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        List<Report> reports = new ArrayList<>();
        operationRepository.findAll().forEach(operations::add);
        orderRepository.findAll().forEach(orders::add);
        reportRepository.findAll().forEach(reports::add);
        for(var el : operations)
            if(el.getUser().getUserID().equals(id))
                return "users";
        for(var el : orders)
            if(el.getUser().getUserID().equals(id))
                return "users";
        for(var el : reports)
            if(el.getUser().getUserID().equals(id))
                return "users";
        User userToDelete = userRepository.findById(id).orElseThrow();
        userRepository.delete(userToDelete);
        return "redirect:/administration/users";
    }
    @GetMapping("/users/{id}/block")
    public String blockUser(@PathVariable(name = "id") Long id, @AuthenticationPrincipal User user, Model model) {
        User userToBlock = userRepository.findById(id).orElseThrow();
        if(!userToBlock.isBlocked())
            userToBlock.setBlocked(true);
        userRepository.save(userToBlock);
        User profile = userRepository.findById(user.getUserID()).orElseThrow();
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        users.remove(profile);
        model.addAttribute("users", users);
        return "users";
    }
    @GetMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable(name = "id") Long id) {
        User userToUnblock = userRepository.findById(id).orElseThrow();
        if(userToUnblock.isBlocked())
            userToUnblock.setBlocked(false);
        userRepository.save(userToUnblock);
        return "redirect:/administration/users";
    }
    @GetMapping("/users/{id}/upgrade")
    public String upgradeUser(@PathVariable(name = "id") Long id) {
        User userToUpgrade = userRepository.findById(id).orElseThrow();
        if(userToUpgrade.getRole().equals("user"))
            userToUpgrade.setRole("admin");
        userRepository.save(userToUpgrade);
        return "redirect:/administration/users";
    }
    @GetMapping("/users/{id}/downgrade")
    public String downgradeUser(@PathVariable(name = "id") Long id) {
        User userToDowngrade = userRepository.findById(id).orElseThrow();
        if(userToDowngrade.getRole().equals("admin"))
            userToDowngrade.setRole("user");
        userRepository.save(userToDowngrade);
        return "redirect:/administration/users";
    }
    @GetMapping("/activity")
    public String getActivity(Model model) {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        model.addAttribute("users", users);
        return "activity";
    }
}
