
package keyword.framework.KeywordDrivenFramework;

import org.openqa.selenium.WebDriver;
import java.util.List;
import java.util.Map;

public class KeywordExecutor extends BaseActions {

    private boolean skipNextStep = false;

    public KeywordExecutor(WebDriver driver) {
        super(driver);
    }

    public void executeKeyword(String action, String locatorType, String locatorValue, String data) {
        if (action == null || action.trim().isEmpty()) {
            System.out.println("⚠️ Empty action. Skipping step.");
            return;
        }

        action = action.trim().toLowerCase();

        try {
            switch (action) {
                case "click":
                    click(locatorType, locatorValue);
                    break;

                case "entertext":
                    enterText(locatorType, locatorValue, data);
                    break;

                case "hover":
                    hover(locatorType, locatorValue);
                    break;

                case "scrollandclick":
                	scrollAndClick(locatorType, locatorValue);
                    break;

                case "optionalclick":
                    optionalClick(locatorType, locatorValue);
                    break;

                case "dismiss":
                    dismissElementIfVisible(locatorType, locatorValue);
                    break;

                case "sleep":
                    sleep(Long.parseLong(data));
                    break;

                case "switch":
                    switchToNewWindowSimple();
                    break;
                
                case "closeCurrentTabAndSwitchBack":
                	closeCurrentTabAndSwitchBack();
                    break;
                
                case "switchtonewwindowandwait":
                    switchToNewWindowAndWait(20); 
                    break;

                case "verifytext":
                    verifyText(locatorType, locatorValue, data);
                    break;

                case "selectdropdown":
                    selectDropdown(locatorType, locatorValue, data);
                    break;

                case "clicklistbytext":
                    clickListByText(locatorType, locatorValue, data);
                    break;

                case "elementpresent":
                    System.out.println("[CHECK] Element present: " + elementPresent(locatorType, locatorValue));
                    break;

                case "waitforseconds":
                    waitForSeconds(data);
                    break;

                case "acceptalert":
                    acceptAlert();
                    break;

                case "navigate":
                    navigateTo(data);
                    break;

                case "findelement":
                    System.out.println("[CHECK] Element found: " + findElement(locatorType, locatorValue));
                    break;
        
                case "noop":
                case "comment":
                    System.out.println("📝 Comment: " + data);
                    break;

                default:
                    System.out.println("❌ Unknown action: " + action);
            }

        } catch (Exception e) {
            System.out.println("❗ Error executing: " + action + " | Message: " + e.getMessage());
        }
    }

    public void executeSteps(List<Map<String, String>> steps, String testCaseId, Map<String, String> testData) {
        for (Map<String, String> step : steps) {
            if (!testCaseId.equalsIgnoreCase(step.get("TestCaseName"))) continue;

            if (skipNextStep) {
                System.out.println("⏭️ Skipping step '" + step.get("ElementName") + "' due to condition.");
                skipNextStep = false;
                continue;
            }

            String action = step.get("Action");
            String locatorType = step.get("LocatorType");
            String locatorValue = step.get("LocatorValue");
            String dataKey = step.get("TestData");
            String actualData = (dataKey != null && !dataKey.isEmpty())
                    ? testData.getOrDefault(dataKey, dataKey)
                    : null;
                  
            System.out.println("➡️ Executing: " + action +
                    " | Locator: [" + locatorType + "=" + locatorValue + "] | Data: " + actualData);

            executeKeyword(action, locatorType, locatorValue, actualData);
        }
    }
}
