package keyword.framework.KeywordDrivenFramework;

public class FrameworkPaths {

    // ✅ Base folder for all test data files
    public static final String BASE_TESTDATA_DIR =
            "C:/Users/MaliniR/Documents/testdata/";

    // ✅ Excel file paths
    public static final String CONFIG_PATH =
            BASE_TESTDATA_DIR + "ConfigData.xlsx";

    public static final String TESTDATA_PATH =
            BASE_TESTDATA_DIR + "TestDataAMZ.xlsx";

    public static final String KEYWORD_PATH =
            BASE_TESTDATA_DIR + "AmazonKeyData.xlsx";

    // ✅ Optional (if you want reports or drivers later)
    public static final String REPORTS_PATH =
            System.getProperty("user.dir") + "/reports/";

    public static final String DRIVERS_PATH =
            System.getProperty("user.dir") + "/drivers/";
}