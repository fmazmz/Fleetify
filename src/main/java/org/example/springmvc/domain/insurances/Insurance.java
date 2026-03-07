package org.example.springmvc.domain.insurances;

import java.math.BigDecimal;

public interface Insurance {
    BigDecimal getPrice(InsuranceType type);
}
