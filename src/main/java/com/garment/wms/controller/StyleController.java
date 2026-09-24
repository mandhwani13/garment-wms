package com.garment.wms.controller;

import com.garment.wms.model.Style;
import com.garment.wms.repository.StyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/styles")
@RequiredArgsConstructor
public class StyleController {

    private final StyleRepository styleRepository;

    @GetMapping
    public String listStyles(Model model) {
        model.addAttribute("styles", styleRepository.findAll());
        model.addAttribute("newStyle", new Style());
        model.addAttribute("activeNav", "styles");
        return "styles/list";
    }

    @PostMapping("/create")
    public String createStyle(@ModelAttribute Style style, RedirectAttributes redirectAttributes) {
        try {
            if (styleRepository.existsByStyleCode(style.getStyleCode())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Style Code '" + style.getStyleCode() + "' already exists!");
                return "redirect:/styles";
            }
            style.setStyleCode(style.getStyleCode().trim().toUpperCase());
            styleRepository.save(style);
            redirectAttributes.addFlashAttribute("successMessage", "Style '" + style.getStyleCode() + "' created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create style: " + e.getMessage());
        }
        return "redirect:/styles";
    }
}
