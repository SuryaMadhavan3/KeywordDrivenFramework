package keyword.framework.KeywordDrivenFramework;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            System.out.println("Driver is null. Screenshot cannot be captured.");
            return null;
        }

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String destDir = System.getProperty("user.dir") + File.separator + "screenshots" + File.separator;
        String destPath = destDir + testName + "_" + timestamp + ".png";

        try {
            File dir = new File(destDir);
            if (!dir.exists()) dir.mkdirs();
            FileUtils.copyFile(src, new File(destPath));
            System.out.println("Screenshot saved at: " + destPath);
            return destPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
