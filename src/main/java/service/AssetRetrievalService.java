package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.AssetApiSearch;
import model.dto.AssetHistoryApiSearch;
import model.dto.coincap_api.PriceAtGivenTime;
import model.dto.coincap_api.AssetCoincapDTO;
import model.dto.coincap_api.AssetCsv;
import model.internal.Asset;
import model.internal.Wallet;
import model.mapper.AssetMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static config.AppConstants.*;

public class AssetRetrievalService {

    private static final Logger logger = LogManager.getLogger(AssetRetrievalService.class);

    private final ObjectMapper objectMapper;

    public AssetRetrievalService(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public Wallet processAllAssets(Set<AssetCsv> assetsFromCsv, ThreadPoolExecutor executor) throws IOException {

        Wallet wallet = retrieveAllAssetsInformationFromApi(executor,assetsFromCsv);

        return wallet;
    }

    private Wallet retrieveAllAssetsInformationFromApi(ThreadPoolExecutor executor, Set<AssetCsv> assetsFromCsv) throws IOException {

        Wallet wallet = new Wallet();

        if(assetsFromCsv.size()>1){

            assetsFromCsv.parallelStream().forEach(assetCsv->{

                try {
                    executor.submit(()-> getAssetsFromApi(assetCsv, wallet)).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

        }else {

            AssetCsv assetCsv = assetsFromCsv.stream().findFirst().get();

            logger.info(String.format(REQUEST_LOG,assetCsv.getSymbol()));

            getAssetFromApi(assetCsv);

        }
        return wallet;
    }

    private void getAssetsFromApi(AssetCsv assetCsv, Wallet wallet) {

        Thread currentThread = Thread.currentThread();

        logger.info(String.format(REQUEST_LOG,assetCsv.getSymbol()).concat(" Thread " + currentThread.getName()));

        try {

            Asset asset = getAssetFromApi(assetCsv);

            wallet.addAsset(asset);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Asset getAssetFromApi(AssetCsv assetCsv) throws IOException {

        AssetCoincapDTO assetCoincapDTO = objectMapper.readValue(
                new URL(String.format(COINCAP_API_ASSET_URI,assetCsv.getSymbol())),
                AssetApiSearch.class
        ).getData().get(0);

        PriceAtGivenTime priceAtGivenTime = objectMapper.readValue(
                new URL(String.format(COINCAP_API_HISTORY_URI, assetCoincapDTO.getId())),
                AssetHistoryApiSearch.class
        ).getData().get(0);

        return AssetMapper.assetFromDTO(assetCoincapDTO,priceAtGivenTime,assetCsv);
    }
}
