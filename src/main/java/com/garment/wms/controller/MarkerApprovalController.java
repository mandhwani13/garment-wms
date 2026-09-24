package com.garment.wms.controller;

import com.garment.wms.model.MarkerApproval;
import com.garment.wms.model.PartyType;
import com.garment.wms.repository.FabricMasterRepository;
import com.garment.wms.repository.PartyRepository;
import com.garment.wms.repository.StyleRepository;
import com.garment.wms.service.MarkerApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cutting/approvals")
@RequiredArgsConstructor
public class MarkerApprovalController {

    private final MarkerApprovalService markerService;
    private final StyleRepository styleRepository;
    private final PartyRepository partyRepository;
    private final FabricMasterRepository fabricRepository;

    @GetMapping
    public String listMarkers(Model model) {
        model.addAttribute("markers", markerService.getAllMarkers());
        model.addAttribute("styles", styleRepository.findAll());
        model.addAttribute("stitchingUnits", partyRepository.findByPartyTypeAndActiveTrue(PartyType.STITCHING_UNIT));
        model.addAttribute("fabrics", fabricRepository.findByActiveTrue());
        model.addAttribute("activeNav", "marker-approvals");
        return "cutting/approvals";
    }

    @PostMapping("/submit")
    public String submitMarker(@RequestParam Long styleId,
                               @RequestParam Long stitchingUnitId,
                               @RequestParam Long fabricId,
                               @RequestParam double rollWidth,
                               @RequestParam double markerLength,
                               @RequestParam int layCount,
                               @RequestParam int expectedPieces,
                               RedirectAttributes redirectAttributes) {
        try {
            MarkerApproval mkr = markerService.submitMarkerDrawing(styleId, stitchingUnitId, fabricId, rollWidth, markerLength, layCount, expectedPieces);
            redirectAttributes.addFlashAttribute("successMessage", "Marker Drawing " + mkr.getMarkerCode() + " submitted (Calculated Avg: " + mkr.getCalculatedAverageMeters() + "m/pc). Awaiting Admin Approval.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Submission failed: " + e.getMessage());
        }
        return "redirect:/cutting/approvals";
    }

    @PostMapping("/{id}/review")
    public String reviewMarker(@PathVariable Long id,
                               @RequestParam boolean approve,
                               @RequestParam(required = false) String remarks,
                               RedirectAttributes redirectAttributes) {
        try {
            MarkerApproval mkr = markerService.reviewMarker(id, approve, remarks);
            redirectAttributes.addFlashAttribute("successMessage", "Marker " + mkr.getMarkerCode() + " status updated to " + mkr.getStatus());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Review failed: " + e.getMessage());
        }
        return "redirect:/cutting/approvals";
    }
}
