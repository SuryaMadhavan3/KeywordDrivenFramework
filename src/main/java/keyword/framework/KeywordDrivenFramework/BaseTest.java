package keyword.framework.KeywordDrivenFramework;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

	// ✅ Static block to pre-create Chrome profiles before tests start

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
			String tempProfile = System.getProperty("user.dir") + "\\tempProfile_" + Thread.currentThread().getName();
			options.addArguments("user-data-dir=" + tempProfile);
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

	public void initializeDriver(String userName) throws IOException {
		configData = new BrowserReader();
		Map<String, String> config = configData.getConfiguration(FrameworkPaths.CONFIG_PATH, "Configuration");

		String browserName = config.get("Browser");
		baseUrl = config.get("URL");

		WebDriver driver;
		ChromeOptions options = new ChromeOptions();

		Path baseProfiles = Paths.get(System.getProperty("user.dir"), "tempProfiles");
		Files.createDirectories(baseProfiles);
		Path userProfileDir = baseProfiles.resolve(userName + "_profile");
		Files.createDirectories(userProfileDir);
		options.addArguments("user-data-dir=" + userProfileDir.toAbsolutePath().toString());

		if (browserName.equalsIgnoreCase("chrome")) {
			options.addArguments("--start-maximized");
			options.addArguments("--disable-save-password-bubble");
			options.addArguments("--disable-extensions");
			options.addArguments("--no-first-run");
			options.addArguments("--disable-notifications");
			options.addArguments("--disable-popup-blocking");

			driver = new ChromeDriver(options);
		} else {
			throw new RuntimeException("Unsupported browser: " + browserName);
		}

		threadDriver.set(driver);
		threadWait.set(new WebDriverWait(driver, Duration.ofSeconds(15)));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	public void openBaseUrl() {
		if (baseUrl == null || baseUrl.isEmpty()) {
			throw new RuntimeException("Base URL missing in ConfigData.xlsx!");
		}
		getDriver().get(baseUrl);
	}
	
	public void reopenBrowser(String userName) throws IOException {
		quitDriver();
		System.out.println("🔁 Reopening browser for user: "+userName);
		initializeDriver(userName);
		openBaseUrl();
	}
	
	/*@BeforeSuite
	public void preWarmDrivers() {
	    new Thread(() -> new ChromeDriver().quit()).start();
	}*/

	
	@AfterSuite(alwaysRun = true)
	public void cleanupProfiles1() {
		try {
			Path baseDir = Paths.get(System.getProperty("user.dir"));
			Files.list(baseDir).filter(path -> path.getFileName().toString().startsWith("tempProfile_"))
					.forEach(folder -> {
						try {
							Files.walk(folder).sorted(Comparator.reverseOrder()).map(Path::toFile)
									.forEach(java.io.File::delete);
							System.out.println("Deleted " + folder.getFileName());
						} catch (Exception e) {
							System.out.println("Could not delete " + folder.getFileName() + ": " + e.getMessage());
						}
					});
		} catch (Exception e) {
			System.out.println("Cleanup failed: " + e.getMessage());
		}
	}

	public void quitDriver() {
		if (getDriver() != null) {
			getDriver().quit();
			threadDriver.remove();
			threadWait.remove();
		}
	}
}
