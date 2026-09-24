package com.garment.wms.service;

import com.garment.wms.dto.ColorAllocationDto;
import com.garment.wms.dto.LotCreationRequest;
import com.garment.wms.model.*;
import com.garment.wms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitService implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final StyleRepository styleRepository;
    private final SizeSetRepository sizeSetRepository;
    private final CuttingLotRepository cuttingLotRepository;
    private final CuttingLotService cuttingLotService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedSampleCuttingLot();
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("master")) {
            userRepository.save(UserAccount.builder()
                    .username("master")
                    .password(passwordEncoder.encode("master123"))
                    .fullName("Master Administrator")
                    .role(Role.ROLE_MASTER)
                    .enabled(true)
                    .build());
            log.info("Default MASTER user seeded: username=master");
        }

        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(UserAccount.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Operations Admin")
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .build());
            log.info("Default ADMIN user seeded: username=admin");
        }

        if (!userRepository.existsByUsername("operator")) {
            userRepository.save(UserAccount.builder()
                    .username("operator")
                    .password(passwordEncoder.encode("operator123"))
                    .fullName("Warehouse Scanner Operator")
                    .role(Role.ROLE_WAREHOUSE_USER)
                    .enabled(true)
                    .build());
            log.info("Default OPERATOR user seeded: username=operator");
        }
    }

    private void seedSampleCuttingLot() {
        if (cuttingLotRepository.countTotalLots() == 0) {
            Style style = styleRepository.findByStyleCode("STY-101").orElse(null);
            SizeSet sizeSet = sizeSetRepository.findBySetName("Ratio 12 Set (30-36)").orElse(null);

            if (style != null && sizeSet != null) {
                LotCreationRequest lotRequest = LotCreationRequest.builder()
                        .lotNumber("LOT-2026-001")
                        .styleId(style.getId())
                        .sizeSetId(sizeSet.getId())
                        .totalPieces(84) // 48 pcs navy + 36 pcs olive
                        .colors(List.of(
                                ColorAllocationDto.builder().colorName("Navy Blue").piecesAllocated(48).build(), // 4 complete sets
                                ColorAllocationDto.builder().colorName("Olive Green").piecesAllocated(36).build() // 3 complete sets
                        ))
                        .build();

                try {
                    cuttingLotService.createCuttingLot(lotRequest, "SYSTEM_INIT");
                    log.info("Sample Cutting Lot LOT-2026-001 seeded with 84 pieces (7 complete bundles).");
                } catch (Exception e) {
                    log.warn("Sample cutting lot initialization skipped: {}", e.getMessage());
                }
            }
        }
    }
}
