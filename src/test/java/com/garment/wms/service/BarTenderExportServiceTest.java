package com.garment.wms.service;

import com.garment.wms.dto.BarTenderRowDto;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BarTenderExportServiceTest {

    private final BarTenderExportService exportService = new BarTenderExportService();

    @Test
    void testExportToCsvContainsRequiredHeaders() throws IOException {
        BarTenderRowDto row = BarTenderRowDto.builder()
                .barcode("SET-LOT01-NAVY-001")
                .lotNo("LOT-01")
                .styleCode("STY-101")
                .designName("Oxford Button-Down")
                .color("Navy")
                .size("SET")
                .itemType("SET")
                .setCode("SET #1")
                .setRatio("30:1 / 32:3")
                .finishingUnit("Apex Finishing")
                .build();

        byte[] csvBytes = exportService.exportToCsv(List.of(row));
        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);

        // Verify headers
        assertTrue(csvContent.contains("Barcode"));
        assertTrue(csvContent.contains("LotNo"));
        assertTrue(csvContent.contains("StyleCode"));
        assertTrue(csvContent.contains("DesignName"));
        assertTrue(csvContent.contains("Color"));
        assertTrue(csvContent.contains("Size"));
        assertTrue(csvContent.contains("ItemType"));
        assertTrue(csvContent.contains("SetCode"));
        assertTrue(csvContent.contains("SetRatio"));
        assertTrue(csvContent.contains("FinishingUnit"));

        // Verify row values
        assertTrue(csvContent.contains("SET-LOT01-NAVY-001"));
        assertTrue(csvContent.contains("Oxford Button-Down"));
        assertTrue(csvContent.contains("Apex Finishing"));
    }
}
