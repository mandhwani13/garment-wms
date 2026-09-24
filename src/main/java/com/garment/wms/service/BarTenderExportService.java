package com.garment.wms.service;

import com.garment.wms.dto.BarTenderRowDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Service to export generated lot barcodes into formatted CSV specifically
 * mapped for BarTender Commander / Database Connection.
 * Headers: Barcode, LotNo, StyleCode, DesignName, Color, Size, ItemType, SetCode, SetRatio, FinishingUnit
 */
@Service
public class BarTenderExportService {

    public static final String[] BARTENDER_HEADERS = {
            "Barcode",
            "LotNo",
            "StyleCode",
            "DesignName",
            "Color",
            "Size",
            "ItemType",
            "SetCode",
            "SetRatio",
            "FinishingUnit"
    };

    public byte[] exportToCsv(List<BarTenderRowDto> rows) throws IOException {
        StringWriter sw = new StringWriter();
        // UTF-8 BOM helps BarTender and Excel recognize encoding immediately
        sw.write('\ufeff');

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(BARTENDER_HEADERS)
                .setRecordSeparator("\r\n")
                .build();

        try (CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
            for (BarTenderRowDto row : rows) {
                printer.printRecord(
                        row.getBarcode(),
                        row.getLotNo(),
                        row.getStyleCode(),
                        row.getDesignName(),
                        row.getColor(),
                        row.getSize(),
                        row.getItemType(),
                        row.getSetCode(),
                        row.getSetRatio(),
                        row.getFinishingUnit()
                );
            }
        }

        return sw.toString().getBytes(StandardCharsets.UTF_8);
    }
}
