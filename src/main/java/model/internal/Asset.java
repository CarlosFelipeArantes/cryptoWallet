package model.internal;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Builder
@Getter
public class Asset implements Comparable {

    private String id;
    private String symbol;
    private String name;
    private BigDecimal currentPriceUsd;
    private BigDecimal olderPriceUsd;
    private BigDecimal performanceFromOlderToCurrent;
    private BigDecimal quantity;

    @Override
    public int compareTo(Object o) {
        Asset asset = (Asset) o;
        return this.performanceFromOlderToCurrent.compareTo(asset.performanceFromOlderToCurrent);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return getId().equals(asset.getId()) &&
                getSymbol().equals(asset.getSymbol()) &&
                getName().equals(asset.getName()) &&
                getCurrentPriceUsd().equals(asset.getCurrentPriceUsd()) &&
                getOlderPriceUsd().equals(asset.getOlderPriceUsd()) &&
                getPerformanceFromOlderToCurrent().equals(asset.getPerformanceFromOlderToCurrent()) &&
                getQuantity().equals(asset.getQuantity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSymbol(), getName(), getCurrentPriceUsd(), getOlderPriceUsd(), getPerformanceFromOlderToCurrent(), getQuantity());
    }
}
