package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.AssetApiSearch;
import model.dto.AssetHistoryApiSearch;
import model.dto.coincap_api.AssetCoincapDTO;
import model.dto.coincap_api.AssetCsv;
import model.dto.coincap_api.PriceAtGivenTime;
import model.internal.Asset;
import model.internal.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

import static config.AppConstants.*;
import static model.dto.coincap_api.AssetCsvTest.getAssetsCsvSet;
import static model.internal.WalletTest.testToString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetRetrievalServiceTest {

    private AssetRetrievalService service;
    private Set<AssetCsv> assetsCsvSet;
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() throws IOException {

        assetsCsvSet = getAssetsCsvSet();

        for (AssetCsv assetCsv : assetsCsvSet) {

            URL firstUrl = new URL(String.format(COINCAP_API_ASSET_URI,assetCsv.getSymbol()));

            AssetCoincapDTO assetCoincapDTO = AssetCoincapDTO.builder()
                    .id(assetCsv.getSymbol().toLowerCase())
                    .name(assetCsv.getSymbol().toLowerCase())
                    .symbol(assetCsv.getSymbol())
                    .priceUsd("30.14")
                    .build();

            when(objectMapper.readValue(firstUrl, AssetApiSearch.class))
                    .thenReturn(
                            AssetApiSearch.builder()
                                    .data(Arrays.asList(assetCoincapDTO)
                                    ).build()
                    );

            URL secondUrl = new URL(String.format(COINCAP_API_HISTORY_URI,assetCoincapDTO.getId()));

            when(objectMapper.readValue(secondUrl, AssetHistoryApiSearch.class))
                    .thenReturn(
                            AssetHistoryApiSearch.builder()
                                    .data(Arrays.asList(
                                            PriceAtGivenTime.builder()
                                                    .priceUsd(new BigDecimal("314.159"))
                                                    .build()
                                            )
                                    ).build()
                    );
        }
        service = new AssetRetrievalService(objectMapper);
    }

    @Test
    void processAllAssets() throws IOException {

        Wallet wallet = service.processAllAssets(assetsCsvSet,executor);

        List<Asset> assetList = new ArrayList<>(wallet.getAssets());

        AtomicReference<Asset> worstAsset= new AtomicReference<>();
        AtomicReference<Asset> bestAsset= new AtomicReference<>();

        AtomicReference<BigDecimal> total = new AtomicReference<>();

        total.set(BigDecimal.ZERO);

        assetList.forEach(asset -> {

            if(Objects.isNull(worstAsset.get()) && Objects.isNull(bestAsset.get())){

                worstAsset.set(asset);
                bestAsset.set(asset);

            }else{

                if(asset.compareTo(bestAsset.get()) > 0){

                    bestAsset.set(asset);

                }else if(asset.compareTo(worstAsset.get()) < 0) {

                    worstAsset.set(asset);

                }

            }

            total.set(total.get().add(asset.getQuantity().multiply(asset.getCurrentPriceUsd())));

        });

        String expected = MessageFormat.format(
                "total={0}, best_asset={1}, best_performance={2}%, worst_asset={3}, worst_performance={4}%",
                total.get().setScale(2, RoundingMode.HALF_UP).toString(),
                bestAsset.get().toString(),
                Wallet.getPerformanceString(bestAsset.get().getPerformanceFromOlderToCurrent()),
                worstAsset.get().toString(),
                Wallet.getPerformanceString(worstAsset.get().getPerformanceFromOlderToCurrent())
        );

        testToString(wallet,expected);

    }
}