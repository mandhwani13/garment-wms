package com.garment.wms.controller;

import com.garment.wms.model.PartyType;
import com.garment.wms.model.WashingBatch;
import com.garment.wms.repository.CuttingLotRepository;
import com.garment.wms.repository.PartyRepository;
import com.garment.wms.service.WashingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/washing/batches")
@RequiredArgsConstructor
public class WashingController {

    private final WashingService washingService;
    private final CuttingLotRepository cuttingLotRepository;
    private final PartyRepository partyRepository;

    @GetMapping
    public String listBatches(Model model) {
        model.addAttribute("batches", washingService.getAllBatches());
        model.addAttribute("lots", cuttingLotRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("washingUnits", partyRepository.findByPartyTypeAndActiveTrue(PartyType.WASHING_UNIT));
        model.addAttribute("activeNav", "washing-batches");
        return "washing/batches";
    }

    @PostMapping("/create")
    public String createBatch(@RequestParam Long cuttingLotId,
                              @RequestParam Long washingUnitId,
                              @RequestParam String colorName,
                              @RequestParam int piecesIn,
                              @RequestParam(required = false) String notes,
                              RedirectAttributes redirectAttributes) {
        try {
            WashingBatch batch = washingService.createBatch(cuttingLotId, washingUnitId, colorName, piecesIn, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Washing Batch " + batch.getBatchNo() + " initiated for color " + colorName);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error initiating wash batch: " + e.getMessage());
        }
        return "redirect:/washing/batches";
    }

    @PostMapping("/{id}/complete")
    public String completeWash(@PathVariable Long id,
                               @RequestParam int piecesOut,
                               @RequestParam double shrinkagePct,
                               @RequestParam int rejectionPieces,
                               @RequestParam(required = false) String notes,
                               RedirectAttributes redirectAttributes) {
        try {
            WashingBatch batch = washingService.completeWash(id, piecesOut, shrinkagePct, rejectionPieces, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Batch " + batch.getBatchNo() + " wash completed! Shrinkage: " + shrinkagePct + "%");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error completing wash: " + e.getMessage());
        }
        return "redirect:/washing/batches";
    }
}
