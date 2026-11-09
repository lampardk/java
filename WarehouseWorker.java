package Problem3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * WAREHOUSE WORKER (Task a, c, d)
 * ============================================================================
 * Represents one of the 5 concurrent warehouse workers.
 *
 * Responsibilities:
 * 1. Pulls paid orders from packingQueue (Consumer).
 * 2. Picks physical products from the warehouse shelves.
 * 3. Packages the parcel and labels it.
 * 4. Pushes packed order into the deliveryQueue for vehicle dispatch (Producer).
 */
public class WarehouseWorker implements Runnable {

    private final String workerName;
    private final BlockingQueue<CustomerOrder> packingQueue;
    private final BlockingQueue<CustomerOrder> deliveryQueue;

    private static final AtomicInteger totalOrdersFulfilled = new AtomicInteger(0);
    private volatile boolean keepRunning = true;

    public WarehouseWorker(String workerName,
                           BlockingQueue<CustomerOrder> packingQueue,
                           BlockingQueue<CustomerOrder> deliveryQueue) {
        this.workerName = workerName;
        this.packingQueue = packingQueue;
        this.deliveryQueue = deliveryQueue;
    }

    public void stopWorker() {
        this.keepRunning = false;
    }

    @Override
    public void run() {
        while (keepRunning || !packingQueue.isEmpty()) {
            try {
                CustomerOrder order = packingQueue.poll(50, TimeUnit.MILLISECONDS);
                if (order == null) continue;

                // Simulate picking and packaging time
                Thread.sleep(8);

                order.setStatus(OrderStatus.PACKED, "Picked & packaged by " + workerName);
                totalOrdersFulfilled.incrementAndGet();

                // Forward to logistics delivery queue
                deliveryQueue.put(order);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static int getTotalOrdersFulfilled() {
        return totalOrdersFulfilled.get();
    }
}

