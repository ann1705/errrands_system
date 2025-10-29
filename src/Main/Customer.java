package Main;
import Config.config;
import java.util.Scanner;

public class Customer {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    private int loggedInUserId; // Store the logged-in user's ID
    
    // Constructor to set the user ID when they log in
    public Customer(int userId) {
        this.loggedInUserId = userId;
    }
    
    public static void viewServices() {
        String Query = "SELECT * FROM tbl_services";
        String[] userHeaders = {"ID", "Service", "Description", "Price"};
        String[] userColumns = {"s_id", "s_services", "s_description", "s_price"};
        config conf = new config();
        conf.viewServices(Query, userHeaders, userColumns);
    }
    
    // Method to validate if service ID exists - FIXED VERSION
    private boolean isValidServiceId(int serviceId) {
        String query = "SELECT * FROM tbl_services WHERE s_id = ?";
        try {
            java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(query, serviceId);
            return !result.isEmpty();
        } catch (Exception e) {
            System.out.println("Error validating service ID: " + e.getMessage());
            return false;
        }
    }
    
    // Fixed viewRequest as a proper instance method
    public void viewRequest() {
        System.out.println("\n=== MY ERRANDS ===");
        // View only the logged-in user's requests with all details
        String viewQuery = "SELECT r.r_id, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                          "FROM tbl_request r " +
                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                          "WHERE r.u_id = " + loggedInUserId;
        String[] requestHeaders = {"Request ID", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
        String[] requestColumns = {"r_id", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
        conf.viewRequest(viewQuery, requestHeaders, requestColumns);
    }
    
    // Method to get service details
    private void getServiceDetails(int serviceId) {
        String query = "SELECT * FROM tbl_services WHERE s_id = " + serviceId;
        String[] headers = {"ID", "Service", "Description", "Price"};
        String[] columns = {"s_id", "s_services", "s_description", "s_price"};
        conf.viewServices(query, headers, columns);
    }
    
    public void Customer(){
        boolean customerRunning = true;
        while(customerRunning){
            System.out.println("\n===== CUSTOMER DASHBOARD =====");
            System.out.println("1. Request Errands");
            System.out.println("2. View My Errands");
            System.out.println("3. Cancel Errands");
            System.out.println("4. Logout");
            System.out.print("Enter Choice: ");
            int resp = sc.nextInt();
            
            switch(resp){
                case 1:
                    System.out.println("\n=== REQUEST ERRANDS ===");
                    viewServices();
                    
                    System.out.print("\nEnter Service ID to Choose: ");
                    int servid = sc.nextInt();
                    
                    // Validate service ID
                    if(!isValidServiceId(servid)) {
                        System.out.println("Invalid Service ID. Please try again.");
                        break;
                    }
                    
                    // Show selected service
                    System.out.println("\nSelected Service:");
                    getServiceDetails(servid);
                    
                    // Get additional details for the errand request
                    sc.nextLine(); // Consume newline
                    
                    System.out.print("\nEnter Errand Description/Details: ");
                    String errandDetails = sc.nextLine();
                    
                    System.out.print("Enter Preferred Date (YYYY-MM-DD): ");
                    String preferredDate = sc.nextLine();
                    
                    System.out.print("Enter Preferred Time (HH:MM AM/PM): ");
                    String preferredTime = sc.nextLine();
                    
                    System.out.print("Enter Pickup/Service Location: ");
                    String location = sc.nextLine();
                    
                    // Confirm the request
                    System.out.println("\n=== CONFIRM YOUR REQUEST ===");
                    System.out.println("Customer ID: " + loggedInUserId);
                    System.out.println("Service ID: " + servid);
                    System.out.println("Details: " + errandDetails);
                    System.out.println("Date: " + preferredDate);
                    System.out.println("Time: " + preferredTime);
                    System.out.println("Location: " + location);
                    System.out.print("\nConfirm Request? (Y/N): ");
                    String confirm = sc.next();
                    
                    if(confirm.equalsIgnoreCase("Y")) {
                        // Insert into tbl_request with all details
                        String insertQuery = "INSERT INTO tbl_request (u_id, s_id, r_details, r_date, r_time, r_location, r_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        
                        int requestId = conf.addRecordAndReturnId(insertQuery, loggedInUserId,servid,errandDetails,preferredDate,preferredTime,location,"Pending");
                        
                        if(requestId > 0) {
                            System.out.println("\n✓ Errand Request Submitted Successfully!");
                            System.out.println("Your Request ID: " + requestId);
                            System.out.println("Status: Pending");
                        } else {
                            System.out.println("✗ Failed to submit errand request. Please try again.");
                        }
                    } else {
                        System.out.println("Request cancelled.");
                    }
                    break;
                    
                case 2:
                    viewRequest(); // Now properly calls the viewRequest method
                    break;
                    
                case 3:
                    System.out.println("\n=== CANCEL ERRANDS ===");
                    // Show only user's pending requests with details
                    String cancelViewQuery = "SELECT r.r_id, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, r.r_status " +
                                            "FROM tbl_request r " +
                                            "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                                            "WHERE r.u_id = " + loggedInUserId + " AND r.r_status = 'Pending'";
                    String[] cancelHeaders = {"Request ID", "Service", "Details", "Date", "Time", "Location", "Status"};
                    String[] cancelColumns = {"r_id", "s_services", "r_details", "r_date", "r_time", "r_location", "r_status"};
                    conf.cancelRequest(cancelViewQuery, cancelHeaders, cancelColumns);
                    
                    System.out.print("\nEnter Request ID to Cancel (0 to go back): ");
                    int requestId = sc.nextInt();
                    
                    if(requestId == 0) {
                        break;
                    }
                    
                    // Verify the request belongs to this user
                    String verifyQuery = "SELECT * FROM tbl_request WHERE r_id = ? AND u_id = ? AND r_status = 'Pending'";
                    java.util.List<java.util.Map<String, Object>> verifyResult = conf.fetchRecords(verifyQuery, requestId, loggedInUserId);
                    
                    if(verifyResult.isEmpty()) {
                        System.out.println("Invalid Request ID or you don't have permission to cancel this request.");
                        break;
                    }
                    
                    System.out.print("Are you sure you want to cancel this request? (Y/N): ");
                    String cancelConfirm = sc.next();
                    
                    if(cancelConfirm.equalsIgnoreCase("Y")) {
                        String updateQuery = "UPDATE tbl_request SET r_status = 'Cancelled' WHERE r_id = ? AND u_id = ?";
                        conf.updateRequest(updateQuery, requestId, loggedInUserId);
                        System.out.println("✓ Request cancelled successfully!");
                    } else {
                        System.out.println("Cancellation aborted.");
                    }
                    break;
                    
                case 4:
                    System.out.println("Logging out...");
                    customerRunning = false;
                    break;
                    
                default:
                    System.out.println("Invalid Choice.");
            }
        }
    }
}