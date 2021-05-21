package config;

public class AppConstants {

    private static final String COINCAP_API_BASE_URI = "https://api.coincap.io";

    private static final String COINCAP_API_ASSET_BASE_URI = COINCAP_API_BASE_URI.concat("/v2/assets");

    public static final String COINCAP_API_ASSET_URI = COINCAP_API_ASSET_BASE_URI.concat("?search=%s&limit=1");

    public static final String COINCAP_API_HISTORY_URI = COINCAP_API_ASSET_BASE_URI.concat("/%s/history?interval=d1&start=1617753600000&end=1617753601000");

    public static final String CSV_INPUT_FILE_NAME = "input.csv";

    public static final int MAX_THREADS = 3;

    public static final String REQUEST_LOG = "Submitted request %s";

}
