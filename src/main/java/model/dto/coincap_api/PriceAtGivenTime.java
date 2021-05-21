package model.dto.coincap_api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceAtGivenTime {

    private BigDecimal priceUsd;
    private LocalDateTime date;
    private long time;

}
