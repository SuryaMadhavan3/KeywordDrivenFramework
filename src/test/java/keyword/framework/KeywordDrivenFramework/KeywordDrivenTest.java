package keyword.framework.KeywordDrivenFramework;

import org.testng.annotations.*;
import java.io.IOException;
import java.util.*;

@Listeners({ keyword.framework.KeywordDrivenFramework.TestListener.class })
public class KeywordDrivenTest extends BaseTest {

	private KeywordExecutor keywordExecutor;
	private ExcelDataReader excelDataReader;
	private String userName;
	private Map<String, String> loginData;

	// Static map to track user/browser usage
	private static final Map<String, Boolean> userBusy = Collections.synchronizedMap(new HashMap<>());

	// ✅ Factory constructor: creates a separate instance per user
	@Factory(dataProvider = "LoginData", dataProviderClass = DataProviderUtil.class)
	public KeywordDrivenTest(Map<String, String> testData) {
		this.loginData = testData;
		this.userName = testData.get("UsersName");
		userBusy.put(userName, false);
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
		System.out.println("✅ Login done for user: " + userName);
		quitDriver(); // close after login to save session in profile
	}

	@Test(priority = 2, dataProvider = "PurchaseData", dataProviderClass = DataProviderUtil.class)
	public void runPurchaseTest(String testCaseId, List<String> products) throws IOException, InterruptedException {
		
		String currentUser =  getNextFreeUser(); // find free browser
		System.out.println("🧩 Assigning test case to user: " + currentUser);
        userBusy.put(currentUser, true);  // mark it busy
        
        reopenBrowser(currentUser);  // reopen browser
     
        for (String product : products) {
            keywordExecutor.executeSteps(steps, "Purchase", Map.of("Product", product));
        }
	
		quitDriver();  // close after done
        userBusy.put(currentUser, false);  // mark it free
        System.out.println("✅ Completed Purchase test for user: " + currentUser);
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
	
	private synchronized String getNextFreeUser() throws InterruptedException {
		while (true) {
            for (Map.Entry<String, Boolean> entry : userBusy.entrySet()) {
                if (!entry.getValue()) {
                    return entry.getKey();
                }
            }
            Thread.sleep(1000); // wait a second if both busy
        }
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		quitDriver();
	}
}