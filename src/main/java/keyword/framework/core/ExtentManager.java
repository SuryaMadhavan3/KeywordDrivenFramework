package keyword.framework.core;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

	private static ExtentReports extent;

	public static ExtentReports getInstance() {
		if (extent == null) {
			// Create report output path
			String reportPath = FrameworkPaths.REPORTS_PATH + "TestExecutionReport.html";

			ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
			spark.config().setReportName("Keyword Driven Framework Report");
			spark.config().setDocumentTitle("Automation Results");

			extent = new ExtentReports();
			extent.attachReporter(spark);

			// System information in report
			extent.setSystemInfo("Framework Type", "Keyword-Driven");
			extent.setSystemInfo("Author", "Surya");
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			extent.setSystemInfo("Java Version", System.getProperty("java.version"));
		}
		return extent;
	}
}
