package keyword.framework.KeywordDrivenFramework;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestDataReader extends ExcelDataReader {
	
    public List<Map<String, String>> getTestData(String dataSheet) throws IOException {
        return readSheet(FrameworkPaths.TESTDATA_PATH, dataSheet);
    }
}


