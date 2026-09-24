package com.garment.wms.controller;

import com.garment.wms.model.Brand;
import com.garment.wms.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandRepository brandRepository;

    @GetMapping
    public String listBrands(Model model) {
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("newBrand", new Brand());
        model.addAttribute("activeNav", "brands");
        return "brands/list";
    }

    @PostMapping("/create")
    public String createBrand(@ModelAttribute Brand brand, RedirectAttributes redirectAttributes) {
        try {
            if (brandRepository.existsByBrandCode(brand.getBrandCode())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Brand code already exists!");
                return "redirect:/brands";
            }
            brand.setBrandCode(brand.getBrandCode().trim().toUpperCase());
            brandRepository.save(brand);
            redirectAttributes.addFlashAttribute("successMessage", "Brand '" + brand.getBrandName() + "' created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating brand: " + e.getMessage());
        }
        return "redirect:/brands";
    }
}
