package keyword.framework.KeywordDrivenFramework;

import org.testng.annotations.*;
import java.io.IOException;
import java.util.*;

@Listeners({ keyword.framework.KeywordDrivenFramework.TestListener.class })
public class KeywordDrivenTest extends BaseTest {

	private KeywordExecutor executor;
	private ExcelDataReader excel;
	private String userName;
	private Map<String, String> loginData;

	// ASSIGN USER (FROM XML)
	@BeforeClass
	@Parameters("UserName")
	public void setUser(String user) throws IOException {
		this.userName = user;
		System.out.println("♟️ Runner assigned to user: " + userName);

		excel = new ExcelDataReader();
		List<Map<String, String>> rows = excel.getTestDataRows("Login");

		for (Map<String, String> row : rows) {
			if (row.get("UsersName").equalsIgnoreCase(userName)) {
				loginData = row;
				break;
			}
		}

		if (loginData == null) {
			throw new RuntimeException("Login data not found for user: " + userName);
		}
	}

	// LOGIN ONCE PER USER → PROFILE SAVES SESSION
	@Test(priority = 1)
	public void loginOnce() throws IOException {

		System.out.println("🔑 [" + userName + "] Performing one-time login...");

		initializeDriver(userName);
		openBaseUrl();

		executor = new KeywordExecutor(getDriver());
		List<Map<String, String>> steps = excel.getKeywordSteps("Login");
		executor.executeSteps(steps, "Login", loginData);
		
		System.out.println("✔ Login completed for user: " + userName);

		quitDriver(); // keep Chrome profile with session
	}

	// PURCHASE EXECUTION USING SHARED QUEUE
	@Test(priority = 2, dataProvider = "PurchaseGroupData", dataProviderClass = DataProviderUtil.class)
	public void runPurchases(String dummy) throws Exception {

        while (true) {

            Map<String, Object> testCase = TestCaseQueue.getNextPurchaseCase();
            if (testCase == null) {
                System.out.println("🛑 [" + userName + "] No more Purchase test cases.");
                break;
            }

            executeTestCase("Purchase", testCase);
        }
    }

	 // REMOVEPRODUCT EXECUTION USING SHARED QUEUE
    /*@Test(priority = 3, dataProvider = "RemoveGroupData", dataProviderClass = DataProviderUtil.class)
    public void runRemoveCases(String dummy) throws Exception {

        while (true) {

            Map<String, Object> testCase = TestCaseQueue.getNextRemoveCase();
            if (testCase == null) {
                System.out.println("🛑 [" + userName + "] No more RemoveProduct test cases.");
                break;
            }

            executeTestCase("RemoveProduct", testCase);
        }
    }*/

    // COMMON EXECUTION METHOD FOR ANY MODULE
    private void executeTestCase(String module, Map<String, Object> testCase) throws Exception {

        String tcId = (String) testCase.get("TestCaseID");
        @SuppressWarnings("unchecked")
        List<String> products = (List<String>) testCase.get("Products");

        System.out.println("\n🚀 [" + userName + "] Running " + module + " TestCase: " + tcId);

        // Open browser with saved login
        initializeDriver(userName);
        openBaseUrl();

        executor = new KeywordExecutor(getDriver());
        List<Map<String, String>> steps = excel.getKeywordSteps(module);

        for (String product : products) {
            Map<String, String> data = Map.of("Product", product);
            System.out.println("➡ [" + userName + "][TC:" + tcId + "] Product: " + product);
            executor.executeSteps(steps, module, data);
        }

        System.out.println("✔ Completed TC " + tcId + " by user " + userName);

        quitDriver(); // close browser after each test case
    }

	@AfterMethod(alwaysRun = true)
	public void closeBrowserAfterTC() {
		try {
			quitDriver();
			System.out.println("🧹 [" + userName + "] Browser closed after test case.");
		} catch (Exception e) {
			System.out.println("⚠️ [" + userName + "] Error closing browser: " + e.getMessage());
		}
	}
}