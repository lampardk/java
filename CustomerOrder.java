package Problem3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * CUSTOMER ORDER CLASS (Task a)
 * ============================================================================
 * Represents a customer's purchase request.
 * Contains order details, tracking state, and an audit trail of status changes.
 */
public class CustomerOrder {

    private final int orderId;
    private final String customerId;
    private final String productName; // "Product A" or "Product B"
    private final int quantity;

    private volatile OrderStatus status;
    private int assignedVehicleId = -1;

    // Thread-safe history of state transitions for auditing
    private final List<String> statusHistory = Collections.synchronizedList(new ArrayList<>());

    public CustomerOrder(int orderId, String customerId, String productName, int quantity) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productName = productName;
        this.quantity = quantity;
        this.status = OrderStatus.SUBMITTED;
        addHistory("Order created for " + quantity + " unit(s) of " + productName);
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(OrderStatus newStatus, String reason) {
        this.status = newStatus;
        addHistory("Status -> " + newStatus + (reason != null ? " (" + reason + ")" : ""));
    }

    public int getAssignedVehicleId() {
        return assignedVehicleId;
    }

    public void setAssignedVehicleId(int vehicleId) {
        this.assignedVehicleId = vehicleId;
    }

    private void addHistory(String note) {
        statusHistory.add(note);
    }

    public List<String> getStatusHistory() {
        return new ArrayList<>(statusHistory);
    }

    @Override
    public String toString() {
        return "Order #" + orderId + " [" + customerId + " | " + quantity + "x "
                + productName + " | " + status + "]";
    }
}

