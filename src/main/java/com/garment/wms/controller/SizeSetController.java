package com.garment.wms.controller;

import com.garment.wms.model.SizeSet;
import com.garment.wms.model.SizeSetRatio;
import com.garment.wms.repository.SizeSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sizesets")
@RequiredArgsConstructor
public class SizeSetController {

    private final SizeSetRepository sizeSetRepository;

    @GetMapping
    public String listSizeSets(Model model) {
        model.addAttribute("sizeSets", sizeSetRepository.findAll());
        model.addAttribute("activeNav", "sizesets");
        return "sizesets/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("activeNav", "sizesets");
        return "sizesets/form";
    }

    @PostMapping("/create")
    public String createSizeSet(
            @RequestParam("setName") String setName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("sizeName[]") List<String> sizeNames,
            @RequestParam("ratioCount[]") List<Integer> ratioCounts,
            RedirectAttributes redirectAttributes) {

        try {
            if (sizeSetRepository.existsBySetName(setName)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Size Set '" + setName + "' already exists!");
                return "redirect:/sizesets/create";
            }

            SizeSet sizeSet = SizeSet.builder()
                    .setName(setName.trim())
                    .description(description)
                    .createdAt(OffsetDateTime.now())
                    .ratios(new ArrayList<>())
                    .build();

            for (int i = 0; i < sizeNames.size(); i++) {
                String sName = sizeNames.get(i).trim();
                int count = (i < ratioCounts.size() && ratioCounts.get(i) != null) ? ratioCounts.get(i) : 1;
                if (!sName.isEmpty() && count > 0) {
                    SizeSetRatio ratio = SizeSetRatio.builder()
                            .sizeName(sName)
                            .ratioCount(count)
                            .sortOrder(i + 1)
                            .build();
                    sizeSet.addRatio(ratio);
                }
            }

            if (sizeSet.getRatios().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please provide at least one size ratio with count > 0.");
                return "redirect:/sizesets/create";
            }

            sizeSetRepository.save(sizeSet);
            redirectAttributes.addFlashAttribute("successMessage", "Size Set '" + setName + "' (" + sizeSet.getTotalRatioPieces() + " pcs ratio) created successfully!");
            return "redirect:/sizesets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create size set: " + e.getMessage());
            return "redirect:/sizesets/create";
        }
    }
}
