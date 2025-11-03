package keyword.framework.KeywordDrivenFramework;

import org.testng.annotations.*;
import java.io.IOException;
import java.util.*;

@Listeners({keyword.framework.KeywordDrivenFramework.TestListener.class})
public class KeywordDrivenTest extends BaseTest {

    private KeywordExecutor keywordExecutor;
    private ExcelDataReader excelDataReader;
    private String userName;
    private Map<String, String> loginData;

    // ✅ Factory constructor: creates a separate instance per user
    @Factory(dataProvider = "LoginData", dataProviderClass = DataProviderUtil.class)
    public KeywordDrivenTest(Map<String, String> testData) {
        this.loginData = testData;
        this.userName = testData.get("UsersName");
    }

    @BeforeClass
    public void setUp() throws IOException {
        System.out.println("🚀 Launching browser for: " + userName);
        initializeDriver(userName);
        openBaseUrl();
        keywordExecutor = new KeywordExecutor(getDriver());
        excelDataReader = new ExcelDataReader();
    }

    @Test(priority = 1)
    public void runLoginTest() throws IOException {
        List<Map<String, String>> steps = excelDataReader.getKeywordSteps("Login");
        keywordExecutor.executeSteps(steps, "Login", loginData);
    }

    @Test(priority = 2, dataProvider = "PurchaseData", dataProviderClass = DataProviderUtil.class)
    public void runPurchaseTest(Map<String, String> testData) throws IOException {
        List<Map<String, String>> steps = excelDataReader.getKeywordSteps("Purchase");
        keywordExecutor.executeSteps(steps, "Purchase", testData);
    }

    @Test(priority = 3, dataProvider = "RemoveData", dataProviderClass = DataProviderUtil.class)
    public void runRemoveTest(Map<String, String> testData) throws IOException {
        List<Map<String, String>> steps = excelDataReader.getKeywordSteps("RemoveProduct");
        keywordExecutor.executeSteps(steps, "RemoveProduct", testData);
    }

    @Test(priority = 4)
    public void runSignoutTest() throws IOException {
        List<Map<String, String>> steps = excelDataReader.getKeywordSteps("SignOut");
        keywordExecutor.executeSteps(steps, "SignOut", new HashMap<>());
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        quitDriver();
    }
}