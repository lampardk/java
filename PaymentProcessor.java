package Problem3;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * PAYMENT PROCESSOR WORKER (Task a, c, d, g)
 * ============================================================================
 * Represents one of the 3 concurrent payment workers.
 *
 * Responsibilities:
 * 1. Pulls incoming customer orders from orderSubmissionQueue.
 * 2. Attempts inventory validation and reservation via InventoryManager (Task f).
 * 3. Simulates payment card authorization:
 *    - If payment succeeds: marks PAID and pushes order to warehouse packing queue.
 *    - If payment fails (Task g): cancels order, rolls back and releases
 *      the reserved stock back to inventory, and notifies customer.
 */
public class PaymentProcessor implements Runnable {

    private final String workerName;
    private final BlockingQueue<CustomerOrder> submissionQueue;
    private final BlockingQueue<CustomerOrder> packingQueue;
    private final InventoryManager inventoryManager;
    private final NotificationService notificationService;

    // Shared metrics across all payment processors
    private static final AtomicInteger successfulPayments = new AtomicInteger(0);
    private static final AtomicInteger failedPayments = new AtomicInteger(0);
    private static final AtomicInteger outOfStockCancellations = new AtomicInteger(0);

    private volatile boolean keepRunning = true;
    private final Random random = new Random();

    public PaymentProcessor(String workerName,
                            BlockingQueue<CustomerOrder> submissionQueue,
                            BlockingQueue<CustomerOrder> packingQueue,
                            InventoryManager inventoryManager,
                            NotificationService notificationService) {
        this.workerName = workerName;
        this.submissionQueue = submissionQueue;
        this.packingQueue = packingQueue;
        this.inventoryManager = inventoryManager;
        this.notificationService = notificationService;
    }

    public void stopWorker() {
        this.keepRunning = false;
    }

    @Override
    public void run() {
        while (keepRunning || !submissionQueue.isEmpty()) {
            try {
                CustomerOrder order = submissionQueue.poll(50, TimeUnit.MILLISECONDS);
                if (order == null) continue;

                order.setStatus(OrderStatus.PAYMENT_PENDING, "Under review by " + workerName);

                // Step 1: Inventory Validation & Reservation (Task f)
                boolean stockReserved = inventoryManager.reserveStock(order);
                if (!stockReserved) {
                    // Out of stock -> Cancel order immediately
                    order.setStatus(OrderStatus.CANCELLED, "Insufficient stock for " + order.getProductName());
                    outOfStockCancellations.incrementAndGet();
                    notificationService.notifyOrderCancelled(order, "Item out of stock");
                    continue;
                }

                // Step 2: Validate Payment (Simulate 92% success rate, 8% card declined)
                Thread.sleep(5); // Simulate gateway latency
                boolean paymentSuccess = (random.nextInt(100) >= 8);

                if (paymentSuccess) {
                    order.setStatus(OrderStatus.PAID, "Payment approved by " + workerName);
                    successfulPayments.incrementAndGet();
                    notificationService.notifyOrderConfirmed(order);

                    // Forward to warehouse packing queue (Producer)
                    packingQueue.put(order);
                } else {
                    // Payment Failed -> Trigger Order Cancellation & Stock Rollback (Task g)
                    order.setStatus(OrderStatus.CANCELLED, "Payment authorization declined");
                    failedPayments.incrementAndGet();

                    // CRITICAL: Release reserved stock back so other customers can buy it!
                    inventoryManager.releaseStock(order);
                    notificationService.notifyOrderCancelled(order, "Card payment declined");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static int getSuccessfulPayments() {
        return successfulPayments.get();
    }

    public static int getFailedPayments() {
        return failedPayments.get();
    }

    public static int getOutOfStockCancellations() {
        return outOfStockCancellations.get();
    }

    public static int getTotalCancelled() {
        return failedPayments.get() + outOfStockCancellations.get();
    }
}

