package keyword.framework.KeywordDrivenFramework;

import org.testng.annotations.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class KeywordDrivenTest extends BaseTest {

    private KeywordExecutor keywordExecutor;
    private KeywordDataReader keywordReader;

    @BeforeClass
    public void setUp() throws IOException {
        initializeDriver();
        openBaseUrl();

        keywordExecutor = new KeywordExecutor(getDriver());
        keywordReader = new KeywordDataReader();
    }

    @Test(dataProvider = "LoginData", dataProviderClass = DataProviderUtil.class, priority = 1)
    public void runLoginTest(Map<String, String> testData) throws IOException {
        List<Map<String, String>> steps = keywordReader.getKeywordSteps("Login");
        keywordExecutor.executeSteps(steps, "Login", testData);
    }

    @Test(dataProvider = "PurchaseData", dataProviderClass = DataProviderUtil.class, priority = 2)
    public void runPurchaseTest(Map<String, String> testData) throws IOException {
        List<Map<String, String>> steps = keywordReader.getKeywordSteps("Purchase");
        keywordExecutor.executeSteps(steps, "Purchase", testData);
    }

    @AfterClass
    public void tearDown() {
        quitDriver();
    }
}
