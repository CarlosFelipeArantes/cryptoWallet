package model.dto.coincap_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssetCsvTest {

    private Set<AssetCsv> assetsCsvSet;

    @BeforeEach
    void beforeEach(){

        assetsCsvSet = ConcurrentHashMap.newKeySet();

        assetsCsvSet.addAll(getAssetsCsvSet());
    }

    @Test
    void testEquals() {

        assertEquals(2,assetsCsvSet.size());

        assetsCsvSet.add(
                AssetCsv.builder()
                        .symbol("ETH")
                        .quantity(new BigDecimal("1.61803"))
                        .price(new BigDecimal("10.61803"))
                        .build()
        );

        assertEquals(2,assetsCsvSet.size());

    }

    @Test
    void testHashCode() {

        assertEquals(2,assetsCsvSet.size());

        assetsCsvSet.add(
                AssetCsv.builder()
                        .symbol("ETH")
                        .quantity(new BigDecimal("1.61803"))
                        .price(new BigDecimal("10.61803"))
                        .build()
        );

        assertEquals(2,assetsCsvSet.size());

    }


    public static Set<AssetCsv> getAssetsCsvSet(){

        Set<AssetCsv> assetsCsvSet = ConcurrentHashMap.newKeySet();

        assetsCsvSet.addAll(Arrays.asList(
                AssetCsv.builder()
                        .symbol("ETH")
                        .quantity(new BigDecimal("3.14159"))
                        .price(new BigDecimal("30.14159"))
                        .build(),
                AssetCsv.builder()
                        .symbol("BTC")
                        .quantity(new BigDecimal("1.61803"))
                        .price(new BigDecimal("10.61803"))
                        .build()
        ));

        return assetsCsvSet;
    }
}