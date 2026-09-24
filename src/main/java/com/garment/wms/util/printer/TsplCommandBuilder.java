package com.garment.wms.util.printer;

import com.garment.wms.dto.BarTenderRowDto;

import java.util.List;

/**
 * Utility for generating native TSPL (TSC Printer Language) command scripts
 * compatible with TSC Thermal Printers (e.g., TTP-244 Pro, TE200, TX200, MB240).
 */
public class TsplCommandBuilder {

    private final StringBuilder sb = new StringBuilder();
    private final double widthMm;
    private final double heightMm;
    private final int density;
    private final int direction;

    public TsplCommandBuilder(double widthMm, double heightMm, int density, int direction) {
        this.widthMm = widthMm > 0 ? widthMm : 50.0;
        this.heightMm = heightMm > 0 ? heightMm : 38.0;
        this.density = density > 0 ? density : 8;
        this.direction = (direction == 0 || direction == 1) ? direction : 1;
    }

    public static TsplCommandBuilder create(double widthMm, double heightMm) {
        return new TsplCommandBuilder(widthMm, heightMm, 8, 1);
    }

    public static TsplCommandBuilder create(LabelDimensions preset) {
        return new TsplCommandBuilder(preset.getWidthMm(), preset.getHeightMm(), 8, 1);
    }

    /**
     * Appends a complete label for a single piece garment item.
     */
    public TsplCommandBuilder appendSinglePieceLabel(BarTenderRowDto item) {
        beginLabel();

        // 50x25 mm or 50x38 mm formatting
        if (heightMm <= 28) {
            // Compact 50x25 tag
            // Line 1: Style & Size
            text(20, 15, "3", 1, 1, item.getStyleCode() + " | SZ: " + item.getSize());
            // Line 2: Color
            text(20, 42, "2", 1, 1, item.getColor());
            // Barcode (Code 128)
            barcode(20, 65, "128", 40, 1, item.getBarcode());
            // Footer: Lot No
            text(20, 140, "1", 1, 1, "LOT: " + item.getLotNo());
        } else if (widthMm >= 90) {
            // Master/wide format 100x50 mm
            text(30, 20, "4", 1, 1, "GARMENT PIECE TAG");
            text(30, 60, "3", 1, 1, "STYLE: " + item.getStyleCode() + " (" + item.getDesignName() + ")");
            text(30, 95, "3", 1, 1, "COLOR: " + item.getColor() + "   SIZE: " + item.getSize());
            text(30, 130, "2", 1, 1, "LOT: " + item.getLotNo() + "   SET: " + item.getSetCode());
            barcode(30, 170, "128", 65, 1, item.getBarcode());
            qrCode(600, 160, 4, item.getBarcode());
        } else {
            // Standard 50x38 mm format
            text(20, 20, "3", 1, 1, item.getStyleCode());
            text(20, 50, "2", 1, 1, truncate(item.getDesignName(), 24));
            text(20, 75, "3", 1, 1, "COL: " + item.getColor() + " | " + item.getSize());
            barcode(20, 110, "128", 55, 1, item.getBarcode());
            text(20, 210, "2", 1, 1, "LOT: " + item.getLotNo() + (item.getSetCode() != null ? " | " + item.getSetCode() : ""));
        }

        endLabel();
        return this;
    }

    /**
     * Appends a complete label for a set bundle / carton.
     */
    public TsplCommandBuilder appendSetBundleLabel(BarTenderRowDto item) {
        beginLabel();

        if (widthMm >= 90) {
            // 100x50 mm Master Carton / Bundle
            text(30, 20, "4", 1, 1, "[MASTER BUNDLE / PACK]");
            text(30, 65, "3", 1, 1, "STYLE: " + item.getStyleCode() + " - " + item.getDesignName());
            text(30, 100, "3", 1, 1, "COLOR: " + item.getColor() + " | SET: " + item.getSetCode());
            text(30, 135, "3", 1, 1, "RATIO / SIZES: " + item.getSetRatio());
            text(30, 170, "2", 1, 1, "LOT NO: " + item.getLotNo() + " | UNIT: " + (item.getFinishingUnit() != null ? item.getFinishingUnit() : "N/A"));
            barcode(30, 215, "128", 75, 1, item.getBarcode());
            qrCode(620, 200, 5, item.getBarcode());
        } else {
            // 50x38 mm or smaller Bundle Tag
            text(20, 15, "3", 1, 1, "[BUNDLE PACK]");
            text(20, 45, "3", 1, 1, item.getStyleCode() + " - " + item.getColor());
            text(20, 75, "2", 1, 1, "RATIO: " + truncate(item.getSetRatio(), 22));
            barcode(20, 110, "128", 55, 1, item.getBarcode());
            text(20, 210, "2", 1, 1, "LOT: " + item.getLotNo() + " | " + item.getSetCode());
        }

        endLabel();
        return this;
    }

    public TsplCommandBuilder appendBatch(List<BarTenderRowDto> items) {
        for (BarTenderRowDto item : items) {
            if ("SET".equalsIgnoreCase(item.getItemType())) {
                appendSetBundleLabel(item);
            } else {
                appendSinglePieceLabel(item);
            }
        }
        return this;
    }

    private void beginLabel() {
        sb.append(String.format("SIZE %.1f mm, %.1f mm\r\n", widthMm, heightMm));
        sb.append("GAP 2 mm, 0 mm\r\n");
        sb.append("DIRECTION ").append(direction).append("\r\n");
        sb.append("DENSITY ").append(density).append("\r\n");
        sb.append("CLS\r\n");
    }

    private void endLabel() {
        sb.append("PRINT 1, 1\r\n\r\n");
    }

    private void text(int x, int y, String font, int xMult, int yMult, String content) {
        String safeContent = content != null ? content.replace("\"", "'") : "";
        sb.append(String.format("TEXT %d, %d, \"%s\", 0, %d, %d, \"%s\"\r\n", x, y, font, xMult, yMult, safeContent));
    }

    private void barcode(int x, int y, String type, int height, int readable, String content) {
        String safeContent = content != null ? content.trim() : "";
        // BARCODE X, Y, "code type", height, human-readable (0/1), rotation, narrow, wide, "code"
        sb.append(String.format("BARCODE %d, %d, \"%s\", %d, %d, 0, 2, 2, \"%s\"\r\n", x, y, type, height, readable, safeContent));
    }

    private void qrCode(int x, int y, int cellWidth, String content) {
        String safeContent = content != null ? content.trim() : "";
        // QRCODE X, Y, ECC Level, cell width, mode, rotation, "content"
        sb.append(String.format("QRCODE %d, %d, L, %d, A, 0, \"%s\"\r\n", x, y, cellWidth, safeContent));
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen - 1) + "…";
    }

    public String build() {
        return sb.toString();
    }
}
