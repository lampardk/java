package Problem3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ============================================================================
 * INVENTORY MANAGER CLASS (Task a, f, g)
 * ============================================================================
 * Manages product stock:
 * - 100 units of Product A
 * - 50 units of Product B
 *
 * Concurrency Safety:
 * - Uses ReentrantLock to ensure atomic stock validation, reservation, and rollback.
 * - Prevents overselling (Task f).
 * - Implements stock release on cancellation (Task g).
 */
public class InventoryManager {

    // Product stock tracking
    private final ConcurrentHashMap<String, Integer> stock = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> totalSold = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> totalCancelledStock = new ConcurrentHashMap<>();

    // ReentrantLock ensures atomic checks so 2 threads cannot claim the last unit
    private final ReentrantLock inventoryLock = new ReentrantLock(true);

    public InventoryManager(int stockA, int stockB) {
        stock.put("Product A", stockA);
        stock.put("Product B", stockB);
        totalSold.put("Product A", 0);
        totalSold.put("Product B", 0);
        totalCancelledStock.put("Product A", 0);
        totalCancelledStock.put("Product B", 0);
    }

    /**
     * Attempts to reserve stock for an order (Task f: prevents overselling).
     * Returns true if reserved successfully, false if insufficient stock.
     */
    public boolean reserveStock(CustomerOrder order) {
        inventoryLock.lock();
        try {
            String product = order.getProductName();
            int qty = order.getQuantity();
            int available = stock.getOrDefault(product, 0);

            if (available >= qty) {
                // Deduct from available stock
                stock.put(product, available - qty);
                totalSold.put(product, totalSold.get(product) + qty);
                return true;
            } else {
                return false;
            }
        } finally {
            inventoryLock.unlock();
        }
    }

    /**
     * Releases reserved stock back into inventory if an order is cancelled (Task g).
     */
    public void releaseStock(CustomerOrder order) {
        inventoryLock.lock();
        try {
            String product = order.getProductName();
            int qty = order.getQuantity();

            stock.put(product, stock.get(product) + qty);
            totalSold.put(product, totalSold.get(product) - qty);
            totalCancelledStock.put(product, totalCancelledStock.get(product) + qty);

            System.out.println("[STOCK RELEASED] Order #" + order.getOrderId()
                    + " cancelled. Restored " + qty + " units of " + product
                    + " (Now available: " + stock.get(product) + ")");
        } finally {
            inventoryLock.unlock();
        }
    }

    public int getStock(String product) {
        inventoryLock.lock();
        try {
            return stock.getOrDefault(product, 0);
        } finally {
            inventoryLock.unlock();
        }
    }

    public int getTotalSold(String product) {
        inventoryLock.lock();
        try {
            return totalSold.getOrDefault(product, 0);
        } finally {
            inventoryLock.unlock();
        }
    }

    public int getTotalCancelledStock(String product) {
        inventoryLock.lock();
        try {
            return totalCancelledStock.getOrDefault(product, 0);
        } finally {
            inventoryLock.unlock();
        }
    }
}

