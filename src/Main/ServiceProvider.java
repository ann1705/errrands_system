package Main;
import Config.config;
import java.util.Scanner;

public class ServiceProvider {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    private int loggedInUserId;
    
    public ServiceProvider(int userId) {
        this.loggedInUserId = userId;
    }
    
    // View all pending customer requests
    public void viewRequest() {
        System.out.println("\n=== CUSTOMER REQUESTS ===");
        
        String viewQuery = "SELECT r.r_id, u.u_first_name, u.u_last_name, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                          "FROM tbl_request r " +
                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                          "INNER JOIN tbl_users u ON r.u_id = u.u_id " +
                          "WHERE r.r_status = 'Pending'";
        String[] requestHeaders = {"Request ID", "First Name", "Last Name", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
        String[] requestColumns = {"r_id", "u_first_name", "u_last_name", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
        conf.viewRequest(viewQuery, requestHeaders, requestColumns);
    }
    
    // View accepted requests
    private void viewAcceptedRequests() {
        System.out.println("\n=== ACCEPTED REQUESTS ===");
        
        String viewQuery = "SELECT r.r_id, u.u_first_name, u.u_last_name, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                          "FROM tbl_request r " +
                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                          "INNER JOIN tbl_users u ON r.u_id = u.u_id " +
                          "WHERE r.r_status = 'Accepted'";
        String[] requestHeaders = {"Request ID", "First Name", "Last Name", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
        String[] requestColumns = {"r_id", "u_first_name", "u_last_name", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
        conf.viewRequest(viewQuery, requestHeaders, requestColumns);
    }
    
    // View request history (completed and cancelled requests)
    private void viewRequestHistory() {
        System.out.println("\n=== REQUEST HISTORY ===");
        
        String viewQuery = "SELECT r.r_id, u.u_first_name, u.u_last_name, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                          "FROM tbl_request r " +
                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                          "INNER JOIN tbl_users u ON r.u_id = u.u_id " +
                          "WHERE r.r_status = 'Completed' OR r.r_status = 'Cancelled'";
        String[] requestHeaders = {"Request ID", "First Name", "Last Name", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
        String[] requestColumns = {"r_id", "u_first_name", "u_last_name", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
        conf.viewRequest(viewQuery, requestHeaders, requestColumns);
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
            sc.nextLine(); // Consume newline
            
            switch(provResp){
                case 1:
                    viewRequest();
                    break;
                    
                case 2:
                    System.out.println("\n=== ACCEPT REQUEST ===");
                    
                    int acceptAttempts = 0;
                    int maxAcceptAttempts = 3;
                    boolean validAcceptId = false;
                    
                    while (acceptAttempts < maxAcceptAttempts && !validAcceptId) {
                        viewRequest();
                        
                        System.out.print("\nEnter Request ID to Accept (Attempt " + (acceptAttempts + 1) + " of " + maxAcceptAttempts + ", or 0 to cancel): ");
                        int reqId = sc.nextInt();
                        sc.nextLine(); // Consume newline
                        
                        if(reqId == 0) {
                            System.out.println("Cancelled.");
                            validAcceptId = true; // Exit the loop
                            break;
                        }
                        
                        // Validate request is pending
                        if(isValidRequest(reqId, "Pending")) {
                            System.out.print("Confirm acceptance of Request ID " + reqId + "? (Y/N): ");
                            String acceptConfirm = sc.nextLine();
                            
                            if(acceptConfirm.equalsIgnoreCase("Y")) {
                                String updateQuery = "UPDATE tbl_request SET r_status = ? WHERE r_id = ?";
                                conf.updateRequest(updateQuery, "Accepted", reqId);
                                System.out.println("✓ Request accepted successfully!");
                                validAcceptId = true;
                            } else {
                                System.out.println("Request acceptance cancelled.");
                                validAcceptId = true; // Exit after user cancels confirmation
                            }
                        } else {
                            acceptAttempts++;
                            if (acceptAttempts < maxAcceptAttempts) {
                                System.out.println("Error: Invalid Request ID or request is not pending. Please try again.");
                            } else {
                                System.out.println("Error: Maximum attempts reached. Returning to dashboard...");
                            }
                        }
                    }
                    break;
                    
                case 3:
                    System.out.println("\n=== MARK REQUEST COMPLETED ===");
                    
                    int completeAttempts = 0;
                    int maxCompleteAttempts = 3;
                    boolean validCompleteId = false;
                    
                    while (completeAttempts < maxCompleteAttempts && !validCompleteId) {
                        viewAcceptedRequests();
                        
                        System.out.print("\nEnter Request ID to Mark as Complete (Attempt " + (completeAttempts + 1) + " of " + maxCompleteAttempts + ", or 0 to cancel): ");
                        int comId = sc.nextInt();
                        sc.nextLine(); // Consume newline
                        
                        if(comId == 0) {
                            System.out.println("Cancelled.");
                            validCompleteId = true; // Exit the loop
                            break;
                        }
                        
                        // Verify the request is accepted
                        if(isValidRequest(comId, "Accepted")) {
                            System.out.print("Confirm completion of Request ID " + comId + "? (Y/N): ");
                            String completeConfirm = sc.nextLine();
                            
                            if(completeConfirm.equalsIgnoreCase("Y")) {
                                String updateQuery = "UPDATE tbl_request SET r_status = ? WHERE r_id = ?";
                                conf.updateRequest(updateQuery, "Completed", comId);
                                System.out.println("✓ Request marked as completed!");
                                validCompleteId = true;
                            } else {
                                System.out.println("Completion cancelled.");
                                validCompleteId = true; // Exit after user cancels confirmation
                            }
                        } else {
                            completeAttempts++;
                            if (completeAttempts < maxCompleteAttempts) {
                                System.out.println("Error: Invalid Request ID or request is not in accepted status. Please try again.");
                            } else {
                                System.out.println("Error: Maximum attempts reached. Returning to dashboard...");
                            }
                        }
                    }
                    break;
                    
                case 4:
                    viewRequestHistory();
                    break;
                    
                case 5:
                    System.out.println("\n=== DELETE REQUEST HISTORY ===");
                    
                    int deleteAttempts = 0;
                    int maxDeleteAttempts = 3;
                    boolean validDeleteId = false;
                    
                    while (deleteAttempts < maxDeleteAttempts && !validDeleteId) {
                        viewRequestHistory();
                        
                        System.out.print("\nEnter Request ID to Delete (Attempt " + (deleteAttempts + 1) + " of " + maxDeleteAttempts + ", or 0 to cancel): ");
                        int hisId = sc.nextInt();
                        sc.nextLine(); // Consume newline
                        
                        if(hisId == 0) {
                            System.out.println("Cancelled.");
                            validDeleteId = true; // Exit the loop
                            break;
                        }
                        
                        // Verify the request is completed or cancelled
                        String verifyDeleteQuery = "SELECT * FROM tbl_request WHERE r_id = ? AND (r_status = 'Completed' OR r_status = 'Cancelled')";
                        java.util.List<java.util.Map<String, Object>> verifyDeleteResult = conf.fetchRecords(verifyDeleteQuery, hisId);
                        
                        if(!verifyDeleteResult.isEmpty()) {
                            System.out.print("Are you sure you want to delete Request ID " + hisId + "? (Y/N): ");
                            String deleteConfirm = sc.nextLine();
                            
                            if(deleteConfirm.equalsIgnoreCase("Y")) {
                                String deleteQuery = "DELETE FROM tbl_request WHERE r_id = ?";
                                conf.deleteRequest(deleteQuery, hisId);
                                System.out.println("✓ Request deleted successfully!");
                                validDeleteId = true;
                            } else {
                                System.out.println("Deletion cancelled.");
                                validDeleteId = true; // Exit after user cancels confirmation
                            }
                        } else {
                            deleteAttempts++;
                            if (deleteAttempts < maxDeleteAttempts) {
                                System.out.println("Error: Invalid Request ID or request is not completed/cancelled. Please try again.");
                            } else {
                                System.out.println("Error: Maximum attempts reached. Returning to dashboard...");
                            }
                        }
                    }
                    break;
                    
                case 6:
                    System.out.println("Logging out...");
                    providerRunning = false;
                    break;
                    
                default:
                    System.out.println("Invalid Choice.");
                    break;
            }
        }
    }
}