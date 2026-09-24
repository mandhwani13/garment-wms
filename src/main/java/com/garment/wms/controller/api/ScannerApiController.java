package com.garment.wms.controller.api;

import com.garment.wms.dto.ScanRequestDto;
import com.garment.wms.dto.ScanResponseDto;
import com.garment.wms.dto.StockSummaryDto;
import com.garment.wms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class ScannerApiController {

    private final InventoryService inventoryService;

    @PostMapping("/scan")
    public ResponseEntity<ScanResponseDto> scan(
            @RequestBody ScanRequestDto request,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "OPERATOR";
        ScanResponseDto response = inventoryService.processScan(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<StockSummaryDto> summary() {
        return ResponseEntity.ok(inventoryService.getStockSummary());
    }
}
