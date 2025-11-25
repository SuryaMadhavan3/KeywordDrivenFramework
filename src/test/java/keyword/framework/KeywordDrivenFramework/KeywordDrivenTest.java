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

	@BeforeClass
	@Parameters("UserName")
	public void setUser(String user) throws IOException {
		this.userName = user;
		System.out.println("♟️ Runner assigned to user: " + userName);

		excel = new ExcelDataReader();
		List<Map<String, String>> rows = excel.getTestDataRows("Login");
		System.out.println(rows);
		for (Map<String, String> row : rows) {
			System.out.println(row);
			if (row.get("UsersName").equalsIgnoreCase(userName)) {
				loginData = row;
				break;
			}
		}

		if (loginData == null) {
			throw new RuntimeException("Login data not found for user: " + userName);
		}
	}

	@Test(priority = 1)
	public void loginOnce() throws IOException {

		System.out.println("🔑 [" + userName + "] Performing one-time login...");

		initializeDriver(userName);
		openBaseUrl();

		executor = new KeywordExecutor(getDriver());
		List<Map<String, String>> steps = excel.getKeywordSteps("Login");
		executor.executeSteps(steps, "Login", loginData);

		System.out.println("✔ Login completed for user: " + userName);

		// quitDriver();
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

	// COMMON EXECUTION METHOD FOR ANY MODULE
	@SuppressWarnings("unchecked")
	private void executeTestCase(String module, Map<String, Object> testCase) throws Exception {

		String tcId = (String) testCase.get("TestCaseID");
		List<String> products = (List<String>) testCase.get("Products");
		List<Double> expectedPrices = (List<Double>) testCase.get("ExpectedPrices");

		System.out.println("TestCase : " + tcId);
		System.out.println("Products : " + products);
		System.out.println("Products Expected Price : " + expectedPrices);

		System.out.println("\n🚀 [" + userName + "] Running " + module + " TestCase: " + tcId);

		// Open browser with saved login
		initializeDriver(userName);
		openBaseUrl();

		executor = new KeywordExecutor(getDriver());
		List<Map<String, String>> steps = excel.getKeywordSteps(module);

		for (int i = 0; i < products.size(); i++) {

			String product = products.get(i);
			double expectedPrice = expectedPrices.get(i);

			Map<String, String> data = Map.of("Product", product);

			System.out.println("🔍 [" + tcId + "] Checking Product " + (i + 1) + ": " + product);

			// 🔹 Single call – Excel drives ALL Purchase steps
			executor.executeSteps(steps, module, data);

			// 🔹 Read price captured by getprice action
			double actualPrice = executor.getLastExtractedPrice();
			System.out.println("💰 Expected: " + expectedPrice + " | Actual: " + actualPrice);

			// 🔹 Compare price for this product
			if (actualPrice != expectedPrice) {
				System.out.println("❌ Price mismatch for product: " + product + ". Failing test case: " + tcId);
				ExcelWriter.updateStatus(tcId, "Fail");
				quitDriver();
				return; // stop this test case immediately
			}
		}

		quitDriver();
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