package com.garment.wms.controller;

import com.garment.wms.model.Customer;
import com.garment.wms.model.FinishingUnit;
import com.garment.wms.repository.CustomerRepository;
import com.garment.wms.repository.FinishingUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parties")
@RequiredArgsConstructor
public class PartyController {

    private final FinishingUnitRepository finishingUnitRepository;
    private final CustomerRepository customerRepository;

    @GetMapping("/finishing-units")
    public String listFinishingUnits(Model model) {
        model.addAttribute("units", finishingUnitRepository.findAll());
        model.addAttribute("newUnit", new FinishingUnit());
        model.addAttribute("activeNav", "parties-units");
        return "parties/units";
    }

    @PostMapping("/finishing-units/create")
    public String createFinishingUnit(@ModelAttribute FinishingUnit unit, RedirectAttributes redirectAttributes) {
        try {
            if (finishingUnitRepository.existsByUnitCode(unit.getUnitCode())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Finishing Unit Code '" + unit.getUnitCode() + "' already exists!");
                return "redirect:/parties/finishing-units";
            }
            unit.setUnitCode(unit.getUnitCode().trim().toUpperCase());
            finishingUnitRepository.save(unit);
            redirectAttributes.addFlashAttribute("successMessage", "Finishing Unit '" + unit.getUnitName() + "' added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding finishing unit: " + e.getMessage());
        }
        return "redirect:/parties/finishing-units";
    }

    @GetMapping("/customers")
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("newCustomer", new Customer());
        model.addAttribute("activeNav", "parties-customers");
        return "parties/customers";
    }

    @PostMapping("/customers/create")
    public String createCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        try {
            if (customerRepository.existsByCustomerCode(customer.getCustomerCode())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Customer Code '" + customer.getCustomerCode() + "' already exists!");
                return "redirect:/parties/customers";
            }
            customer.setCustomerCode(customer.getCustomerCode().trim().toUpperCase());
            customerRepository.save(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Customer '" + customer.getCustomerName() + "' added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding customer: " + e.getMessage());
        }
        return "redirect:/parties/customers";
    }
}
