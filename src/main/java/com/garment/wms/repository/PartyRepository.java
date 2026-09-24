package com.garment.wms.repository;

import com.garment.wms.model.Party;
import com.garment.wms.model.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
    Optional<Party> findByPartyCode(String partyCode);
    List<Party> findByPartyTypeAndActiveTrue(PartyType partyType);
    List<Party> findByActiveTrue();
    boolean existsByPartyCode(String partyCode);
}
