package com.garment.wms.controller;

import com.garment.wms.model.JobWorkChallan;
import com.garment.wms.model.PartyType;
import com.garment.wms.repository.CuttingLotRepository;
import com.garment.wms.repository.PartyRepository;
import com.garment.wms.service.JobWorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/jobwork/challans")
@RequiredArgsConstructor
public class JobWorkController {

    private final JobWorkService jobWorkService;
    private final CuttingLotRepository cuttingLotRepository;
    private final PartyRepository partyRepository;

    @GetMapping
    public String listChallans(Model model) {
        model.addAttribute("challans", jobWorkService.getAllChallans());
        model.addAttribute("lots", cuttingLotRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("jobWorkers", partyRepository.findByPartyTypeAndActiveTrue(PartyType.JOB_WORKER));
        model.addAttribute("activeNav", "jobwork-challans");
        return "jobwork/challans";
    }

    @PostMapping("/issue")
    public String issueChallan(@RequestParam Long cuttingLotId,
                               @RequestParam Long jobWorkerId,
                               @RequestParam String componentType,
                               @RequestParam int piecesSent,
                               @RequestParam(required = false) String notes,
                               RedirectAttributes redirectAttributes) {
        try {
            JobWorkChallan ch = jobWorkService.issueChallan(cuttingLotId, jobWorkerId, componentType, piecesSent, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Subcontracting Challan " + ch.getChallanNo() + " issued (" + piecesSent + " pcs)");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error issuing challan: " + e.getMessage());
        }
        return "redirect:/jobwork/challans";
    }

    @PostMapping("/{id}/receive")
    public String receiveChallan(@PathVariable Long id,
                                 @RequestParam int piecesReceived,
                                 @RequestParam int damagedPieces,
                                 @RequestParam(required = false) String notes,
                                 RedirectAttributes redirectAttributes) {
        try {
            JobWorkChallan ch = jobWorkService.receiveChallan(id, piecesReceived, damagedPieces, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Challan " + ch.getChallanNo() + " inward received (" + piecesReceived + " good, " + damagedPieces + " damaged)");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error receiving challan: " + e.getMessage());
        }
        return "redirect:/jobwork/challans";
    }
}
