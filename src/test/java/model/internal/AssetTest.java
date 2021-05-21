package model.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    private Asset firstAsset, secondAsset, thirdAsset;


    @BeforeEach
    void beforeEach(){

        firstAsset = Asset.builder()
                .performanceFromOlderToCurrent(BigDecimal.TEN)
                .build();

        secondAsset = Asset.builder()
                .performanceFromOlderToCurrent(BigDecimal.ONE)
                .build();

        thirdAsset = Asset.builder()
                .performanceFromOlderToCurrent(BigDecimal.ONE)
                .build();
    }

    @Test
    void compareTo() {

        assertAll(
                ()-> assertTrue(firstAsset.compareTo(secondAsset)>0),
                ()-> assertTrue(secondAsset.compareTo(firstAsset)<0),
                ()-> assertTrue(firstAsset.compareTo(secondAsset)>0),
                ()-> assertTrue(secondAsset.compareTo(thirdAsset)==0)
        );

    }
}