package com.garment.wms.util.printer;

import com.garment.wms.dto.BarTenderRowDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TsplCommandBuilderTest {

    @Test
    void testSinglePieceLabelGeneration() {
        BarTenderRowDto item = BarTenderRowDto.builder()
                .barcode("PC-LOT01-NAVY-32-001")
                .lotNo("LOT-01")
                .styleCode("STY-101")
                .designName("Oxford Cotton Shirt")
                .color("Navy")
                .size("32")
                .itemType("PIECE")
                .setCode("SET #1")
                .setRatio("30:1 / 32:3")
                .build();

        String tspl = TsplCommandBuilder.create(50.0, 38.0)
                .appendSinglePieceLabel(item)
                .build();

        assertNotNull(tspl);
        assertTrue(tspl.contains("SIZE 50.0 mm, 38.0 mm"));
        assertTrue(tspl.contains("GAP 2 mm, 0 mm"));
        assertTrue(tspl.contains("CLS"));
        assertTrue(tspl.contains("STY-101"));
        assertTrue(tspl.contains("PC-LOT01-NAVY-32-001"));
        assertTrue(tspl.contains("PRINT 1, 1"));
    }

    @Test
    void testSetBundleLabelGeneration() {
        BarTenderRowDto item = BarTenderRowDto.builder()
                .barcode("SET-LOT01-NAVY-001")
                .lotNo("LOT-01")
                .styleCode("STY-101")
                .designName("Oxford Cotton Shirt")
                .color("Navy")
                .size("ALL")
                .itemType("SET")
                .setCode("SET #1")
                .setRatio("30:1 / 32:3 / 34:3 / 36:2")
                .finishingUnit("Apex Finishing")
                .build();

        String tspl = TsplCommandBuilder.create(100.0, 50.0)
                .appendSetBundleLabel(item)
                .build();

        assertNotNull(tspl);
        assertTrue(tspl.contains("SIZE 100.0 mm, 50.0 mm"));
        assertTrue(tspl.contains("[MASTER BUNDLE / PACK]"));
        assertTrue(tspl.contains("SET-LOT01-NAVY-001"));
        assertTrue(tspl.contains("PRINT 1, 1"));
    }
}
