package keyword.framework.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseTest {

	private static ThreadLocal<WebDriver> threadDriver = new ThreadLocal<>();
	private static ThreadLocal<WebDriverWait> threadWait = new ThreadLocal<>();
	protected ConfigReader configData;
	private String browserName, baseUrl;

	public WebDriver getDriver() 
	{
		return threadDriver.get();
	}

	public WebDriverWait getWait() 
	{
		return threadWait.get();
	}

	public void initializeDriver(String userName) throws IOException {
		configData = new ConfigReader();
		Map<String, String> config = configData.getConfigData();

		browserName = config.get("Browser");
		baseUrl = config.get("URL");
		// System.out.println("Browser Name : "+browserName);
		// System.out.println("URL : "+baseUrl);

		WebDriver driver;

		if (browserName.equalsIgnoreCase("chrome")) 
		{
			ChromeOptions options = new ChromeOptions();
			
			// User profile folder (session persistence)
			Path baseProfiles = Paths.get(System.getProperty("user.dir"), "tempProfiles");
			Files.createDirectories(baseProfiles);
			
			Path userProfileDir = baseProfiles.resolve(userName + "_profile");
			Files.createDirectories(userProfileDir);
			
			options.addArguments("user-data-dir=" + userProfileDir.toAbsolutePath().toString());
			options.addArguments("--start-maximized");
			options.addArguments("--disable-notifications");
			options.addArguments("--disable-popup-blocking");

			driver = new ChromeDriver(options);
		} 
		else 
		{
			throw new RuntimeException("Unsupported browser: " + browserName);
		}

		threadDriver.set(driver);
		threadWait.set(new WebDriverWait(driver, Duration.ofSeconds(15)));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		 System.out.println("🌐 Browser Launched for User: " + userName);
	}

	public void openBaseUrl() {
		if (baseUrl == null || baseUrl.isEmpty()) 
		{
			throw new RuntimeException("Base URL missing in ConfigData.xlsx!");
		}
		getDriver().get(baseUrl);
		String title = getDriver().getTitle();
		System.out.println("Navigated to:" + baseUrl);
		System.out.println("URL Title:" + title);
	}

	public void reopenBrowser(String userName) throws IOException {
		quitDriver();
		System.out.println("🔁 Reopening browser for user: " + userName);
		initializeDriver(userName);
		openBaseUrl();
	}

	public void quitDriver() {
		try {
			WebDriver driver = getDriver();
			if (driver != null) {
				driver.quit();
			}
		} finally {
			threadDriver.remove();
			threadWait.remove();
		}

		System.out.println("🛑 Browser Closed");
	}
}
