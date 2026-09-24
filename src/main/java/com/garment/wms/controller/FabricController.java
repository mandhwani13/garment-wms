package com.garment.wms.controller;

import com.garment.wms.model.FabricMaster;
import com.garment.wms.model.FabricPurchaseOrder;
import com.garment.wms.model.PartyType;
import com.garment.wms.repository.FabricMasterRepository;
import com.garment.wms.repository.PartyRepository;
import com.garment.wms.service.FabricProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/fabric")
@RequiredArgsConstructor
public class FabricController {

    private final FabricProcurementService procurementService;
    private final FabricMasterRepository fabricRepository;
    private final PartyRepository partyRepository;

    @GetMapping("/masters")
    public String listFabrics(Model model) {
        model.addAttribute("fabrics", fabricRepository.findAll());
        model.addAttribute("newFabric", new FabricMaster());
        model.addAttribute("activeNav", "fabric-masters");
        return "fabric/masters";
    }

    @PostMapping("/masters/create")
    public String createFabric(@ModelAttribute FabricMaster fabric, RedirectAttributes redirectAttributes) {
        try {
            if (fabricRepository.existsByShortNumber(fabric.getShortNumber())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fabric Short No already exists!");
                return "redirect:/fabric/masters";
            }
            fabric.setShortNumber(fabric.getShortNumber().trim().toUpperCase());
            fabricRepository.save(fabric);
            redirectAttributes.addFlashAttribute("successMessage", "Fabric master created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating fabric: " + e.getMessage());
        }
        return "redirect:/fabric/masters";
    }

    @GetMapping("/orders")
    public String listPurchaseOrders(Model model) {
        model.addAttribute("orders", procurementService.getAllOrders());
        model.addAttribute("mills", partyRepository.findByPartyTypeAndActiveTrue(PartyType.FABRIC_MILL));
        model.addAttribute("stitchingUnits", partyRepository.findByPartyTypeAndActiveTrue(PartyType.STITCHING_UNIT));
        model.addAttribute("fabrics", fabricRepository.findByActiveTrue());
        model.addAttribute("activeNav", "fabric-orders");
        return "fabric/orders";
    }

    @PostMapping("/orders/create")
    public String createPO(@RequestParam Long millId,
                           @RequestParam Long stitchingUnitId,
                           @RequestParam Long fabricId,
                           @RequestParam String poBillNo,
                           @RequestParam int totalRolls,
                           @RequestParam double totalBilledMeters,
                           @RequestParam double ratePerMeter,
                           RedirectAttributes redirectAttributes) {
        try {
            FabricPurchaseOrder po = procurementService.createFabricPO(millId, stitchingUnitId, fabricId, poBillNo, totalRolls, totalBilledMeters, ratePerMeter);
            redirectAttributes.addFlashAttribute("successMessage", "Consignment " + po.getConsignmentNo() + " issued with Transit Slip " + po.getTransitSlipNo());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error issuing PO: " + e.getMessage());
        }
        return "redirect:/fabric/orders";
    }

    @GetMapping("/orders/{id}")
    public String viewPO(@PathVariable Long id, Model model) {
        FabricPurchaseOrder po = procurementService.getOrderById(id);
        model.addAttribute("po", po);
        model.addAttribute("activeNav", "fabric-orders");
        return "fabric/view_po";
    }

    @PostMapping("/orders/{id}/inward")
    public String inwardRolls(@PathVariable Long id,
                              @RequestParam List<Double> receivedMeters,
                              @RequestParam List<Double> weights,
                              @RequestParam(required = false) List<Boolean> defects,
                              @RequestParam(required = false) List<String> defectNotes,
                              @RequestParam(required = false) String adminNotes,
                              RedirectAttributes redirectAttributes) {
        try {
            procurementService.processRollInward(id, receivedMeters, weights, defects, defectNotes, adminNotes);
            redirectAttributes.addFlashAttribute("successMessage", "Rolls physically inspected and GRN generated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Inward verification failed: " + e.getMessage());
        }
        return "redirect:/fabric/orders/" + id;
    }

    @PostMapping("/orders/{id}/approve")
    public String approveDiscrepancy(@PathVariable Long id, @RequestParam String approvalNotes, RedirectAttributes redirectAttributes) {
        try {
            procurementService.approveDiscrepancy(id, approvalNotes);
            redirectAttributes.addFlashAttribute("successMessage", "Discrepancy approved by Admin!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Approval failed: " + e.getMessage());
        }
        return "redirect:/fabric/orders/" + id;
    }
}
