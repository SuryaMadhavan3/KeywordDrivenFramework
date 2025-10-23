
package keyword.framework.KeywordDrivenFramework;

import org.testng.annotations.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Listeners;

@Listeners({keyword.framework.KeywordDrivenFramework.TestListener.class})
public class KeywordDrivenTest extends BaseTest {

    private KeywordExecutor keywordExecutor;
    private ExcelDataReader excelDataReader ;

    @BeforeClass
    public void setUp() throws IOException {
        initializeDriver();
        openBaseUrl();

        keywordExecutor = new KeywordExecutor(getDriver());
        excelDataReader= new ExcelDataReader();
    }

    @Test(dataProvider = "LoginData", dataProviderClass = DataProviderUtil.class, priority = 1)
    public void runLoginTest(Map<String, String> testData) throws IOException {
        List<Map<String, String>> steps =excelDataReader.getKeywordSteps("Login");
        keywordExecutor.executeSteps(steps, "Login", testData);
    }

   /* @Test(dataProvider = "PurchaseData", dataProviderClass = DataProviderUtil.class, priority = 2)
    public void runPurchaseTest(Map<String, String> testData) throws IOException {
        List<Map<String, String>> steps =excelDataReader.getKeywordSteps("Purchase");
        keywordExecutor.executeSteps(steps, "Purchase", testData);
    }
    @Test(dataProvider = "RemoveData", dataProviderClass = DataProviderUtil.class, priority = 3)
    public void runRemoveTest(Map<String, String> testData) throws IOException {
        List<Map<String, String>> steps =excelDataReader.getKeywordSteps("RemoveProduct");
        keywordExecutor.executeSteps(steps, "RemoveProduct", testData);
    }*/
    
    @Test(priority = 4)
    public void runSignoutTest() throws IOException {
        List<Map<String, String>> steps = excelDataReader.getKeywordSteps("SignOut");
        keywordExecutor.executeSteps(steps, "SignOut", new HashMap<>());
    }
         
    @Test(dataProvider = "InvalidLoginData", dataProviderClass = 
    		 DataProviderUtil.class, priority = 5)
    public void runInvalidTest(Map<String, String> testData) throws IOException {
        List<Map<String, String>> steps =excelDataReader.getKeywordSteps("InvalidLogin");
        keywordExecutor.executeSteps(steps, "InvalidLogin", testData);
    }
    
    @Test(dataProvider = "InvalidMailData", dataProviderClass = 
   		 DataProviderUtil.class, priority = 6)
   public void runInvalidMailTest(Map<String, String> testData) throws IOException {
       List<Map<String, String>> steps =excelDataReader.getKeywordSteps("InvalidMailLogin");
       keywordExecutor.executeSteps(steps, "InvalidMailLogin", testData);
   }
    @AfterClass
    public void tearDown() {
        quitDriver();
    }
}
