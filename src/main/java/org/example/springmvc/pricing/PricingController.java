package org.example.springmvc.pricing;

import org.example.springmvc.insurances.InsuranceType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/insurance")
    public ResponseEntity<Map<InsuranceType, BigDecimal>> getInsurancePrices() {
        return ResponseEntity.ok(pricingService.getInsurancePrices());
    }
}
