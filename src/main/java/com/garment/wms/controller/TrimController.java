package com.garment.wms.controller;

import com.garment.wms.model.TrimAccessory;
import com.garment.wms.repository.BrandRepository;
import com.garment.wms.repository.TrimAccessoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trims")
@RequiredArgsConstructor
public class TrimController {

    private final TrimAccessoryRepository trimRepository;
    private final BrandRepository brandRepository;

    @GetMapping("/store")
    public String listTrims(Model model) {
        model.addAttribute("trims", trimRepository.findAll());
        model.addAttribute("brands", brandRepository.findByActiveTrue());
        model.addAttribute("newTrim", new TrimAccessory());
        model.addAttribute("activeNav", "trims-store");
        return "trims/store";
    }

    @PostMapping("/create")
    public String createTrim(@ModelAttribute TrimAccessory trim,
                             @RequestParam(required = false) Long brandId,
                             RedirectAttributes redirectAttributes) {
        try {
            if (trimRepository.existsByItemCode(trim.getItemCode())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Trim item code already exists!");
                return "redirect:/trims/store";
            }
            if (brandId != null) {
                trim.setBrand(brandRepository.findById(brandId).orElse(null));
            }
            trim.setItemCode(trim.getItemCode().trim().toUpperCase());
            trimRepository.save(trim);
            redirectAttributes.addFlashAttribute("successMessage", "Trim / Accessory registered in Central Store!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error registering trim: " + e.getMessage());
        }
        return "redirect:/trims/store";
    }

    @PostMapping("/{id}/add-stock")
    public String addStock(@PathVariable Long id, @RequestParam double quantity, RedirectAttributes redirectAttributes) {
        try {
            TrimAccessory trim = trimRepository.findById(id).orElseThrow();
            trim.setStockQuantity(trim.getStockQuantity() + quantity);
            trimRepository.save(trim);
            redirectAttributes.addFlashAttribute("successMessage", "Added " + quantity + " " + trim.getUom() + " to stock for " + trim.getItemName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding stock: " + e.getMessage());
        }
        return "redirect:/trims/store";
    }
}
