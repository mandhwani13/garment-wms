package com.garment.wms.controller;

import com.garment.wms.model.Role;
import com.garment.wms.model.UserAccount;
import com.garment.wms.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.OffsetDateTime;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasAuthority('ROLE_MASTER')")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", Role.values());
        model.addAttribute("activeNav", "admin-users");
        return "admin/users";
    }

    @PostMapping("/create")
    public String createUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("fullName") String fullName,
            @RequestParam("role") Role role,
            RedirectAttributes redirectAttributes) {

        try {
            if (userRepository.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username '" + username + "' is already taken.");
                return "redirect:/admin/users";
            }

            UserAccount user = UserAccount.builder()
                    .username(username.trim().toLowerCase())
                    .password(passwordEncoder.encode(password))
                    .fullName(fullName.trim())
                    .role(role)
                    .enabled(true)
                    .createdAt(OffsetDateTime.now())
                    .build();

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User '" + username + "' created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/toggle")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userRepository.findById(id).ifPresent(u -> {
            u.setEnabled(!u.isEnabled());
            userRepository.save(u);
            redirectAttributes.addFlashAttribute("successMessage", "Status updated for user: " + u.getUsername());
        });
        return "redirect:/admin/users";
    }
}
