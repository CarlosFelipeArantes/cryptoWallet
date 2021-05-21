package model.mapper;

import model.dto.coincap_api.AssetCoincapDTO;
import model.dto.coincap_api.AssetCsv;
import model.dto.coincap_api.PriceAtGivenTime;
import model.internal.Asset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssetMapperTest {

    private AssetCoincapDTO assetCoincapDTO;
    private PriceAtGivenTime priceAtGivenTime;
    private AssetCsv assetCsv;

    @BeforeEach
    void beforeEach(){

        assetCoincapDTO = AssetCoincapDTO.builder()
                .id("ethereum")
                .symbol("ETH")
                .name("Ethereum")
                .build();

        priceAtGivenTime = PriceAtGivenTime.builder()
                .priceUsd(new BigDecimal("2459.0150840852424385"))
                .build();

        assetCsv = AssetCsv.builder()
                .symbol("ETH")
                .price(new BigDecimal("2459.0150840852424385"))
                .quantity(new BigDecimal("3.43817509"))
                .build();
    }


    @Test
    void assetFromDTO() {

        Asset asset = AssetMapper.assetFromDTO(assetCoincapDTO,priceAtGivenTime,assetCsv);

        BigDecimal performance = priceAtGivenTime.getPriceUsd().divide(assetCsv.getPrice(),5, RoundingMode.HALF_UP);

        Assertions.assertAll(

                ()-> assertEquals(assetCoincapDTO.getSymbol(),assetCsv.getSymbol()),
                ()-> assertEquals(assetCoincapDTO.getSymbol(),asset.getSymbol()),
                ()-> assertEquals(assetCoincapDTO.getId(),asset.getId()),
                ()-> assertEquals(assetCoincapDTO.getName(), asset.getName()),
                ()-> assertEquals(assetCsv.getPrice().toString(),asset.getOlderPriceUsd().toString()),
                ()-> assertEquals(priceAtGivenTime.getPriceUsd(),asset.getCurrentPriceUsd()),
                ()-> assertEquals(performance,asset.getPerformanceFromOlderToCurrent())

        );
    }
}