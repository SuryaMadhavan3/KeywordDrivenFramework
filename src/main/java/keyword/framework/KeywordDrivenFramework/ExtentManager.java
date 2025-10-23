package keyword.framework.KeywordDrivenFramework;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            String reportPath = FrameworkPaths.REPORTS_PATH + "TestReport.html";
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setReportName("Keyword Driven Framework Report");
            spark.config().setDocumentTitle("Automation Results");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Framework", "Keyword Driven");
            extent.setSystemInfo("Tester", "Malini");
        }
        return extent;
    }
}
