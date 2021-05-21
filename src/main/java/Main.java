import com.fasterxml.jackson.databind.ObjectMapper;
import helper.converter.CsvConverter;
import model.dto.coincap_api.AssetCsv;
import model.internal.Wallet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.AssetRetrievalService;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static config.AppConstants.MAX_THREADS;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final AssetRetrievalService assetRetrievalService = new AssetRetrievalService(objectMapper);

    public static void main (String [] args) throws Exception {

        objectMapper.findAndRegisterModules();

        logger.info("PROGRAM STARTED!");

        Set<AssetCsv> assetsFromCsv = CsvConverter.readCryptoAssets(executor);

        Wallet wallet = assetRetrievalService.processAllAssets(assetsFromCsv,executor);

        logger.info(wallet.toString());

        executor.shutdown();

    }

}
