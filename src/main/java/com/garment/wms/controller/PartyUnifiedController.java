package com.garment.wms.controller;

import com.garment.wms.model.Location;
import com.garment.wms.model.Party;
import com.garment.wms.model.PartyType;
import com.garment.wms.repository.LocationRepository;
import com.garment.wms.repository.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parties/unified")
@RequiredArgsConstructor
public class PartyUnifiedController {

    private final PartyRepository partyRepository;
    private final LocationRepository locationRepository;

    @GetMapping
    public String listAllParties(@RequestParam(required = false) PartyType type, Model model) {
        if (type != null) {
            model.addAttribute("parties", partyRepository.findByPartyTypeAndActiveTrue(type));
            model.addAttribute("selectedType", type);
        } else {
            model.addAttribute("parties", partyRepository.findAll());
            model.addAttribute("selectedType", null);
        }
        model.addAttribute("partyTypes", PartyType.values());
        model.addAttribute("locations", locationRepository.findByActiveTrue());
        model.addAttribute("activeNav", "parties-unified");
        return "parties/unified";
    }

    @PostMapping("/create")
    public String createParty(@ModelAttribute Party party,
                              @RequestParam(required = false) Long linkedLocationId,
                              RedirectAttributes redirectAttributes) {
        try {
            if (partyRepository.existsByPartyCode(party.getPartyCode())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Party code already exists!");
                return "redirect:/parties/unified";
            }
            if (linkedLocationId != null) {
                Location loc = locationRepository.findById(linkedLocationId).orElse(null);
                party.setLinkedLocation(loc);
            }
            party.setPartyCode(party.getPartyCode().trim().toUpperCase());
            partyRepository.save(party);
            redirectAttributes.addFlashAttribute("successMessage", "Partner '" + party.getPartyName() + "' registered successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating partner: " + e.getMessage());
        }
        return "redirect:/parties/unified";
    }
}
