package keyword.framework.runner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import keyword.framework.core.BaseTest;
import keyword.framework.core.ExcelDataReader;
import keyword.framework.core.ExcelWriter;
import keyword.framework.core.FrameworkPaths;
import keyword.framework.core.KeywordExecutor;
import keyword.framework.core.TestCaseQueue;
import keyword.framework.dataproviders.DataProviderUtil;

@Listeners({ keyword.framework.listeners.TestListener.class })
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
		List<Map<String, String>> rows = excel.getTestData("Login");

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

	@Test(priority = 1)
	public void loginOnce() throws IOException {

		System.out.println("🔑 [" + userName + "] Performing one-time login...");

		initializeDriver(userName);
		openBaseUrl();

		executor = new KeywordExecutor(getDriver());
		List<Map<String, String>> steps = excel.getTestSteps("Login");
		executor.executeSteps(steps, "Login", loginData);

		System.out.println("✔ Login completed for user: " + userName);

		quitDriver();
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

	@SuppressWarnings("unchecked")
	private void executeTestCase(String module, Map<String, Object> testCase) throws Exception {

		String tcId = (String) testCase.get("TestCaseID");
		List<String> products = (List<String>) testCase.get("Products");
		double expectedTotal = (double) testCase.get("ExpectedTotal");

		System.out.println("\n🚀 [" + userName + "] Running " + module + " TestCase: " + tcId);
		System.out.println("⏭️ Products: " + products);
		System.out.println("Expected Total: " + expectedTotal);

		// 🔹 Open browser with saved login
		initializeDriver(userName);
		openBaseUrl();

		executor = new KeywordExecutor(getDriver());

		// Load keyword steps for 3 modules
		List<Map<String, String>> purchaseSteps = excel.getTestSteps("Purchase");
		List<Map<String, String>> cartCleanupSteps = excel.getTestSteps("CartCleanup");
		List<Map<String, String>> cartTotalSteps = excel.getTestSteps("CartTotal");

		// 1️⃣ PRE: clear the cart
		executor.executeSteps(cartCleanupSteps, "CartCleanup", Collections.emptyMap());

		// 2️⃣ MAIN: execute Purchase steps once per product
		for (String product : products) {
			Map<String, String> data = Map.of("Product", product);
			System.out.println("➡ [" + userName + "][TC:" + tcId + "] Product: " + product);
			executor.executeSteps(purchaseSteps, "Purchase", data);
			//double productPrice = executor.getLastExtractedPrice();
		}

		// 3️⃣ POST: go to cart & read total
		executor.executeSteps(cartTotalSteps, "CartTotal", Collections.emptyMap());
		double cartTotal = executor.getLastExtractedPrice();
		System.out.println("🧮 Expected total: " + expectedTotal + " | Cart total (actual): " + cartTotal);
		boolean isMatch = Math.round(cartTotal) == Math.round(expectedTotal);
		ExcelWriter.updateStatus(FrameworkPaths.TESTDATA_PATH, "Purchase", tcId, cartTotal, isMatch ? "PASS" : "FAIL");
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

	@AfterSuite(alwaysRun = true)
	public void cleanupProfiles() {
		try {
			Path baseProfiles = Paths.get(System.getProperty("user.dir"), "tempProfiles");
			if (Files.exists(baseProfiles)) {
				Files.walk(baseProfiles).sorted(Comparator.reverseOrder()).map(Path::toFile)
						.forEach(java.io.File::delete);

				System.out.println("✔ Deleted all temporary browser profiles.");
			} else {
				System.out.println("No tempProfiles folder found.");
			}
		} catch (Exception e) {
			System.out.println("Cleanup failed: " + e.getMessage());
		}
	}
}
