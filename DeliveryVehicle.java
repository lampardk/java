package Problem3;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * DELIVERY VEHICLE CLASS (Task a)
 * ============================================================================
 * Represents one of the 4 delivery vehicles owned by the logistics network.
 * Tracks total trips completed, mileage, and delivery statistics.
 */
public class DeliveryVehicle {

    private final int vehicleId;
    private final String licensePlate;
    private final AtomicInteger tripsCompleted = new AtomicInteger(0);

    public DeliveryVehicle(int vehicleId, String licensePlate) {
        this.vehicleId = vehicleId;
        this.licensePlate = licensePlate;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void incrementTrips() {
        tripsCompleted.incrementAndGet();
    }

    public int getTripsCompleted() {
        return tripsCompleted.get();
    }

    @Override
    public String toString() {
        return "Vehicle #" + vehicleId + " (" + licensePlate + ") - Trips: " + tripsCompleted.get();
    }
}

