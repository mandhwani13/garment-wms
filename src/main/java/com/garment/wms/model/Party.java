package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "parties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "party_code", nullable = false, unique = true, length = 50)
    private String partyCode;

    @Column(name = "party_name", nullable = false, length = 100)
    private String partyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "party_type", nullable = false, length = 30)
    private PartyType partyType;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(length = 30)
    private String phone;

    @Column(length = 30)
    private String gstin;

    @Column(columnDefinition = "TEXT")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_location_id")
    private Location linkedLocation;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
