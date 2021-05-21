package model.dto.coincap_api;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Builder
@Getter
public class AssetCsv {

    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetCsv that = (AssetCsv) o;
        return symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
