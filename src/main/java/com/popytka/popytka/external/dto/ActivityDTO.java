package com.popytka.popytka.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDTO {

    private Long id;
    private String name;
    private String description;
    private Price price;

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price {

        private BigDecimal amount;
        private String currencyCode;

        public BigDecimal getPriceInBYN() {
            return Price.getPriceInBYN(this);
        }

        public static BigDecimal getPriceInBYN(Price price) {
            Currencies currency = Arrays.stream(Currencies.values())
                    .filter(currencies -> currencies.code.equals(price.getCurrencyCode()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported currency code: " + price.getCurrencyCode()));

            return price.getAmount().multiply(currency.rateToBYN);
        }

        @AllArgsConstructor
        public enum Currencies {
            USD("USD", new BigDecimal("3.05")),
            EUR("EUR", new BigDecimal("3.45")),
            RUB("RUB", new BigDecimal("0.0364"));

            private final String code;
            private final BigDecimal rateToBYN;
        }
    }
}
