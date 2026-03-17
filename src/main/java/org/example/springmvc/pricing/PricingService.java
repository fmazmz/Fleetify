package org.example.springmvc.pricing;

import org.example.springmvc.insurances.Insurance;
import org.example.springmvc.insurances.InsuranceType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PricingService {

    private final Insurance insurance;

    public PricingService(Insurance insurance) {
        this.insurance = insurance;
    }

    public BigDecimal calculateBookingPrice(
            BigDecimal hourlyCarPrice,
            Instant startTime,
            Instant endTime,
            InsuranceType insuranceType
    ) {
        long hours = Duration.between(startTime, endTime).toHours();
        BigDecimal carCost = hourlyCarPrice.multiply(BigDecimal.valueOf(hours));
        BigDecimal insuranceCost = insurance.getPrice(insuranceType);

        return carCost.add(insuranceCost);
    }

    public BigDecimal getInsurancePrice(InsuranceType type) {
        return insurance.getPrice(type);
    }

    public Map<InsuranceType, BigDecimal> getInsurancePrices() {
        Map<InsuranceType, BigDecimal> prices = new LinkedHashMap<>();
        for (InsuranceType type : InsuranceType.values()) {
            prices.put(type, insurance.getPrice(type));
        }
        return prices;
    }

    public Map<InsuranceType, String> getInsuranceDisplayNames() {
        return Map.of(
                InsuranceType.BASIC, "Basic",
                InsuranceType.PREMIUM, "Premium",
                InsuranceType.FULL_COVERAGE, "Full Coverage"
        );
    }
}
