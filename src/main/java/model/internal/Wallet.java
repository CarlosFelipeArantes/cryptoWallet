package model.internal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Wallet {

    private final Set<Asset> assets;
    private BigDecimal total;
    private Asset bestAsset, worstAsset;
    private BigDecimal bestPerformance, worstPerformance;

    public Wallet() {
        this.assets = ConcurrentHashMap.newKeySet();
        this.total = BigDecimal.ZERO;
    }

    public void addAsset(Asset asset){

        this.assets.add(asset);

        synchronized (this){
            this.total = this.total.add(
                    asset
                            .getQuantity()
                            .multiply(
                                    asset.getCurrentPriceUsd()
                            ));
        }
        this.calculateBestAsset(asset);
        this.calculateWorstAsset(asset);
    }

    private synchronized Asset getBestAsset() {
        return bestAsset;
    }

    public synchronized void calculateBestAsset(Asset asset) {

        if(Objects.isNull(this.bestAsset)) {

            this.bestAsset = asset;
            this.bestPerformance = asset.getPerformanceFromOlderToCurrent();

        }else{

            if(this.getBestAsset().compareTo(asset)<0){

                this.bestAsset = asset;
                this.bestPerformance = asset.getPerformanceFromOlderToCurrent();
            }
        }
    }

    private synchronized Asset getWorstAsset() {
        return worstAsset;
    }

    public synchronized void calculateWorstAsset(Asset asset) {

        if(Objects.isNull(this.worstAsset)) {

            this.worstAsset = asset;
            this.worstPerformance = asset.getPerformanceFromOlderToCurrent();

        }else{

            if(this.getWorstAsset().compareTo(asset)>0){

                this.worstAsset = asset;
                this.worstPerformance = asset.getPerformanceFromOlderToCurrent();

            }
        }
    }

    @Override
    public String toString() {

        String bestPerformance = getPerformanceString(this.bestPerformance);

        String worstPerformance = getPerformanceString(this.worstPerformance);

        return MessageFormat.format(
                "total={0}, best_asset={1}, best_performance={2}%, worst_asset={3}, worst_performance={4}%",
                this.total.setScale(2, RoundingMode.HALF_UP).toString(), this.bestAsset.toString(), bestPerformance, this.worstAsset.toString(), worstPerformance
                );
    }

    public static String getPerformanceString(BigDecimal p) {
        if(p.equals(BigDecimal.ZERO))
            return BigDecimal.ZERO
                    .setScale(2,RoundingMode.HALF_UP)
                    .toString();
        else
            return p
                    .subtract(BigDecimal.ONE)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .toString();
    }

    public Set<Asset> getAssets(){
        return assets;
    }
}
