package keyword.framework.KeywordDrivenFramework;

import com.aventstack.extentreports.*;
import org.testng.*;

public class TestListener extends BaseTest implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "Test Failed");
        test.get().log(Status.FAIL, result.getThrowable());

        // Capture screenshot with current WebDriver from BaseTest
        String screenshotPath = Utils.captureScreenshot(getDriver(), result.getMethod().getMethodName());
        if (screenshotPath != null) {
            try {
                test.get().addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                test.get().log(Status.WARNING, "⚠️ Screenshot could not be attached.");
                e.printStackTrace();
            }
        } else {
            test.get().log(Status.WARNING, "Screenshot path was null.");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
