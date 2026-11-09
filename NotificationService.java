package Problem3;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * NOTIFICATION SERVICE CLASS (Task a, c)
 * ============================================================================
 * Sends real-time notifications to customers when their orders are:
 * 1. Confirmed / Payment approved
 * 2. Dispatched for delivery
 * 3. Successfully delivered
 * 4. Cancelled / Refunded
 */
public class NotificationService {

    private final AtomicInteger totalNotificationsSent = new AtomicInteger(0);

    public void notifyOrderConfirmed(CustomerOrder order) {
        totalNotificationsSent.incrementAndGet();
        // In a real system, this sends an SMS or Email
        // System.out.println("[SMS] " + order.getCustomerId() + ": Order #" + order.getOrderId() + " confirmed & paid.");
    }

    public void notifyOrderDispatched(CustomerOrder order, int vehicleId) {
        totalNotificationsSent.incrementAndGet();
        System.out.println("[SMS to " + order.getCustomerId() + "] Order #" + order.getOrderId()
                + " is OUT FOR DELIVERY in Vehicle #" + vehicleId + "!");
    }

    public void notifyOrderDelivered(CustomerOrder order) {
        totalNotificationsSent.incrementAndGet();
        System.out.println("[SMS to " + order.getCustomerId() + "] Order #" + order.getOrderId()
                + " has been successfully DELIVERED! Thank you for shopping with us.");
    }

    public void notifyOrderCancelled(CustomerOrder order, String reason) {
        totalNotificationsSent.incrementAndGet();
        System.out.println("[SMS to " + order.getCustomerId() + "] Order #" + order.getOrderId()
                + " was CANCELLED (" + reason + "). Any reserved funds or stock have been released.");
    }

    public int getTotalNotificationsSent() {
        return totalNotificationsSent.get();
    }
}

