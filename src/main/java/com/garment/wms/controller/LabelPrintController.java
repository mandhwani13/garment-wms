package com.garment.wms.controller;

import com.garment.wms.dto.BarTenderRowDto;
import com.garment.wms.model.CuttingLot;
import com.garment.wms.model.LabelPrintSettings;
import com.garment.wms.repository.CuttingLotRepository;
import com.garment.wms.repository.LabelPrintSettingsRepository;
import com.garment.wms.service.BarTenderExportService;
import com.garment.wms.service.BarcodeGeneratorService;
import com.garment.wms.service.CuttingLotService;
import com.garment.wms.util.printer.LabelDimensions;
import com.garment.wms.util.printer.TsplCommandBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/labels")
@RequiredArgsConstructor
public class LabelPrintController {

    private final CuttingLotRepository cuttingLotRepository;
    private final CuttingLotService cuttingLotService;
    private final BarTenderExportService barTenderExportService;
    private final BarcodeGeneratorService barcodeGeneratorService;
    private final LabelPrintSettingsRepository labelSettingsRepository;

    @Getter
    @Setter
    public static class LabelCardView {
        private String barcode;
        private String barcodeImageBase64;
        private String qrImageBase64;
        private String lotNo;
        private String styleCode;
        private String designName;
        private String color;
        private String size;
        private String itemType;
        private String setCode;
        private String setRatio;
        private String finishingUnit;
    }

    /**
     * Option A: Dynamic BarTender Data Source Export
     */
    @GetMapping("/export/bartender/{lotId}")
    public ResponseEntity<byte[]> exportBarTenderCsv(
            @PathVariable("lotId") Long lotId,
            @RequestParam(value = "type", defaultValue = "ALL") String itemType) throws IOException {

        CuttingLot lot = cuttingLotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot not found: " + lotId));

        List<BarTenderRowDto> rows = cuttingLotService.getBarTenderRows(lotId, itemType);
        byte[] csvBytes = barTenderExportService.exportToCsv(rows);

        String filename = String.format("BarTender_Lot_%s_%s.csv", lot.getLotNumber(), itemType.toLowerCase());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }

    /**
     * Option B: Browser Print Preview with exact mm dimensions and ZXing barcode rendering
     */
    @GetMapping("/preview/{lotId}")
    public String previewLabels(
            @PathVariable("lotId") Long lotId,
            @RequestParam(value = "sizePreset", defaultValue = "50X38") String sizePreset,
            @RequestParam(value = "customWidth", required = false) Double customWidth,
            @RequestParam(value = "customHeight", required = false) Double customHeight,
            @RequestParam(value = "type", defaultValue = "ALL") String itemType,
            @RequestParam(value = "barcodeFormat", defaultValue = "CODE128") String barcodeFormat,
            Model model) {

        CuttingLot lot = cuttingLotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot not found: " + lotId));

        double widthMm;
        double heightMm;

        if ("CUSTOM".equalsIgnoreCase(sizePreset) && customWidth != null && customHeight != null) {
            widthMm = customWidth;
            heightMm = customHeight;
        } else {
            LabelDimensions dim = LabelDimensions.fromPreset(sizePreset);
            widthMm = dim.getWidthMm();
            heightMm = dim.getHeightMm();
        }

        List<BarTenderRowDto> rawItems = cuttingLotService.getBarTenderRows(lotId, itemType);
        List<LabelCardView> cards = new ArrayList<>();

        for (BarTenderRowDto r : rawItems) {
            LabelCardView card = new LabelCardView();
            card.setBarcode(r.getBarcode());
            card.setLotNo(r.getLotNo());
            card.setStyleCode(r.getStyleCode());
            card.setDesignName(r.getDesignName());
            card.setColor(r.getColor());
            card.setSize(r.getSize());
            card.setItemType(r.getItemType());
            card.setSetCode(r.getSetCode());
            card.setSetRatio(r.getSetRatio());
            card.setFinishingUnit(r.getFinishingUnit());

            // Pre-generate ZXing barcode representations
            if ("QR".equalsIgnoreCase(barcodeFormat)) {
                card.setQrImageBase64(barcodeGeneratorService.generateQrCodeBase64(r.getBarcode(), 180));
            } else {
                card.setBarcodeImageBase64(barcodeGeneratorService.generateCode128Base64(r.getBarcode(), 280, 70));
                // Master labels get both Code128 and QR
                if (widthMm >= 90) {
                    card.setQrImageBase64(barcodeGeneratorService.generateQrCodeBase64(r.getBarcode(), 140));
                }
            }
            cards.add(card);
        }

        model.addAttribute("lot", lot);
        model.addAttribute("cards", cards);
        model.addAttribute("sizePreset", sizePreset);
        model.addAttribute("widthMm", widthMm);
        model.addAttribute("heightMm", heightMm);
        model.addAttribute("itemType", itemType);
        model.addAttribute("barcodeFormat", barcodeFormat);
        model.addAttribute("activeNav", "labels");

        return "labels/print-preview";
    }

    /**
     * Option C: Native TSPL (TSC Printer Language) Command File Download (.prn / .txt)
     */
    @GetMapping("/export/tspl/{lotId}")
    public ResponseEntity<byte[]> exportTsplCommands(
            @PathVariable("lotId") Long lotId,
            @RequestParam(value = "sizePreset", defaultValue = "50X38") String sizePreset,
            @RequestParam(value = "customWidth", required = false) Double customWidth,
            @RequestParam(value = "customHeight", required = false) Double customHeight,
            @RequestParam(value = "type", defaultValue = "ALL") String itemType,
            @RequestParam(value = "density", defaultValue = "8") int density,
            @RequestParam(value = "direction", defaultValue = "1") int direction) {

        CuttingLot lot = cuttingLotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot not found: " + lotId));

        double widthMm;
        double heightMm;
        if ("CUSTOM".equalsIgnoreCase(sizePreset) && customWidth != null && customHeight != null) {
            widthMm = customWidth;
            heightMm = customHeight;
        } else {
            LabelDimensions dim = LabelDimensions.fromPreset(sizePreset);
            widthMm = dim.getWidthMm();
            heightMm = dim.getHeightMm();
        }

        List<BarTenderRowDto> items = cuttingLotService.getBarTenderRows(lotId, itemType);
        TsplCommandBuilder builder = new TsplCommandBuilder(widthMm, heightMm, density, direction);
        builder.appendBatch(items);

        byte[] tsplBytes = builder.build().getBytes(StandardCharsets.US_ASCII);
        String filename = String.format("TSC_%s_%s_%dx%dmm.prn", lot.getLotNumber(), itemType.toLowerCase(), (int) widthMm, (int) heightMm);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(tsplBytes);
    }

    @GetMapping("/settings")
    public String labelSettings(Model model) {
        model.addAttribute("presets", labelSettingsRepository.findAll());
        model.addAttribute("activeNav", "label-settings");
        return "labels/settings";
    }
}
