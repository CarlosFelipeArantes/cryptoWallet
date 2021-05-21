package model.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static config.AppConstants.MAX_THREADS;
import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {

    private Wallet wallet;
    private Set<Asset> assetsSet;
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);


    @BeforeEach
    void beforeEach(){

        wallet = new Wallet();

        assetsSet = getAssetsForTesting();

    }

    @Test
    void addAsset() throws ExecutionException, InterruptedException {

        executor.submit(()->assetsSet.parallelStream().forEach(asset -> wallet.addAsset(asset))).get();

        testToString(wallet,"total=74900.00, best_asset=Litecoin, best_performance=300.00%, worst_asset=Ethereum, worst_performance=20.00%");

    }

    @Test
    void calculateBestAsset() throws ExecutionException, InterruptedException {

        addAsset();

        executor.submit(()->
                wallet.addAsset(Asset.builder()
                        .id("newcoin")
                        .symbol("NCN")
                        .name("Newcoin")
                        .currentPriceUsd(
                                new BigDecimal("500.0000")
                        )
                        .olderPriceUsd(
                                new BigDecimal("50.0000")
                        )
                        .performanceFromOlderToCurrent(
                                new BigDecimal("10")
                        )
                        .quantity(
                                new BigDecimal("1")
                        )
                        .build())
        ).get();

        testToString(wallet,"total=75400.00, best_asset=Newcoin, best_performance=900.00%, worst_asset=Ethereum, worst_performance=20.00%");

    }

    @Test
    void calculateWorstAsset() throws ExecutionException, InterruptedException {

        addAsset();

        executor.submit(()->
                wallet.addAsset(Asset.builder()
                        .id("newcoin")
                        .symbol("NCN")
                        .name("Newcoin")
                        .currentPriceUsd(
                                new BigDecimal("500.0000")
                        )
                        .olderPriceUsd(
                                new BigDecimal("500.0000")
                        )
                        .performanceFromOlderToCurrent(
                                new BigDecimal("1.0000")
                        )
                        .quantity(
                                new BigDecimal("1")
                        )
                        .build())
        ).get();

        testToString(wallet,"total=75400.00, best_asset=Litecoin, best_performance=300.00%, worst_asset=Newcoin, worst_performance=0.00%");

        executor.submit(()->
                wallet.addAsset(Asset.builder()
                        .id("evennewercoin")
                        .symbol("ENC")
                        .name("EvenNewerCoin")
                        .currentPriceUsd(
                                new BigDecimal("400.0000")
                        )
                        .olderPriceUsd(
                                new BigDecimal("500.0000")
                        )
                        .performanceFromOlderToCurrent(
                                new BigDecimal("0.8")
                        )
                        .quantity(
                                new BigDecimal("1")
                        )
                        .build())
        ).get();

        testToString(wallet,"total=75800.00, best_asset=Litecoin, best_performance=300.00%, worst_asset=EvenNewerCoin, worst_performance=-20.00%");

    }


    public static void testToString(Wallet wallet, String expected) {

        assertEquals(expected,wallet.toString());

    }

    public static Set<Asset> getAssetsForTesting(){
        Set<Asset> assetsSet = ConcurrentHashMap.newKeySet();

        assetsSet.addAll(
                Arrays.asList(
                        Asset.builder()
                                .id("bitcoin")
                                .symbol("BTC")
                                .name("Bitcoin")
                                .currentPriceUsd(
                                        new BigDecimal("30000.0000")
                                )
                                .olderPriceUsd(
                                        new BigDecimal("20000.0000")
                                )
                                .performanceFromOlderToCurrent(
                                        new BigDecimal("1.5")
                                )
                                .quantity(
                                        new BigDecimal("2.34")
                                )
                                .build(),
                        Asset.builder()
                                .id("ethereum")
                                .symbol("ETH")
                                .name("Ethereum")
                                .currentPriceUsd(
                                        new BigDecimal("3000.0000")
                                )
                                .olderPriceUsd(
                                        new BigDecimal("2500.0000")
                                )
                                .performanceFromOlderToCurrent(
                                        new BigDecimal("1.2")
                                )
                                .quantity(
                                        new BigDecimal("1.5")
                                )
                                .build(),
                        Asset.builder()
                                .id("litecoin")
                                .symbol("LTC")
                                .name("Litecoin")
                                .currentPriceUsd(
                                        new BigDecimal("200.0000")
                                )
                                .olderPriceUsd(
                                        new BigDecimal("50.0000")
                                )
                                .performanceFromOlderToCurrent(
                                        new BigDecimal("4")
                                )
                                .quantity(
                                        new BigDecimal("1")
                                )
                                .build()
                )
        );

        return assetsSet;
    }

    @Test
    void getPerformanceString() {

        String firstExpected = "0.00";
        String firstActual = Wallet.getPerformanceString(BigDecimal.ZERO);

        String secondExpected = "0.00";
        String secondActual = Wallet.getPerformanceString(new BigDecimal("1.000005"));

        String thirdExpected = "0.01";
        String thirdActual = Wallet.getPerformanceString(new BigDecimal("1.00005"));

        String fourthExpected = "0.50";
        String fourthActual = Wallet.getPerformanceString(new BigDecimal("1.005"));

        assertAll(
                ()->assertEquals(firstExpected,firstActual),
                ()->assertEquals(secondExpected,secondActual),
                ()->assertEquals(thirdExpected,thirdActual),
                ()->assertEquals(fourthExpected,fourthActual)
        );
    }
}