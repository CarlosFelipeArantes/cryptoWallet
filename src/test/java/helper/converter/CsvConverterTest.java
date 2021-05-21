package helper.converter;

import model.dto.coincap_api.AssetCsv;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import static config.AppConstants.MAX_THREADS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvConverterTest {

    private ThreadPoolExecutor executor;

    @Mock
    private CSVParser csvParser;

    @Mock
    private CSVRecord firstRecord, secondRecord;

    private final List<CSVRecord> csvRecordList = new ArrayList<>();

    @BeforeEach
    void beforeEach(){

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);

        setCSVParserForTesting();

        setRecordsForTesting();

    }

    @Test
    void readCryptoAssets() throws ExecutionException, InterruptedException {

        Set<AssetCsv> csvAssetsSetConcurrent = ConcurrentHashMap.newKeySet();

        CsvConverter.iterateOverCsvData(executor,csvParser,csvAssetsSetConcurrent);

        assertDoesNotThrow(()->CsvConverter.iterateOverCsvData(executor,csvParser,csvAssetsSetConcurrent));

        csvAssetsSetConcurrent.parallelStream().forEach(this::verifyAssetsSet);

    }

    private void verifyAssetsSet(AssetCsv assetCsv) {

        AtomicBoolean listContainsEntity = new AtomicBoolean(false);

        csvRecordList.parallelStream().forEach(csvRecord->{

                    if(csvRecord.get("symbol").equals(assetCsv.getSymbol())){

                        assertAll(

                                ()-> assertEquals(
                                        new BigDecimal(
                                                csvRecord.get("quantity")
                                        ),
                                        assetCsv.getQuantity()
                                ),
                                ()-> assertEquals(
                                        new BigDecimal(
                                                csvRecord.get("price")
                                        )
                                        ,assetCsv.getPrice()
                                )

                        );

                        listContainsEntity.set(true);

                    }
        });

        assertTrue(listContainsEntity.get());

    }

    private void setCSVParserForTesting() {

        when(csvParser.spliterator()).thenReturn(csvRecordList.spliterator());

    }

    private void setRecordsForTesting() {

        when(firstRecord.get("symbol")).thenReturn("BTC");
        when(firstRecord.get("quantity")).thenReturn("0.12345");
        when(firstRecord.get("price")).thenReturn("37870.5058");

        when(secondRecord.get("symbol")).thenReturn("ETH");
        when(secondRecord.get("quantity")).thenReturn("4.89532");
        when(secondRecord.get("price")).thenReturn("2004.9774");

        csvRecordList.addAll(
                Arrays.asList(
                        firstRecord,
                        secondRecord
                )
        );
    }
}