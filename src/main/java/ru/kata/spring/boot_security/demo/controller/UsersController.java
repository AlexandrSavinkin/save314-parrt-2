package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.reposotiries.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.Collection;


@Controller
@RequestMapping(value = "/")
public class UsersController {
    private final UserService userService;
    @Autowired
    public RoleRepository roleRepository;


    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/login")
    public String login() {
        return "/login";
    }


    @GetMapping("/user")
    public String pageForAuthenticatedUser(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute(user);
        if (user.getRoles().contains(roleRepository.getById(1))) {
            return "show";
        } else {
            return "user";
        }
    }

    @GetMapping("/admin")
    public String getAllUsers(Model model, Principal principal, @ModelAttribute("updatedUser") User updatedUser) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("principalUser", user);
        model.addAttribute("users", userService.getAllUsers());
        Collection<Role> roles = roleRepository.findAll();
        model.addAttribute("allRoles", roles);
        return "all_users";
    }

    @GetMapping("/admin/new")
    public String newPerson(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("principalUser", user);
        model.addAttribute("user", new User());
        Collection<Role> roles = roleRepository.findAll();
        model.addAttribute("allRoles", roles);
        return "new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("admin/{id}/")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("user", userService.show(id));
        Collection<Role> roles = roleRepository.findAll();
        model.addAttribute("allRoles", roles);
        return "redirect:/admin";
    }

    @PatchMapping("admin/{id}")
    public String update(@ModelAttribute("user") User user, @PathVariable("id") int id) {
        userService.update(id, user);
        return "redirect:/admin";
    }


    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        userService.delete(id);
        return "redirect:/admin";
    }




}