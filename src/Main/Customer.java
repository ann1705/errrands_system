package Main;
import Config.config;
import java.util.Scanner;

public class Customer {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    private int loggedInUserId;
    
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
    
    public void viewRequest() {
        System.out.println("\n=== MY ERRANDS ===");
        String viewQuery = "SELECT r.r_id, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                          "FROM tbl_request r " +
                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                          "WHERE r.u_id = " + loggedInUserId;
        String[] requestHeaders = {"Request ID", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
        String[] requestColumns = {"r_id", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
        conf.viewRequest(viewQuery, requestHeaders, requestColumns);
    }
    
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
            System.out.println("3. Edit My Request");
            System.out.println("4. Cancel Errands");
            System.out.println("5. Logout");
            System.out.print("Enter Choice: ");
            int resp = sc.nextInt();
            sc.nextLine(); // Consume newline
            
            switch(resp){
                case 1:
                    System.out.println("\n=== REQUEST ERRANDS ===");
                    viewServices();
                    
                    System.out.print("\nEnter Service ID to Choose: ");
                    int servid = sc.nextInt();
                    sc.nextLine(); // Consume newline
                    
                    if(!isValidServiceId(servid)) {
                        System.out.println("Invalid Service ID. Please try again.");
                        break;
                    }
                    
                    System.out.println("\nSelected Service:");
                    getServiceDetails(servid);
                    
                    System.out.print("\nEnter Errand Description/Details: ");
                    String errandDetails = sc.nextLine();
                    
                    System.out.print("Enter Preferred Date (YYYY-MM-DD): ");
                    String preferredDate = sc.nextLine();
                    
                    System.out.print("Enter Preferred Time (HH:MM AM/PM): ");
                    String preferredTime = sc.nextLine();
                    
                    System.out.print("Enter Pickup/Service Location: ");
                    String location = sc.nextLine();
                    
                    System.out.println("\n=== CONFIRM YOUR REQUEST ===");
                    System.out.println("Customer ID: " + loggedInUserId);
                    System.out.println("Service ID: " + servid);
                    System.out.println("Details: " + errandDetails);
                    System.out.println("Date: " + preferredDate);
                    System.out.println("Time: " + preferredTime);
                    System.out.println("Location: " + location);
                    System.out.print("\nConfirm Request? (Y/N): ");
                    String confirm = sc.nextLine();
                    
                    if(confirm.equalsIgnoreCase("Y")) {
                        String insertQuery = "INSERT INTO tbl_request (u_id, s_id, r_details, r_date, r_time, r_location, r_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        int requestId = conf.addRecordAndReturnId(insertQuery, loggedInUserId, servid, errandDetails, preferredDate, preferredTime, location, "Pending");
                        
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
                    viewRequest();
                    break;
                    
                case 3:
                    System.out.println("\n=== EDIT MY REQUEST ===");
                    // Show only pending requests that can be edited
                    String editViewQuery = "SELECT r.r_id, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                                          "FROM tbl_request r " +
                                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                                          "WHERE r.u_id = " + loggedInUserId + " AND r.r_status = 'Pending'";
                    String[] editHeaders = {"Request ID", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
                    String[] editColumns = {"r_id", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
                    conf.viewRequest(editViewQuery, editHeaders, editColumns);
                    
                    System.out.print("\nEnter Request ID to Edit (0 to go back): ");
                    int editRequestId = sc.nextInt();
                    sc.nextLine(); // Consume newline
                    
                    if(editRequestId == 0) {
                        System.out.println("Cancelled.");
                        break;
                    }
                    
                    // Verify the request belongs to this user and is pending
                    String verifyEditQuery = "SELECT * FROM tbl_request WHERE r_id = ? AND u_id = ? AND r_status = 'Pending'";
                    java.util.List<java.util.Map<String, Object>> verifyEditResult = conf.fetchRecords(verifyEditQuery, editRequestId, loggedInUserId);
                    
                    if(verifyEditResult.isEmpty()) {
                        System.out.println("Invalid Request ID or you cannot edit this request.");
                        break;
                    }
                    
                    // Get current request details
                    java.util.Map<String, Object> currentRequest = verifyEditResult.get(0);
                    String currentDetails = currentRequest.get("r_details").toString();
                    String currentDate = currentRequest.get("r_date").toString();
                    String currentTime = currentRequest.get("r_time").toString();
                    String currentLocation = currentRequest.get("r_location").toString();
                    int currentServiceId = Integer.parseInt(currentRequest.get("s_id").toString());
                    
                    System.out.println("\n=== CURRENT REQUEST DETAILS ===");
                    System.out.println("Service ID: " + currentServiceId);
                    System.out.println("Details: " + currentDetails);
                    System.out.println("Date: " + currentDate);
                    System.out.println("Time: " + currentTime);
                    System.out.println("Location: " + currentLocation);
                    
                    System.out.println("\n=== WHAT TO EDIT? ===");
                    System.out.println("1. Service");
                    System.out.println("2. Details");
                    System.out.println("3. Date");
                    System.out.println("4. Time");
                    System.out.println("5. Location");
                    System.out.println("6. Edit All");
                    System.out.println("7. Cancel Edit");
                    System.out.print("Enter Choice: ");
                    int editChoice = sc.nextInt();
                    sc.nextLine(); // Consume newline
                    
                    String newDetails = currentDetails;
                    String newDate = currentDate;
                    String newTime = currentTime;
                    String newLocation = currentLocation;
                    int newServiceId = currentServiceId;
                    
                    switch(editChoice) {
                        case 1:
                            viewServices();
                            System.out.print("Enter New Service ID: ");
                            newServiceId = sc.nextInt();
                            sc.nextLine();
                            if(!isValidServiceId(newServiceId)) {
                                System.out.println("Invalid Service ID. Edit cancelled.");
                                break;
                            }
                            break;
                            
                        case 2:
                            System.out.print("Enter New Details: ");
                            newDetails = sc.nextLine();
                            break;
                            
                        case 3:
                            System.out.print("Enter New Date (YYYY-MM-DD): ");
                            newDate = sc.nextLine();
                            break;
                            
                        case 4:
                            System.out.print("Enter New Time (HH:MM AM/PM): ");
                            newTime = sc.nextLine();
                            break;
                            
                        case 5:
                            System.out.print("Enter New Location: ");
                            newLocation = sc.nextLine();
                            break;
                            
                        case 6:
                            viewServices();
                            System.out.print("Enter New Service ID: ");
                            newServiceId = sc.nextInt();
                            sc.nextLine();
                            if(!isValidServiceId(newServiceId)) {
                                System.out.println("Invalid Service ID. Edit cancelled.");
                                break;
                            }
                            System.out.print("Enter New Details: ");
                            newDetails = sc.nextLine();
                            System.out.print("Enter New Date (YYYY-MM-DD): ");
                            newDate = sc.nextLine();
                            System.out.print("Enter New Time (HH:MM AM/PM): ");
                            newTime = sc.nextLine();
                            System.out.print("Enter New Location: ");
                            newLocation = sc.nextLine();
                            break;
                            
                        case 7:
                            System.out.println("Edit cancelled.");
                            break;
                            
                        default:
                            System.out.println("Invalid choice. Edit cancelled.");
                            break;
                    }
                    
                    if(editChoice >= 1 && editChoice <= 6) {
                        System.out.println("\n=== CONFIRM CHANGES ===");
                        System.out.println("Service ID: " + newServiceId);
                        System.out.println("Details: " + newDetails);
                        System.out.println("Date: " + newDate);
                        System.out.println("Time: " + newTime);
                        System.out.println("Location: " + newLocation);
                        System.out.print("\nSave Changes? (Y/N): ");
                        String saveConfirm = sc.nextLine();
                        
                        if(saveConfirm.equalsIgnoreCase("Y")) {
                            String updateQuery = "UPDATE tbl_request SET s_id = ?, r_details = ?, r_date = ?, r_time = ?, r_location = ? WHERE r_id = ? AND u_id = ?";
                            conf.updateRequest(updateQuery, newServiceId, newDetails, newDate, newTime, newLocation, editRequestId, loggedInUserId);
                            System.out.println("✓ Request updated successfully!");
                        } else {
                            System.out.println("Changes discarded.");
                        }
                    }
                    break;
                    
                case 4:
                    System.out.println("\n=== CANCEL ERRANDS ===");
                    String cancelViewQuery = "SELECT r.r_id, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, r.r_status " +
                                            "FROM tbl_request r " +
                                            "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                                            "WHERE r.u_id = " + loggedInUserId + " AND r.r_status = 'Pending'";
                    String[] cancelHeaders = {"Request ID", "Service", "Details", "Date", "Time", "Location", "Status"};
                    String[] cancelColumns = {"r_id", "s_services", "r_details", "r_date", "r_time", "r_location", "r_status"};
                    conf.viewRequest(cancelViewQuery, cancelHeaders, cancelColumns);
                    
                    System.out.print("\nEnter Request ID to Cancel (0 to go back): ");
                    int requestId = sc.nextInt();
                    sc.nextLine(); // Consume newline
                    
                    if(requestId == 0) {
                        System.out.println("Cancelled.");
                        break;
                    }
                    
                    String verifyQuery = "SELECT * FROM tbl_request WHERE r_id = ? AND u_id = ? AND r_status = 'Pending'";
                    java.util.List<java.util.Map<String, Object>> verifyResult = conf.fetchRecords(verifyQuery, requestId, loggedInUserId);
                    
                    if(verifyResult.isEmpty()) {
                        System.out.println("Invalid Request ID or you don't have permission to cancel this request.");
                        break;
                    }
                    
                    System.out.print("Are you sure you want to cancel this request? (Y/N): ");
                    String cancelConfirm = sc.nextLine();
                    
                    if(cancelConfirm.equalsIgnoreCase("Y")) {
                        String updateQuery = "UPDATE tbl_request SET r_status = ? WHERE r_id = ? AND u_id = ?";
                        conf.updateRequest(updateQuery, "Cancelled", requestId, loggedInUserId);
                        System.out.println("✓ Request cancelled successfully!");
                    } else {
                        System.out.println("Cancellation aborted.");
                    }
                    break;
                    
                case 5:
                    System.out.println("Logging out...");
                    customerRunning = false;
                    break;
                    
                default:
                    System.out.println("Invalid Choice.");
                    break;
            }
        }
    }
}