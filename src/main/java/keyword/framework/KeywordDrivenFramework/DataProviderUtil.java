package keyword.framework.KeywordDrivenFramework;

import java.io.IOException;
import java.util.*;
import org.testng.annotations.DataProvider;

public class DataProviderUtil {

	private static final ExcelDataReader reader;

	static {
		ExcelDataReader temp = null;
		try {
			temp = new ExcelDataReader();
			System.out.println("✅ ExcelDataReader initialized successfully");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("❌ Failed to initialize ExcelDataReader: " + e.getMessage());
		}
		reader = temp;
	}

	// PURCHASE GROUPED LOADER → LOAD ONCE INTO SHARED QUEUE
	@DataProvider(name = "PurchaseGroupData", parallel = true)
	public static Object[][] groupedPurchaseData() throws IOException {

		if (reader == null) {
			return new Object[][] { { "USE_QUEUE" } };
		}

		List<Map<String, String>> rows = reader.getTestDataRows("Purchase");
		List<Map<String, Object>> grouped = new ArrayList<>();

		for (Map<String, String> row : rows) {

			String tcId = row.get("TestCaseID");
			if (tcId == null)
				continue;

			List<String> products = new ArrayList<>();
			List<Double> expectedPrices = new ArrayList<>();

			// Dynamically detect Product N / Price N
			for (String key : row.keySet()) {
				if (key.toLowerCase().startsWith("product")) {

					String product = row.get(key);
					if (product == null || product.isBlank()) continue;

					String index = key.replaceAll("[^0-9]", "");
					String priceKey = "Price " + index;

					double expPrice = 0.0;
					try {
						expPrice = Double.parseDouble(row.get(priceKey));
					} catch (Exception ignored) {
					}

					products.add(product);
					expectedPrices.add(expPrice);
				}
			}
			double expectedTotal = 0.0;
			try {
				expectedTotal = Double.parseDouble(row.get("Expected total amount"));
			} catch (Exception ignored) {
			}

			Map<String, Object> map = new HashMap<>();
			map.put("TestCaseID", tcId);
			map.put("Products", products);
			map.put("ExpectedPrices", expectedPrices);
			map.put("ExpectedTotal", expectedTotal);

			grouped.add(map);
		}
		
		// Load into shared queue ONCE
		TestCaseQueue.loadPurchaseCases(grouped);
		System.out.println("✅ PurchaseGroupData provided: " + grouped.size() + " testcases");
		return new Object[][] { { "USE_QUEUE" } };
	}

	// Login provider unchanged
	@DataProvider(name = "LoginData")
	public static Iterator<Object[]> loginData() throws IOException {
		List<Map<String, String>> list = reader.getTestDataRows("Login");
		List<Object[]> out = new ArrayList<>();
		for (Map<String, String> m : list) {
			out.add(new Object[] { m });
		}
		return out.iterator();
	}
}