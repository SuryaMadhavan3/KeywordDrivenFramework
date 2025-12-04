package keyword.framework.listeners;

import com.aventstack.extentreports.*;

import keyword.framework.core.BaseTest;
import keyword.framework.core.ExtentManager;
import keyword.framework.core.ScreenShotUtils;

import org.testng.*;

public class TestListener extends BaseTest implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
    	String testName = result.getMethod().getMethodName();
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
        
        System.out.println("🚀 TEST STARTED: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    	String testName = result.getMethod().getMethodName(); 
        test.get().log(Status.PASS, "Test Passed");
        System.out.println("✅ TEST PASSED: " + testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
    	String testName = result.getMethod().getMethodName(); 
    	
		test.get().log(Status.FAIL, "Test Failed");
        test.get().log(Status.FAIL, result.getThrowable());
        // Capture screenshot with current WebDriver from BaseTest
        String screenshotPath = ScreenShotUtils.captureScreenshot(getDriver(), testName + "_FAILED");
        if (screenshotPath != null) {
            try {
                test.get().addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                test.get().log(Status.WARNING, "⚠️ Screenshot could not be attached.");
                e.printStackTrace();
            }
        } 
        System.out.println("❌ TEST FAILED: " + testName);
        }
    
    @Override
    public void onTestSkipped(ITestResult result) {
    	String testName = result.getMethod().getMethodName();
        test.get().log(Status.SKIP, "Test Skipped");
        System.out.println("⚠ TEST SKIPPED: " + testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
        System.out.println("📄 Extent Report Generated");
    }
}