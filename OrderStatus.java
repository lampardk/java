package Problem3;

/**
 * ============================================================================
 * ORDER STATUS ENUM
 * ============================================================================
 * Represents the lifecycle states of an e-commerce order as it moves through
 * the concurrent processing pipeline.
 */
public enum OrderStatus {
    SUBMITTED,           // Order placed by customer
    PAYMENT_PENDING,     // Being checked by payment processor
    PAID,                // Payment successfully validated
    PAYMENT_FAILED,      // Payment was declined (triggers cancellation)
    INSUFFICIENT_STOCK,  // Out of stock (triggers cancellation)
    PACKED,              // Packaged by warehouse worker
    OUT_FOR_DELIVERY,    // Loaded into delivery vehicle
    DELIVERED,           // Successfully delivered to customer
    CANCELLED            // Cancelled and any reserved stock released
}

