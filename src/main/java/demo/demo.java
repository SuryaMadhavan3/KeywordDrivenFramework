package demo;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class demo {

	
	public static void main(String[] args) throws InterruptedException {
		
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-data-dir=" + System.getProperty("user.dir") + "/tempProfile");
		options.addArguments("--disable-web-authn"); // lowercase, works better
		options.addArguments("--disable-password-manager-reauthentication");
		options.addArguments("--disable-notifications");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--no-first-run");
		options.addArguments("--disable-extensions");
		WebDriver driver = new ChromeDriver(options);
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		Actions actions = new Actions(driver);
		
		driver.get("https://www.amazon.in/");
		driver.manage().window().maximize();
		
	
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id='nav-link-accountList']")));
		actions.moveToElement(element).perform();
		
		try {
			WebElement sigIn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Sign in']")));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", sigIn);
			sigIn.click();
			System.out.println("✅ Scrolled and clicked element: " + sigIn);
		} catch (Exception e) {
			System.err.println("❌ Failed to scroll and click: SigIn");
		}
		
		
		WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='email' and @name='email' and contains(@class, 'a-input-text')]")));
		emailField.clear();
		emailField.sendKeys("6383175201");
		
		driver.findElement(By.xpath("//input[@type='submit' and contains(@class, 'a-button-input')]")).click();
		
		WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password']")));
		passwordField.clear();
		passwordField.sendKeys("Surya*2403");
		
		driver.findElement(By.id("signInSubmit")).click();
		
		Thread.sleep(5000);
		driver.quit();
	}
}
