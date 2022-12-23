package com.sivalabs.devzone.users.web.controllers;

import com.sivalabs.devzone.common.exceptions.DevZoneException;
import com.sivalabs.devzone.config.annotations.AnyAuthenticatedUser;
import com.sivalabs.devzone.config.annotations.CurrentUser;
import com.sivalabs.devzone.users.domain.models.ChangePasswordRequest;
import com.sivalabs.devzone.users.domain.models.User;
import com.sivalabs.devzone.users.domain.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AnyAuthenticatedUser
public class ChangePasswordController {
    private static final Logger log = LoggerFactory.getLogger(ChangePasswordController.class);
    private final UserService userService;

    public ChangePasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        model.addAttribute("changePassword", new ChangePasswordRequest());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute("changePassword") ChangePasswordRequest changePasswordRequest,
            BindingResult bindingResult,
            @CurrentUser User loginUser,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "change-password";
        }
        try {
            userService.changePassword(loginUser.getEmail(), changePasswordRequest);
            redirectAttributes.addFlashAttribute("msg", "Password changed successfully");
        } catch (DevZoneException e) {
            log.error("ChangePassword err", e);
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
        }
        return "redirect:/change-password";
    }
}
