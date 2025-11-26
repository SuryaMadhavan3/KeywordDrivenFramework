package demo;

import java.util.List;
import java.time.Duration;
import java.util.Iterator;
import java.util.Set;


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

		options.addArguments("--start-maximized");
		options.addArguments("--disable-notifications");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--no-first-run");
		options.addArguments("--disable-extensions");
		WebDriver driver = new ChromeDriver(options);

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		Actions actions = new Actions(driver);

		driver.get("https://www.amazon.in/");
		
		String title = driver.getTitle();
		System.out.println(title);
		
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id='nav-link-accountList']")));
		actions.moveToElement(element).perform();

		try {
			WebElement sigIn = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Sign in']")));
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
	
		Thread.sleep(3000);
		
		
		
		driver.findElement(By.id("nav-cart")).click();
		List<WebElement> products = driver.findElements(By.xpath("//form[@id='activeCartViewForm']//div[@class='sc-item-content-group']"));
		int total = products.size();   // get count only
		for (int i = 0; i < total; i++) {
		    // Re-locate delete button fresh each time
		    WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@data-feature-id='delete-active'])[" + 1 + "]")));
		    deleteBtn.click();
		    // Wait for item to disappear from DOM
		    wait.until(ExpectedConditions.stalenessOf(deleteBtn));
		    Thread.sleep(1500);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		driver.findElement(By.id("twotabsearchtextbox")).sendKeys("Apple iphone 14");
		driver.findElement(By.id("nav-search-submit-button")).click();
		Thread.sleep(3000);
		driver.findElement(By.xpath("(//div[@data-component-type='s-search-result']//a//h2)[1]")).click();
		Set<String> allwindowhandles = driver.getWindowHandles();
		Iterator<String> it = allwindowhandles.iterator();
		String parentId = it.next();
		String childId = it.next();
		driver.switchTo().window(childId);
		String price = driver.findElement(By.xpath("//span[@class=\"a-price aok-align-center reinventPricePriceToPayMargin priceToPay\"]//span[@class=\"a-price-whole\"]")).getText();
		System.out.println(price);
		
		Thread.sleep(3000);

		driver.quit();
	}
}
