package keyword.framework.core;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestCaseQueue {

	// Thread-safe queues
	private static final Queue<Map<String, Object>> purchaseQueue = new ConcurrentLinkedQueue<>();
	
	// LOADERS (Called only once)
	public static synchronized void loadPurchaseCases(List<Map<String, Object>> cases) {
		if (purchaseQueue.isEmpty()) {
			purchaseQueue.addAll(cases);
			System.out.println("📌 Loaded Purchase queue: " + purchaseQueue.size());
		}
	}
	
	// Get next test case safely
	public static synchronized Map<String, Object> getNextPurchaseCase() {
		return purchaseQueue.poll();
	}
}
