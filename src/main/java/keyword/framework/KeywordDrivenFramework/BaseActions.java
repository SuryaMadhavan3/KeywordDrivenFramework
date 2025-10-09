package keyword.framework.KeywordDrivenFramework;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
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
            case "id": return By.id(locatorValue);
            case "xpath": return By.xpath(locatorValue);
            case "css": return By.cssSelector(locatorValue);
            case "classname": return By.className(locatorValue);
            case "name": return By.name(locatorValue);
            case "linktext": return By.linkText(locatorValue);
            case "partiallinktext": return By.partialLinkText(locatorValue);
            case "tagname": return By.tagName(locatorValue);
            default: throw new IllegalArgumentException("Invalid locator type: " + locatorType);
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
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(locatorType, locatorValue)));
        element.clear();
        element.sendKeys(data);
        System.out.println("[ACTION] Entered text: " + data + " into " + locatorValue);
    }

    public void hover(String locatorType, String locatorValue) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(locatorType, locatorValue)));
        actions.moveToElement(element).perform();
        System.out.println("[ACTION] Hovered over: " + locatorValue);
    }

    public void scrollTo(String locatorType, String locatorValue) {
        try {
            List<WebElement> elements = driver.findElements(getBy(locatorType, locatorValue));
            if (elements.isEmpty()) {
                throw new NoSuchElementException("Element not found: " + locatorValue);
            }

            WebElement target = elements.stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(elements.get(0));

            ((JavascriptExecutor) driver).executeScript(
            	    "arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", target);
            	Thread.sleep(1000);


            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOf(target));

            System.out.println("✅ Scrolled to element successfully: " + locatorValue);

        } catch (Exception e) {
            System.err.println("❌ Failed to scroll to element: " + locatorValue + " | " + e.getMessage());
        }
    }


    public void selectDropdown(String locatorType, String locatorValue, String visibleText) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(locatorType, locatorValue)));
        new Select(dropdown).selectByVisibleText(visibleText);
        System.out.println("[ACTION] Selected dropdown: " + visibleText);
    }

    public void verifyText(String locatorType, String locatorValue, String expected) {
        String actual = wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(locatorType, locatorValue))).getText().trim();
        if (!actual.equalsIgnoreCase(expected)) {
            throw new AssertionError("❌ Text mismatch. Expected: " + expected + ", Found: " + actual);
        }
        System.out.println("[VERIFY] Text verified: " + expected);
    }

    public void optionalClick(String locatorType, String locatorValue) {
        try {
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


    public void clickListByText(String locatorType, String locatorValue, String text) {
        List<WebElement> items = driver.findElements(getBy(locatorType, locatorValue));
        for (WebElement item : items) {
            if (item.getText().toLowerCase().contains(text.toLowerCase())) {
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
        String current = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(current)) {
                driver.switchTo().window(handle);
                System.out.println("[ACTION] Switched to new window: " + handle);
                break;
            }
        }
    }public void switchToProductPage(int timeoutSeconds) {
        String originalHandle = driver.getWindowHandle();
        Set<String> oldHandles = driver.getWindowHandles();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        try {
            // Wait for either a new window OR a new URL in the same window
            wait.until(d -> {
                Set<String> newHandles = d.getWindowHandles();
                if (newHandles.size() > oldHandles.size()) return true;
                String currentUrl = d.getCurrentUrl();
                // Amazon product pages usually contain "/dp/" or "/gp/"
                return currentUrl.contains("/dp/") || currentUrl.contains("/gp/");
            });

            Set<String> allHandles = driver.getWindowHandles();

            if (allHandles.size() > oldHandles.size()) {
                // Real new tab opened
                for (String handle : allHandles) {
                    if (!oldHandles.contains(handle)) {
                        driver.switchTo().window(handle);
                        break;
                    }
                }
                System.out.println("✅ Switched to new product tab.");
            } else {
                // No new tab — same window navigation
                System.out.println("ℹ️ Product opened in same tab. Staying here.");
            }

            // Wait for page load completion
            wait.until(d -> ((JavascriptExecutor) d)
                    .executeScript("return document.readyState").equals("complete"));

            System.out.println("🟢 Product page loaded: " + driver.getCurrentUrl());

        } catch (Exception e) {
            System.out.println("❌ Failed to switch or detect new tab: " + e.getMessage());
        }
    }


    public void switchToNewWindowAndWait(int timeoutSeconds) {
        try {
            String currentWindow = driver.getWindowHandle();
            Set<String> oldWindows = driver.getWindowHandles();

            System.out.println("🕒 Waiting for new window to open...");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

            // Wait until a new window handle appears
            wait.until(d -> d.getWindowHandles().size() > oldWindows.size());

            // Switch to the new window
            Set<String> allWindows = driver.getWindowHandles();
            for (String window : allWindows) {
                if (!oldWindows.contains(window)) {
                    driver.switchTo().window(window);
                    System.out.println("✅ Switched to new window: " + window);
                    break;
                }
            }

            // Wait for page to finish loading fully
            wait.until(d -> ((JavascriptExecutor) d)
                    .executeScript("return document.readyState").equals("complete"));

            System.out.println("🟢 Page loaded: " + driver.getCurrentUrl());

        } catch (Exception e) {
            System.out.println("❌ Failed to switch to new window: " + e.getMessage());
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
        } catch (InterruptedException ignored) {}
    }

    public void navigateTo(String url) {
        driver.navigate().to(url);
        System.out.println("[ACTION] Navigated to: " + url);
    }
}
