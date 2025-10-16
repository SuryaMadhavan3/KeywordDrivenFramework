package keyword.framework.KeywordDrivenFramework;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

public class BaseTest {

	private static ThreadLocal<WebDriver> threadDriver = new ThreadLocal<>();
	private static ThreadLocal<WebDriverWait> threadWait = new ThreadLocal<>();
	protected BrowserReader configData;
	private String baseUrl;

	public WebDriver getDriver() {
		return threadDriver.get();
	}

	public WebDriverWait getWait() {
		return threadWait.get();
	}

	public void initializeDriver() throws IOException {
		configData = new BrowserReader();
		Map<String, String> config = configData.getConfiguration(FrameworkPaths.CONFIG_PATH, "Configuration");

		String browserName = config.get("Browser");
		baseUrl = config.get("URL");

		WebDriver driver;
		
		if (browserName == null || browserName.isEmpty()) {
		    throw new RuntimeException("Browser name missing in ConfigData.xlsx!");
		}
		if (browserName.equalsIgnoreCase("chrome")) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--start-maximized");
			driver = new ChromeDriver(options);
		} else if (browserName.equalsIgnoreCase("edge")) {
			driver = new EdgeDriver();
		} else
			throw new RuntimeException("Unsupported browser: " + browserName);

		threadDriver.set(driver);
		threadWait.set(new WebDriverWait(driver, Duration.ofSeconds(15)));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	public void openBaseUrl() {
		
		if (baseUrl == null || baseUrl.isEmpty()) {
		    throw new RuntimeException("Base URL missing in ConfigData.xlsx!");
		}
		getDriver().get(baseUrl);
		getDriver().manage().deleteAllCookies();
		
		
	}

	public void quitDriver() {
		if (getDriver() != null) {
			getDriver().quit();
			threadDriver.remove();
			threadWait.remove();
		}
	}
}