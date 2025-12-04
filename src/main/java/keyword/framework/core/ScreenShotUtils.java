package keyword.framework.core;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenShotUtils {

    public static String captureScreenshot(WebDriver driver, String screenshotName) {

        if (driver == null) {
            System.out.println("❌ Cannot capture screenshot: WebDriver is null.");
            return null;
        }

        try {
            // readable timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            // standard framework path: /reports/screenshots/
            String screenshotDir = FrameworkPaths.REPORTS_PATH + "screenshots/";
            Files.createDirectories(Paths.get(screenshotDir));

            String finalPath = screenshotDir + screenshotName + "_" + timestamp + ".png";

            // take screenshot
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(finalPath);

            FileUtils.copyFile(src, dest);

            System.out.println("📸 Screenshot saved at: " + finalPath);
            return finalPath;

        }catch (IOException e) {
            System.out.println("❌ Screenshot saving failed: " + e.getMessage());
            return null;
        }
        catch (Exception e) {
            System.out.println("❌ Screenshot error: " + e.getMessage());
            return null;
        }
    }
}
