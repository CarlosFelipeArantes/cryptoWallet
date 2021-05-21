package model.mapper;

import model.dto.coincap_api.AssetCsv;
import model.dto.coincap_api.PriceAtGivenTime;
import model.dto.coincap_api.AssetCoincapDTO;
import model.internal.Asset;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AssetMapper{

    public static Asset assetFromDTO(AssetCoincapDTO assetCoincapDTO, PriceAtGivenTime priceAtGivenTime, AssetCsv assetCsv){

        BigDecimal currentPriceUsd = priceAtGivenTime.getPriceUsd();
        BigDecimal olderPriceUsd = assetCsv.getPrice();

        return Asset.builder()
                .id(assetCoincapDTO.getId())
                .symbol(assetCoincapDTO.getSymbol())
                .name(assetCoincapDTO.getName())
                .currentPriceUsd(currentPriceUsd)
                .olderPriceUsd(olderPriceUsd)
                .performanceFromOlderToCurrent(
                        currentPriceUsd.divide(olderPriceUsd,5, RoundingMode.HALF_UP)
                )
                .quantity(assetCsv.getQuantity())
                .build();
    }



}
