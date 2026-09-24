package com.garment.wms.controller;

import com.garment.wms.dto.ColorAllocationDto;
import com.garment.wms.dto.LotCreationRequest;
import com.garment.wms.model.*;
import com.garment.wms.repository.*;
import com.garment.wms.service.CuttingLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/lots")
@RequiredArgsConstructor
public class LotController {

    private final CuttingLotRepository cuttingLotRepository;
    private final CuttingLotColorRepository cuttingLotColorRepository;
    private final StyleRepository styleRepository;
    private final SizeSetRepository sizeSetRepository;
    private final SetBundleRepository setBundleRepository;
    private final SinglePieceRepository singlePieceRepository;
    private final CuttingLotService cuttingLotService;

    @GetMapping
    public String listLots(Model model) {
        model.addAttribute("lots", cuttingLotRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("activeNav", "lots");
        return "lots/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("styles", styleRepository.findAll());
        model.addAttribute("sizeSets", sizeSetRepository.findAll());
        model.addAttribute("activeNav", "lots");
        return "lots/create";
    }

    @PostMapping("/create")
    public String createLot(
            @RequestParam("lotNumber") String lotNumber,
            @RequestParam("styleId") Long styleId,
            @RequestParam("sizeSetId") Long sizeSetId,
            @RequestParam("colorName[]") List<String> colorNames,
            @RequestParam("colorPieces[]") List<Integer> colorPieces,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            List<ColorAllocationDto> colors = new ArrayList<>();
            int totalPieces = 0;
            for (int i = 0; i < colorNames.size(); i++) {
                String cName = colorNames.get(i).trim();
                int pieces = (i < colorPieces.size() && colorPieces.get(i) != null) ? colorPieces.get(i) : 0;
                if (!cName.isEmpty() && pieces > 0) {
                    colors.add(ColorAllocationDto.builder()
                            .colorName(cName)
                            .piecesAllocated(pieces)
                            .build());
                    totalPieces += pieces;
                }
            }

            if (colors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please provide at least one color with allocated pieces > 0.");
                return "redirect:/lots/create";
            }

            LotCreationRequest request = LotCreationRequest.builder()
                    .lotNumber(lotNumber)
                    .styleId(styleId)
                    .sizeSetId(sizeSetId)
                    .totalPieces(totalPieces)
                    .colors(colors)
                    .build();

            String user = authentication != null ? authentication.getName() : "ADMIN";
            CuttingLot lot = cuttingLotService.createCuttingLot(request, user);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Cutting Lot '" + lot.getLotNumber() + "' created successfully with " + totalPieces + " pieces across " + colors.size() + " color(s)!");

            return "redirect:/lots/" + lot.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create cutting lot: " + e.getMessage());
            return "redirect:/lots/create";
        }
    }

    @GetMapping("/{id}")
    public String viewLot(@PathVariable("id") Long id, Model model) {
        CuttingLot lot = cuttingLotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lot not found: " + id));

        List<CuttingLotColor> colors = cuttingLotColorRepository.findByCuttingLotId(id);
        List<SetBundle> bundles = setBundleRepository.findByCuttingLotIdOrderBySetIndexAsc(id);
        List<SinglePiece> pieces = singlePieceRepository.findByCuttingLotId(id);

        long inWarehouseCount = pieces.stream().filter(p -> p.getStatus() == InventoryStatus.IN_WAREHOUSE).count();
        long dispatchedCount = pieces.stream().filter(p -> p.getStatus() == InventoryStatus.DISPATCHED).count();
        long plannedCount = pieces.stream().filter(p -> p.getStatus() == InventoryStatus.PLANNED).count();

        model.addAttribute("lot", lot);
        model.addAttribute("colors", colors);
        model.addAttribute("bundles", bundles);
        model.addAttribute("pieces", pieces);
        model.addAttribute("inWarehouseCount", inWarehouseCount);
        model.addAttribute("dispatchedCount", dispatchedCount);
        model.addAttribute("plannedCount", plannedCount);
        model.addAttribute("activeNav", "lots");

        return "lots/view";
    }
}
