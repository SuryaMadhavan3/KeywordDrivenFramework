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

		//quitDriver();
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
        double expectedTotal = (double) testCase.get("ExpectedTotal");
        System.out.println("TestCase : "+tcId);
        System.out.println("Products : "+products);
        System.out.println("Products Expected Price : "+expectedPrices);
        System.out.println("Overall Total : "+expectedTotal);
        
        System.out.println("\n🚀 [" + userName + "] Running " + module + " TestCase: " + tcId);

        // Open browser with saved login
        initializeDriver(userName);
        openBaseUrl();

        executor = new KeywordExecutor(getDriver());
        List<Map<String, String>> steps = excel.getKeywordSteps(module);

        double actualTotal = 0.0;

        for (int i = 0; i < products.size(); i++) {

            String product = products.get(i);
            double expectedPrice = expectedPrices.get(i);

            Map<String, String> data = Map.of("Product", product);

            System.out.println("🔍 Checking Product: " + product);

            // 1️⃣ First half of steps (search/click/switch)
            for (Map<String, String> step : steps) {

                if (!step.get("TestCaseName").equalsIgnoreCase("Purchase"))
                    continue;

                String action = step.get("Action");

                if (action.equalsIgnoreCase("scrollandclick")) break;

                executor.executeKeyword(
                    action,
                    step.get("LocatorType"),
                    step.get("LocatorValue"),
                    data.getOrDefault(step.get("TestData"), null)
                );
            }

            // 2️⃣ Extract actual price
            double actualPrice = executor.getLastExtractedPrice();
            System.out.println("💰 Expected: " + expectedPrice + " | Actual: " + actualPrice);

            // 3️⃣ Compare
            if (actualPrice != expectedPrice) {
                System.out.println("❌ Price mismatch! Failing immediately.");

                ExcelWriter.updateStatus(tcId, actualPrice, "FAIL");
                quitDriver();
                return;
            }

            actualTotal += actualPrice;

            // 4️⃣ Execute remaining Purchase steps (add to cart)
            boolean start = false;
            for (Map<String, String> step : steps) {

                if (!step.get("TestCaseName").equalsIgnoreCase("Purchase"))
                    continue;

                if (step.get("Action").equalsIgnoreCase("scrollandclick")) start = true;
                if (!start) continue;

                executor.executeKeyword(
                    step.get("Action"),
                    step.get("LocatorType"),
                    step.get("LocatorValue"),
                    data.getOrDefault(step.get("TestData"), null)
                );
            }

        }

        // 5️⃣ After all products: total validation
        boolean isMatch = (actualTotal == expectedTotal);

        ExcelWriter.updateStatus(tcId, actualTotal, isMatch ? "PASS" : "FAIL");

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