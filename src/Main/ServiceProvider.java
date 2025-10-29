package Main;
import Config.config;
import java.util.Scanner;

public class ServiceProvider {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    private int loggedInUserId; // Store the logged-in service provider's ID
    
    // Constructor to set the user ID when they log in
    public ServiceProvider(int userId) {
        this.loggedInUserId = userId;
    }
    
    // View all pending customer requests
    public void viewRequest() {
        System.out.println("\n=== CUSTOMER REQUESTS ===");
        
        // View all pending requests from customers
        String viewQuery = "SELECT r.r_id, u.u_first_name, u.u_last_name, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                          "FROM tbl_request r " +
                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                          "INNER JOIN tbl_Users u ON r.u_id = u.u_id " +
                          "WHERE r.r_status = 'Pending'";
        String[] requestHeaders = {"Request ID", "First Name", "Last Name", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
        String[] requestColumns = {"r_id", "u_first_name", "u_last_name", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
        conf.viewServices(viewQuery, requestHeaders, requestColumns);
    }
    
    // View accepted requests (all accepted requests, not filtered by provider)
    private void viewAcceptedRequests() {
        System.out.println("\n=== ACCEPTED REQUESTS ===");
        
        String viewQuery = "SELECT r.r_id, u.u_first_name, u.u_last_name, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                          "FROM tbl_request r " +
                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                          "INNER JOIN tbl_Users u ON r.u_id = u.u_id " +
                          "WHERE r.r_status = 'Accepted'";
        String[] requestHeaders = {"Request ID", "First Name", "Last Name", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
        String[] requestColumns = {"r_id", "u_first_name", "u_last_name", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
        conf.viewServices(viewQuery, requestHeaders, requestColumns);
    }
    
    // View request history (completed and cancelled requests)
    private void viewRequestHistory() {
        System.out.println("\n=== REQUEST HISTORY ===");
        
        String viewQuery = "SELECT r.r_id, u.u_first_name, u.u_last_name, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                          "FROM tbl_request r " +
                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                          "INNER JOIN tbl_Users u ON r.u_id = u.u_id " +
                          "WHERE r.r_status = 'Completed' OR r.r_status = 'Cancelled'";
        String[] requestHeaders = {"Request ID", "First Name", "Last Name", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
        String[] requestColumns = {"r_id", "u_first_name", "u_last_name", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
        conf.viewServices(viewQuery, requestHeaders, requestColumns);
    }
    
    // Validate if request exists and is in correct status
    private boolean isValidRequest(int requestId, String expectedStatus) {
        String query = "SELECT * FROM tbl_request WHERE r_id = ? AND r_status = ?";
        try {
            java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(query, requestId, expectedStatus);
            return !result.isEmpty();
        } catch (Exception e) {
            System.out.println("Error validating request ID: " + e.getMessage());
            return false;
        }
    }
    
    public void ServiceProvider() {
        boolean providerRunning = true;
        while(providerRunning){
            System.out.println("\n===== SERVICE PROVIDER DASHBOARD =====");
            System.out.println("1. View Customer's Request");
            System.out.println("2. Accept Request");
            System.out.println("3. Mark Request Completed");
            System.out.println("4. View Request History");
            System.out.println("5. Delete Request History");
            System.out.println("6. Logout");
            System.out.print("Enter Choice: ");
            int provResp = sc.nextInt();
            
            switch(provResp){
                case 1:
                    viewRequest();
                    break;
                    
                case 2:
                    System.out.println("\n=== ACCEPT REQUEST ===");
                    viewRequest(); // Show pending requests
                    
                    System.out.print("\nEnter Request ID to Accept (0 to go back): ");
                    int reqId = sc.nextInt();
                    
                    if(reqId == 0) {
                        break;
                    }
                    
                    // Validate request is pending
                    if(!isValidRequest(reqId, "Pending")) {
                        System.out.println("Invalid Request ID or request is not pending.");
                        break;
                    }
                    
                    System.out.print("Confirm acceptance of Request ID " + reqId + "? (Y/N): ");
                    String acceptConfirm = sc.next();
                    
                    if(acceptConfirm.equalsIgnoreCase("Y")) {
                        // Update request status to Accepted
                        String updateQuery = "UPDATE tbl_request SET r_status = 'Accepted' WHERE r_id = ?";
                        conf.updateRequest(updateQuery, reqId);
                        System.out.println("✓ Request accepted successfully!");
                    } else {
                        System.out.println("Request acceptance cancelled.");
                    }
                    break;
                    
                case 3:
                    System.out.println("\n=== MARK REQUEST COMPLETED ===");
                    viewAcceptedRequests(); // Show accepted requests
                    
                    System.out.print("\nEnter Request ID to Mark as Complete (0 to go back): ");
                    int comId = sc.nextInt();
                    
                    if(comId == 0) {
                        break;
                    }
                    
                    // Verify the request is accepted
                    if(!isValidRequest(comId, "Accepted")) {
                        System.out.println("Invalid Request ID or request is not in accepted status.");
                        break;
                    }
                    
                    System.out.print("Confirm completion of Request ID " + comId + "? (Y/N): ");
                    String completeConfirm = sc.next();
                    
                    if(completeConfirm.equalsIgnoreCase("Y")) {
                        String updateQuery = "UPDATE tbl_request SET r_status = 'Completed' WHERE r_id = ?";
                        conf.updateRequest(updateQuery, comId);
                        System.out.println("✓ Request marked as completed!");
                    } else {
                        System.out.println("Completion cancelled.");
                    }
                    break;
                    
                case 4:
                    viewRequestHistory();
                    break;
                    
                case 5:
                    System.out.println("\n=== DELETE REQUEST HISTORY ===");
                    viewRequestHistory(); // Show completed/cancelled requests
                    
                    System.out.print("\nEnter Request ID to Delete (0 to go back): ");
                    int hisId = sc.nextInt();
                    
                    if(hisId == 0) {
                        break;
                    }
                    
                    // Verify the request is completed or cancelled
                    String verifyDeleteQuery = "SELECT * FROM tbl_request WHERE r_id = ? AND (r_status = 'Completed' OR r_status = 'Cancelled')";
                    java.util.List<java.util.Map<String, Object>> verifyDeleteResult = conf.fetchRecords(verifyDeleteQuery, hisId);
                    
                    if(verifyDeleteResult.isEmpty()) {
                        System.out.println("Invalid Request ID or request is not completed/cancelled.");
                        break;
                    }
                    
                    System.out.print("Are you sure you want to delete Request ID " + hisId + "? (Y/N): ");
                    String deleteConfirm = sc.next();
                    
                    if(deleteConfirm.equalsIgnoreCase("Y")) {
                        String deleteQuery = "DELETE FROM tbl_request WHERE r_id = ?";
                        conf.updateRequest(deleteQuery, hisId);
                        System.out.println("✓ Request deleted successfully!");
                    } else {
                        System.out.println("Deletion cancelled.");
                    }
                    break;
                    
                case 6:
                    System.out.println("Logging out...");
                    providerRunning = false;
                    break;
                    
                default:
                    System.out.println("Invalid Choice.");
            }
        }
    }
}