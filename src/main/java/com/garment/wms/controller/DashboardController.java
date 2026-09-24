package com.garment.wms.controller;

import com.garment.wms.dto.StockSummaryDto;
import com.garment.wms.model.InventoryTransaction;
import com.garment.wms.repository.InventoryTransactionRepository;
import com.garment.wms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final InventoryService inventoryService;
    private final InventoryTransactionRepository transactionRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        StockSummaryDto summary = inventoryService.getStockSummary();
        List<InventoryTransaction> recentTransactions = transactionRepository.findTop50ByOrderByCreatedAtDesc();

        model.addAttribute("summary", summary);
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("activeNav", "dashboard");
        return "dashboard/index";
    }
}
