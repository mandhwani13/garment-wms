package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "barcode_type", nullable = false, length = 20)
    private BarcodeType barcodeType;

    @Column(name = "barcode_value", nullable = false, length = 100)
    private String barcodeValue;

    @Column(name = "pieces_count", nullable = false)
    @Builder.Default
    private int piecesCount = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cutting_lot_id")
    private CuttingLot cuttingLot;

    @Column(name = "style_code", length = 50)
    private String styleCode;

    @Column(name = "color_name", length = 50)
    private String colorName;

    @Column(name = "size_name", length = 50)
    private String sizeName;

    @Column(name = "party_name", length = 100)
    private String partyName;

    @Column(name = "reference_no", length = 100)
    private String referenceNo;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "performed_by", length = 50)
    private String performedBy;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
