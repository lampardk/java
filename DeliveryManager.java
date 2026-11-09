package Problem3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * DELIVERY MANAGER CLASS (Task a, c, e)
 * ============================================================================
 * Manages logistics and the fleet of 4 delivery vehicles.
 *
 * Concurrency Mechanism (Task e):
 * - Uses a Semaphore with 4 permits (new Semaphore(4, true)) to strictly
 *   restrict concurrent deliveries to the 4 vehicles.
 * - When all 4 vehicles are on the road, additional dispatch attempts wait
 *   until a vehicle returns and releases its permit.
 */
public class DeliveryManager {

    private final List<DeliveryVehicle> vehicleFleet = new ArrayList<>();
    // Pool of idle vehicle references
    private final ConcurrentLinkedQueue<DeliveryVehicle> idleVehicles = new ConcurrentLinkedQueue<>();

    // Semaphore restricting concurrent access to the 4 vehicles (Task e)
    private final Semaphore vehicleSemaphore = new Semaphore(4, true);

    private final NotificationService notificationService;
    private final AtomicInteger totalOrdersDelivered = new AtomicInteger(0);

    public DeliveryManager(NotificationService notificationService) {
        this.notificationService = notificationService;

        // Create the 4 delivery vehicles
        for (int i = 1; i <= 4; i++) {
            DeliveryVehicle vehicle = new DeliveryVehicle(i, "KDD-" + (100 + i) + "X");
            vehicleFleet.add(vehicle);
            idleVehicles.add(vehicle);
        }
    }

    /**
     * Dispatches an order for delivery using one of the 4 vehicles.
     * Controlled by the Semaphore.
     */
    public boolean deliverOrder(CustomerOrder order) {
        try {
            // Step 1: Acquire vehicle permit from Semaphore (Task e)
            vehicleSemaphore.acquire();

            // Step 2: Grab an available vehicle
            DeliveryVehicle vehicle = idleVehicles.poll();
            if (vehicle == null) {
                // Failsafe
                vehicleSemaphore.release();
                return false;
            }

            order.setAssignedVehicleId(vehicle.getVehicleId());
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY, "Loaded into Vehicle #" + vehicle.getVehicleId());
            notificationService.notifyOrderDispatched(order, vehicle.getVehicleId());

            // Step 3: Simulate transit time
            Thread.sleep(15);

            // Step 4: Mark delivered
            order.setStatus(OrderStatus.DELIVERED, "Delivered by Vehicle #" + vehicle.getVehicleId());
            vehicle.incrementTrips();
            totalOrdersDelivered.incrementAndGet();
            notificationService.notifyOrderDelivered(order);

            // Step 5: Return vehicle to idle pool and release permit
            idleVehicles.add(vehicle);
            vehicleSemaphore.release();
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public int getTotalOrdersDelivered() {
        return totalOrdersDelivered.get();
    }

    public List<DeliveryVehicle> getVehicleFleet() {
        return vehicleFleet;
    }

    public void printVehicleUtilizationReport() {
        System.out.println("\n===== DELIVERY VEHICLE UTILIZATION REPORT =====");
        int totalTrips = 0;
        for (DeliveryVehicle vehicle : vehicleFleet) {
            System.out.println("  " + vehicle);
            totalTrips += vehicle.getTripsCompleted();
        }
        System.out.println("Total Delivery Trips Completed: " + totalTrips);
        System.out.println("================================================");
    }
}

