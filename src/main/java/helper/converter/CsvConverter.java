package helper.converter;

import model.dto.coincap_api.AssetCsv;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static config.AppConstants.CSV_INPUT_FILE_NAME;

public class CsvConverter{

    private static final Logger logger = LogManager.getLogger(CsvConverter.class);

    private static InputStreamReader newReader(final InputStream inputStream) {
        return new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);
    }

    public static Set<AssetCsv> readCryptoAssets(ThreadPoolExecutor executor) throws Exception {

        InputStream is = CsvConverter.class.getClassLoader().getResourceAsStream(CSV_INPUT_FILE_NAME);

        Reader reader = newReader(is);

        CSVParser csvParser = new CSVParser(
                reader,
                CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim()
        );

        Set<AssetCsv> csvAssetsSet = ConcurrentHashMap.newKeySet();

        iterateOverCsvData(executor, csvParser, csvAssetsSet);

        logger.info("ASSETS RETRIEVED FROM CSV INPUT FILE!");

        return csvAssetsSet;
    }

    protected static void iterateOverCsvData(ThreadPoolExecutor executor, CSVParser csvParser, Set<AssetCsv> csvAssetsSet) throws ExecutionException, InterruptedException {

        Stream <CSVRecord> stream = StreamSupport.stream(csvParser.spliterator(), true);

        executor.submit(
                ()-> stream.forEach(
                        CSVRecord -> {
                            AssetCsv newerAssetCsv = AssetCsv.builder()
                                    .symbol(
                                            CSVRecord.get("symbol")
                                    )
                                    .quantity(
                                            new BigDecimal(CSVRecord.get("quantity"))
                                    )
                                    .price(
                                            new BigDecimal(CSVRecord.get("price"))
                                    )
                                    .build();
                            csvAssetsSet.add(newerAssetCsv);
                        })
        ).get();
    }

}