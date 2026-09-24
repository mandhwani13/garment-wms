package com.garment.wms.controller;

import com.garment.wms.dto.StockFilterDto;
import com.garment.wms.dto.StockSummaryDto;
import com.garment.wms.model.InventoryStatus;
import com.garment.wms.model.SinglePiece;
import com.garment.wms.repository.*;
import com.garment.wms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final FinishingUnitRepository finishingUnitRepository;
    private final CustomerRepository customerRepository;
    private final StyleRepository styleRepository;
    private final CuttingLotRepository cuttingLotRepository;
    private final InventoryTransactionRepository transactionRepository;

    @GetMapping("/scanner")
    public String scanner(
            @RequestParam(value = "mode", defaultValue = "INWARD") String mode,
            Model model) {
        model.addAttribute("mode", mode.toUpperCase());
        model.addAttribute("finishingUnits", finishingUnitRepository.findByActiveTrue());
        model.addAttribute("customers", customerRepository.findByActiveTrue());
        model.addAttribute("activeNav", "scanner");
        return "scanner/index";
    }

    @GetMapping("/stock")
    public String stock(
            @RequestParam(value = "lotId", required = false) Long lotId,
            @RequestParam(value = "styleId", required = false) Long styleId,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "unitId", required = false) Long unitId,
            @RequestParam(value = "status", required = false) InventoryStatus status,
            Model model) {

        StockFilterDto filter = StockFilterDto.builder()
                .lotId(lotId)
                .styleId(styleId)
                .color(color != null && !color.isBlank() ? color.trim() : null)
                .size(size != null && !size.isBlank() ? size.trim() : null)
                .unitId(unitId)
                .status(status)
                .build();

        List<SinglePiece> stockItems = inventoryService.filterStock(filter);
        StockSummaryDto summary = inventoryService.getStockSummary();

        model.addAttribute("stockItems", stockItems);
        model.addAttribute("summary", summary);
        model.addAttribute("filter", filter);
        model.addAttribute("styles", styleRepository.findAll());
        model.addAttribute("lots", cuttingLotRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("finishingUnits", finishingUnitRepository.findByActiveTrue());
        model.addAttribute("statuses", InventoryStatus.values());
        model.addAttribute("activeNav", "stock");

        return "inventory/stock";
    }

    @GetMapping("/transactions")
    public String transactions(Model model) {
        model.addAttribute("transactions", transactionRepository.findTop50ByOrderByCreatedAtDesc());
        model.addAttribute("activeNav", "transactions");
        return "inventory/transactions";
    }
}
