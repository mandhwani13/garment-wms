package com.garment.wms.controller;

import com.garment.wms.model.FinishingProgram;
import com.garment.wms.model.PartyType;
import com.garment.wms.repository.BrandRepository;
import com.garment.wms.repository.CuttingLotRepository;
import com.garment.wms.repository.PartyRepository;
import com.garment.wms.repository.TrimAccessoryRepository;
import com.garment.wms.service.FinishingProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/finishing/programs")
@RequiredArgsConstructor
public class FinishingProgramController {

    private final FinishingProgramService finishingProgramService;
    private final CuttingLotRepository cuttingLotRepository;
    private final BrandRepository brandRepository;
    private final PartyRepository partyRepository;
    private final TrimAccessoryRepository trimRepository;

    @GetMapping
    public String listPrograms(Model model) {
        model.addAttribute("programs", finishingProgramService.getAllPrograms());
        model.addAttribute("lots", cuttingLotRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("brands", brandRepository.findByActiveTrue());
        model.addAttribute("finishingUnits", partyRepository.findByPartyTypeAndActiveTrue(PartyType.FINISHING_UNIT));
        model.addAttribute("activeNav", "finishing-programs");
        return "finishing/programs";
    }

    @PostMapping("/create")
    public String createProgram(@RequestParam Long cuttingLotId,
                                @RequestParam Long brandId,
                                @RequestParam Long finishingUnitId,
                                @RequestParam int piecesAllocated,
                                RedirectAttributes redirectAttributes) {
        try {
            FinishingProgram prog = finishingProgramService.createProgramWithBom(cuttingLotId, brandId, finishingUnitId, piecesAllocated);
            redirectAttributes.addFlashAttribute("successMessage", "Finishing Program " + prog.getProgramNo() + " created with automated BOM calculation!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating finishing program: " + e.getMessage());
        }
        return "redirect:/finishing/programs";
    }

    @PostMapping("/{id}/issue-trims")
    public String issueTrims(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            FinishingProgram prog = finishingProgramService.issueTrims(id);
            redirectAttributes.addFlashAttribute("successMessage", "BOM Accessories issued from Central Warehouse for Program " + prog.getProgramNo());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error issuing trims: " + e.getMessage());
        }
        return "redirect:/finishing/programs";
    }

    @PostMapping("/{id}/complete")
    public String completeFinishing(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            FinishingProgram prog = finishingProgramService.completeFinishing(id);
            redirectAttributes.addFlashAttribute("successMessage", "Program " + prog.getProgramNo() + " marked as PACKED & READY for Warehouse Inward!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error completing finishing: " + e.getMessage());
        }
        return "redirect:/finishing/programs";
    }
}
