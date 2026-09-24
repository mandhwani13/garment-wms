package com.garment.wms.service;

import com.garment.wms.model.*;
import com.garment.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkerApprovalService {

    private final MarkerApprovalRepository markerRepository;
    private final StyleRepository styleRepository;
    private final PartyRepository partyRepository;
    private final FabricMasterRepository fabricRepository;

    public List<MarkerApproval> getAllMarkers() {
        return markerRepository.findAllByOrderByCreatedAtDesc();
    }

    public MarkerApproval getMarkerById(Long id) {
        return markerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marker approval not found: " + id));
    }

    @Transactional
    public MarkerApproval submitMarkerDrawing(Long styleId, Long stitchingUnitId, Long fabricId,
                                            double rollWidth, double markerLength, int layCount, int expectedPieces) {
        Style style = styleRepository.findById(styleId)
                .orElseThrow(() -> new IllegalArgumentException("Style not found"));
        Party unit = partyRepository.findById(stitchingUnitId)
                .orElseThrow(() -> new IllegalArgumentException("Stitching unit not found"));
        FabricMaster fabric = fabricRepository.findById(fabricId)
                .orElseThrow(() -> new IllegalArgumentException("Fabric not found"));

        double totalFabricUsedMeters = markerLength * layCount;
        double calculatedAverage = expectedPieces > 0 ? (totalFabricUsedMeters / expectedPieces) : 0.0;

        String markerCode = "MKR-" + style.getStyleCode() + "-" + (System.currentTimeMillis() % 10000);

        MarkerApproval marker = MarkerApproval.builder()
                .markerCode(markerCode)
                .style(style)
                .stitchingUnit(unit)
                .fabric(fabric)
                .rollWidthUtilized(rollWidth)
                .markerLengthMeters(markerLength)
                .layCount(layCount)
                .calculatedAverageMeters(Math.round(calculatedAverage * 100.0) / 100.0)
                .expectedPieces(expectedPieces)
                .status("PENDING")
                .createdAt(OffsetDateTime.now())
                .build();

        return markerRepository.save(marker);
    }

    @Transactional
    public MarkerApproval reviewMarker(Long id, boolean approve, String remarks) {
        MarkerApproval marker = getMarkerById(id);
        marker.setStatus(approve ? "APPROVED" : "REJECTED");
        marker.setAdminRemarks(remarks);
        marker.setApprovedAt(OffsetDateTime.now());
        return markerRepository.save(marker);
    }
}
