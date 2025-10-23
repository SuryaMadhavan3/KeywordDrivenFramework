package keyword.framework.KeywordDrivenFramework;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaseActions {

	private WebDriver driver;
	private WebDriverWait wait;
	private Actions actions;

	public BaseActions(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		this.actions = new Actions(driver);
	}

	public By getBy(String locatorType, String locatorValue) {
		switch (locatorType.toLowerCase()) {

		case "id":
			return By.id(locatorValue);
		case "xpath":
			return By.xpath(locatorValue);
		case "css":
			return By.cssSelector(locatorValue);
		case "classname":
			return By.className(locatorValue);
		case "name":
			return By.name(locatorValue);
		case "linktext":
			return By.linkText(locatorValue);
		case "partiallinktext":
			return By.partialLinkText(locatorValue);
		case "tagname":
			return By.tagName(locatorValue);
		default:
			throw new IllegalArgumentException("Invalid locator type: " + locatorType);
		}
	}

	public void click(String locatorType, String locatorValue) {
		try {
			WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(ExpectedConditions.elementToBeClickable(getBy(locatorType, locatorValue)));
			element.click();
			System.out.println("[ACTION] Clicked: " + locatorValue);
		} catch (TimeoutException e) {
			System.out.println("[ERROR] Element not clickable: " + locatorValue);
		}
	}

	public void enterText(String locatorType, String locatorValue, String data) {
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(getBy(locatorType, locatorValue)));
		element.clear();
		element.sendKeys(data);
		System.out.println("[ACTION] Entered text: " + data + " into " + locatorValue);
	}

	public void hover(String locatorType, String locatorValue) {
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(getBy(locatorType, locatorValue)));
		actions.moveToElement(element).perform();
		System.out.println("[ACTION] Hovered over: " + locatorValue);
	}

	public void scrollAndClick(String locatorType, String locatorValue) {
		try {
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(getBy(locatorType, locatorValue)));

			// Scroll into view
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);

			// Click element
			element.click();

			System.out.println("✅ Scrolled and clicked element: " + locatorValue);

		} catch (Exception e) {
			System.err.println("❌ Failed to scroll and click: " + locatorValue + " | " + e.getMessage());
		}
	}

	public void selectDropdown(String locatorType, String locatorValue, String visibleText) {
		WebElement dropdown = wait
				.until(ExpectedConditions.visibilityOfElementLocated(getBy(locatorType, locatorValue)));
		new Select(dropdown).selectByVisibleText(visibleText);
		System.out.println("[ACTION] Selected dropdown: " + visibleText);
	}

	public void verifyText(String locatorType, String locatorValue, String expected) {
		String actual = wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(locatorType, locatorValue)))
				.getText().trim();
		if (!actual.equalsIgnoreCase(expected)) {
			throw new AssertionError("❌ Text mismatch. Expected: " + expected + ", Found: " + actual);
		}
		System.out.println("[VERIFY] Text verified: " + expected);
	}

	public void optionalClick(String locatorType, String locatorValue) {
		try {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

			WebElement element = new WebDriverWait(driver, Duration.ofSeconds(2))
					.until(ExpectedConditions.elementToBeClickable(getBy(locatorType, locatorValue)));

			element.click();
			System.out.println("[OPTIONAL] Clicked element: " + locatorValue);

		} catch (TimeoutException e) {
			System.out.println("[OPTIONAL] Element not found (skipped): " + locatorValue);
		} catch (Exception e) {
			System.out.println("[OPTIONAL] Unexpected issue with element: " + locatorValue + " - " + e.getMessage());
		}
	}

	public List<String> getElementsTextLowerCaseTrimmed(String locatorType, String locatorValue) {
		List<WebElement> elements = driver.findElements(getBy(locatorType, locatorValue));
		List<String> cleanedTexts = new ArrayList<>();

		for (WebElement element : elements) {
			String text = element.getText();
			cleanedTexts.add(text != null ? text.toLowerCase().trim() : "");
		}
		return cleanedTexts;
	}

	public void clickListByText(String locatorType, String locatorValue, String text) {
		List<WebElement> items = driver.findElements(getBy(locatorType, locatorValue));
		for (WebElement item : items) {
			if (item.getText().toLowerCase().contains(text.toLowerCase())) {
				// Scroll to element before clicking
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", item);
				item.click();
				System.out.println("[ACTION] Clicked product containing: " + text);
				return;
			}
		}
		System.out.println("[INFO] Product not found: " + text);
	}

	public void acceptAlert() {
		try {
			Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			alert.accept();
			System.out.println("[ACTION] Alert accepted.");
		} catch (TimeoutException e) {
			System.out.println("[INFO] No alert found.");
		}
	}

	public void switchToNewWindowSimple() {
		try {
			// Get all open window handles
			List<String> tabs = new ArrayList<>(driver.getWindowHandles());

			// Switch to the last (newest) tab
			driver.switchTo().window(tabs.get(tabs.size() - 1));

			System.out.println("[ACTION] Switched to newest tab: " + driver.getCurrentUrl());
		} catch (Exception e) {
			System.err.println("❌ Failed to switch to newest tab: " + e.getMessage());
		}
	}

	public void switchToNewWindowAndWait(int timeoutSeconds) {
		try {
			// Save the current window and URL before the click
			String originalHandle = driver.getWindowHandle();
			Set<String> oldHandles = driver.getWindowHandles();

			System.out.println("Current window handles before click: " + oldHandles.size());

			Thread.sleep(2000); // Pause to let any new tab open (if needed)

			System.out.println("Window handles after click: " + driver.getWindowHandles().size());

			String startingUrl = driver.getCurrentUrl();

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

			// Wait for either: a new tab OR URL change in same tab
			wait.until(d -> {
				Set<String> newHandles = d.getWindowHandles();
				String currentUrl = d.getCurrentUrl();
				return newHandles.size() > oldHandles.size() || !currentUrl.equals(startingUrl);
			});

			Set<String> allHandles = driver.getWindowHandles();

			// If a new window opened, switch to it
			if (allHandles.size() > oldHandles.size()) {
				for (String handle : allHandles) {
					if (!oldHandles.contains(handle)) {
						driver.switchTo().window(handle);
						System.out.println("✅ Switched to new tab/window: " + handle);
						break;
					}
				}
			} else {
				System.out.println("ℹ️ No new window detected — product opened in same tab.");
			}

			// Wait for page to finish loading
			wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));

			System.out.println("🟢 Page loaded successfully: " + driver.getCurrentUrl());

		} catch (Exception e) {
			System.err.println("❌ Failed to switch or detect new window: " + e.getMessage());
		}
	}

	public void closeCurrentTabAndSwitchBack() {
		List<String> tabs = new ArrayList<>(driver.getWindowHandles());
		if (tabs.size() > 1) {
			driver.close(); // closes current tab
			driver.switchTo().window(tabs.get(tabs.size() - 2)); // go back to previous tab
			System.out.println("[ACTION] Closed current tab and switched back.");
		}
	}

	public void dismissElementIfVisible(String locatorType, String locatorValue) {
		try {
			WebElement element = new WebDriverWait(driver, Duration.ofSeconds(3))
					.until(ExpectedConditions.visibilityOfElementLocated(getBy(locatorType, locatorValue)));
			element.click();
			System.out.println("[OPTIONAL] Dismissed element: " + locatorValue);
		} catch (TimeoutException e) {
			System.out.println("[OPTIONAL] Element not visible, skipping: " + locatorValue);
		}
	}

	public void waitForSeconds(String seconds) {
		try {
			Thread.sleep(Long.parseLong(seconds) * 1000);
			System.out.println("[WAIT] Waited for " + seconds + " seconds");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean elementPresent(String locatorType, String locatorValue) {
		try {
			driver.findElement(getBy(locatorType, locatorValue));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean findElement(String locatorType, String locatorValue) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.presenceOfElementLocated(getBy(locatorType, locatorValue)));
			System.out.println("🔍 Element found: " + locatorValue);
			return true;
		} catch (TimeoutException e) {
			System.out.println("🔍 Element not found: " + locatorValue);
			return false;
		}
	}

	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {
		}
	}

	public void navigateTo(String url) {
		driver.navigate().to(url);
		System.out.println("[ACTION] Navigated to: " + url);
	}
}