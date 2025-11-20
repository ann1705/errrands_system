package Main;
import Config.config;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            List<Map<String, Object>> result = conf.fetchRecords(query, serviceId);
            return !result.isEmpty();
        } catch (Exception e) {
            System.out.println("Error validating service ID: " + e.getMessage());
            return false;
        }
    }
    
    private Map<String, Object> getServiceInfo(int serviceId) {
        String query = "SELECT * FROM tbl_services WHERE s_id = ?";
        try {
            List<Map<String, Object>> result = conf.fetchRecords(query, serviceId);
            if (!result.isEmpty()) {
                return result.get(0);
            }
        } catch (Exception e) {
            System.out.println("Error fetching service info: " + e.getMessage());
        }
        return null;
    }
    
    private String getServiceNamesByIds(String serviceIds) {
        if(serviceIds == null || serviceIds.isEmpty()) {
            return "N/A";
        }
        
        String[] ids = serviceIds.split(",");
        StringBuilder idList = new StringBuilder();
        
        for(int i = 0; i < ids.length; i++) {
            if(i > 0) idList.append(",");
            idList.append(ids[i].trim());
        }
        
        // Fetch all services in ONE query
        String query = "SELECT s_services FROM tbl_services WHERE s_id IN (" + idList.toString() + ")";
        try {
            List<Map<String, Object>> results = conf.fetchRecords(query);
            if(results.isEmpty()) {
                return "N/A";
            }
            
            StringBuilder serviceNames = new StringBuilder();
            for(int i = 0; i < results.size(); i++) {
                if(i > 0) serviceNames.append(", ");
                serviceNames.append(results.get(i).get("s_services"));
            }
            
            return serviceNames.toString();
        } catch(Exception e) {
            return "N/A";
        }
    }
    
    private double calculateTotalPrice(String serviceIds) {
        if(serviceIds == null || serviceIds.isEmpty()) {
            return 0.0;
        }
        
        String[] ids = serviceIds.split(",");
        
        // Build query to get all prices at once
        StringBuilder idList = new StringBuilder();
        for(int i = 0; i < ids.length; i++) {
            if(i > 0) idList.append(",");
            idList.append(ids[i].trim());
        }
        
        String query = "SELECT SUM(s_price) as total FROM tbl_services WHERE s_id IN (" + idList.toString() + ")";
        try {
            List<Map<String, Object>> result = conf.fetchRecords(query);
            if(!result.isEmpty() && result.get(0).get("total") != null) {
                return Double.parseDouble(result.get(0).get("total").toString());
            }
        } catch(Exception e) {
            // If error, return 0
        }
        
        return 0.0;
    }
    
    public void viewRequest() {
        System.out.println("\n" + new String(new char[72]).replace('\0', '='));
        System.out.println("MY ERRANDS");
        System.out.println(new String(new char[72]).replace('\0', '='));
        
        String viewQuery = "SELECT r_id, s_id, r_details, r_date, r_time, r_location, r_total_price, r_status " +
                          "FROM tbl_request WHERE u_id = " + loggedInUserId;
        
        try {
            List<Map<String, Object>> requests = conf.fetchRecords(viewQuery);
            
            if(requests.isEmpty()) {
                System.out.println("No errands found.");
                return;
            }
            
            System.out.printf("%-5s %-25s %-15s %-12s %-12s %-15s %-10s%n", 
                "ID", "Services", "Date", "Time", "Total", "Location", "Status");
            System.out.println(new String(new char[100]).replace('\0', '-'));
            
            for(Map<String, Object> row : requests) {
                int rid = Integer.parseInt(row.get("r_id").toString());
                String serviceIds = row.get("s_id") != null ? row.get("s_id").toString() : "";
                String date = row.get("r_date").toString();
                String time = row.get("r_time").toString();
                String location = row.get("r_location").toString();
                String status = row.get("r_status").toString();
                
                // Get total price from database
                double total = 0.0;
                if(row.get("r_total_price") != null) {
                    total = Double.parseDouble(row.get("r_total_price").toString());
                }
                
                String services = getServiceNamesByIds(serviceIds);
                
                // Truncate long text
                if(services.length() > 23) services = services.substring(0, 20) + "...";
                if(location.length() > 13) location = location.substring(0, 10) + "...";
                
                System.out.printf("%-5d %-25s %-15s %-12s PHP %-8.2f %-15s %-10s%n", 
                    rid, services, date, time, total, location, status);
            }
        } catch (Exception e) {
            System.out.println("Error viewing requests: " + e.getMessage());
        }
    }
    
    private void displaySelectedServices(List<Integer> serviceIds) {
        if (serviceIds.isEmpty()) {
            System.out.println("No services selected yet.");
            return;
        }
        
        System.out.println("\n" + new String(new char[72]).replace('\0', '-'));
        System.out.println("SELECTED SERVICES");
        System.out.println(new String(new char[72]).replace('\0', '-'));
        System.out.printf("%-4s %-30s %-20s %-15s%n", "No", "Service Name", "Description", "Price");
        System.out.println(new String(new char[72]).replace('\0', '-'));
        
        double totalPrice = 0.0;
        int counter = 1;
        
        for (int serviceId : serviceIds) {
            Map<String, Object> service = getServiceInfo(serviceId);
            if (service != null) {
                String serviceName = service.get("s_services").toString();
                String description = service.get("s_description").toString();
                double price = Double.parseDouble(service.get("s_price").toString());
                
                // Truncate long text for display
                if (serviceName.length() > 28) serviceName = serviceName.substring(0, 25) + "...";
                if (description.length() > 18) description = description.substring(0, 15) + "...";
                
                System.out.printf("%-4d %-30s %-20s PHP %8.2f%n", 
                    counter++, serviceName, description, price);
                totalPrice += price;
            }
        }
        
        System.out.println(new String(new char[72]).replace('\0', '-'));
        System.out.printf("TOTAL COST: PHP %.2f%n", totalPrice);
        System.out.println(new String(new char[72]).replace('\0', '-'));
    }
    
    public void Customer(){
        boolean customerRunning = true;
        while(customerRunning){
            System.out.println("\n" + new String(new char[72]).replace('\0', '='));
            System.out.println("CUSTOMER DASHBOARD");
            System.out.println(new String(new char[72]).replace('\0', '='));
            System.out.println("1. Request Errands");
            System.out.println("2. View My Errands");
            System.out.println("3. Edit My Request");
            System.out.println("4. Cancel Errands");
            System.out.println("5. Logout");
            System.out.println(new String(new char[72]).replace('\0', '='));
            System.out.print("Enter Choice: ");
            int resp = sc.nextInt();
            sc.nextLine();
            
            switch(resp){
                case 1:
                    System.out.println("\n" + new String(new char[72]).replace('\0', '='));
                    System.out.println("REQUEST ERRANDS");
                    System.out.println(new String(new char[72]).replace('\0', '='));
                    viewServices();
                    
                    // Ask how many services they want to add
                    int numberOfServices = 0;
                    while(numberOfServices < 1) {
                        System.out.print("\nHow many services would you like to add? ");
                        numberOfServices = sc.nextInt();
                        sc.nextLine();
                        
                        if(numberOfServices < 1) {
                            System.out.println("✗ You must add at least 1 service!");
                        }
                    }
                    
                    List<Integer> selectedServices = new ArrayList<>();
                    
                    // Allow user to select the specified number of services
                    int invalidAttempts = 0;
                    int maxInvalidAttempts = 3;
                    
                    for(int i = 1; i <= numberOfServices; i++) {
                        boolean validSelection = false;
                        
                        while(!validSelection) {
                            System.out.print("\nEnter Service ID for Service #" + i + ": ");
                            int servid = sc.nextInt();
                            sc.nextLine();
                            
                            if(!isValidServiceId(servid)) {
                                invalidAttempts++;
                                System.out.println("✗ Invalid Service ID. Please try again. (" + invalidAttempts + "/" + maxInvalidAttempts + ")");
                                
                                if(invalidAttempts >= maxInvalidAttempts) {
                                    System.out.println("\n⚠ Maximum invalid attempts reached. Returning to dashboard...");
                                    break;
                                }
                            } else if(selectedServices.contains(servid)) {
                                invalidAttempts++;
                                System.out.println("⚠ Service already added to your request! Choose a different service. (" + invalidAttempts + "/" + maxInvalidAttempts + ")");
                                
                                if(invalidAttempts >= maxInvalidAttempts) {
                                    System.out.println("\n⚠ Maximum invalid attempts reached. Returning to dashboard...");
                                    break;
                                }
                            } else {
                                selectedServices.add(servid);
                                Map<String, Object> service = getServiceInfo(servid);
                                System.out.println("✓ Added: " + service.get("s_services") + 
                                                 " (PHP " + service.get("s_price") + ")");
                                validSelection = true;
                                invalidAttempts = 0; // Reset counter on successful addition
                                
                                // Show current selection after each addition
                                if(i < numberOfServices) {
                                    displaySelectedServices(selectedServices);
                                }
                            }
                        }
                        
                        // If max attempts reached, break out of the for loop
                        if(invalidAttempts >= maxInvalidAttempts) {
                            break;
                        }
                    }
                    
                    // Check if request was terminated due to invalid attempts
                    if(invalidAttempts >= maxInvalidAttempts || selectedServices.isEmpty()) {
                        System.out.println("Request cancelled due to invalid attempts.");
                        break;
                    }
                    
                    // Show final selection
                    System.out.println("\n" + new String(new char[72]).replace('\0', '='));
                    System.out.println("ALL SERVICES SELECTED:");
                    displaySelectedServices(selectedServices);
                    
                    System.out.println("\n" + new String(new char[72]).replace('\0', '-'));
                    System.out.print("Add Note (Optional): ");
                    String errandDetails = sc.nextLine();
                    
                    System.out.print("Preferred Date (YYYY-MM-DD): ");
                    String preferredDate = sc.nextLine();
                    
                    System.out.print("Preferred Time (HH:MM AM/PM): ");
                    String preferredTime = sc.nextLine();
                    
                    System.out.print("Pickup/Service Location: ");
                    String location = sc.nextLine();
                    
                    // Convert service IDs to comma-separated string
                    StringBuilder serviceIdsStr = new StringBuilder();
                    for(int i = 0; i < selectedServices.size(); i++) {
                        if(i > 0) serviceIdsStr.append(",");
                        serviceIdsStr.append(selectedServices.get(i));
                    }
                    
                    // Calculate total price
                    double totalPrice = calculateTotalPrice(serviceIdsStr.toString());
                    
                    System.out.println("\n" + new String(new char[72]).replace('\0', '='));
                    System.out.println("CONFIRM YOUR REQUEST");
                    System.out.println(new String(new char[72]).replace('\0', '='));
                    System.out.println("Customer ID : " + loggedInUserId);
                    if(!errandDetails.isEmpty()) {
                        System.out.println("Note        : " + errandDetails);
                    }
                    System.out.println("Date        : " + preferredDate);
                    System.out.println("Time        : " + preferredTime);
                    System.out.println("Location    : " + location);
                    System.out.println(new String(new char[72]).replace('\0', '-'));
                    
                    displaySelectedServices(selectedServices);
                    
                    System.out.println(new String(new char[72]).replace('\0', '='));
                    System.out.print("\nConfirm Request? (Y/N): ");
                    String confirm = sc.nextLine();
                    
                    if(confirm.equalsIgnoreCase("Y")) {
                        String insertQuery = "INSERT INTO tbl_request (u_id, s_id, r_details, r_date, r_time, r_location, r_total_price, r_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        int requestId = conf.addRecordAndReturnId(insertQuery, loggedInUserId, serviceIdsStr.toString(), errandDetails, preferredDate, preferredTime, location, totalPrice, "Pending");
                        
                        if(requestId > 0) {
                            System.out.println("\n" + new String(new char[72]).replace('\0', '='));
                            System.out.println("✓ REQUEST SUBMITTED SUCCESSFULLY!");
                            System.out.println(new String(new char[72]).replace('\0', '='));
                            System.out.println("Request ID  : " + requestId);
                            System.out.println("Total Cost  : PHP " + String.format("%.2f", totalPrice));
                            System.out.println("Status      : Pending");
                            System.out.println(new String(new char[72]).replace('\0', '='));
                        } else {
                            System.out.println("\n✗ Failed to submit errand request. Please try again.");
                        }
                    } else {
                        System.out.println("Request cancelled.");
                    }
                    break;
                    
                case 2:
                    viewRequest();
                    break;
                    
                case 3:
                    System.out.println("\n" + new String(new char[72]).replace('\0', '='));
                    System.out.println("EDIT MY REQUEST");
                    System.out.println(new String(new char[72]).replace('\0', '='));
                    
                    // Show pending requests
                    String editViewQuery = "SELECT * FROM tbl_request WHERE u_id = " + loggedInUserId + " AND r_status = 'Pending'";
                    try {
                        List<Map<String, Object>> pendingRequests = conf.fetchRecords(editViewQuery);
                        
                        if(pendingRequests.isEmpty()) {
                            System.out.println("No pending requests to edit.");
                            break;
                        }
                        
                        System.out.printf("%-5s %-25s %-15s %-12s %-10s%n", 
                            "ID", "Services", "Date", "Time", "Status");
                        System.out.println(new String(new char[72]).replace('\0', '-'));
                        
                        for(Map<String, Object> row : pendingRequests) {
                            int rid = Integer.parseInt(row.get("r_id").toString());
                            String serviceIds = row.get("s_id") != null ? row.get("s_id").toString() : "";
                            String date = row.get("r_date").toString();
                            String time = row.get("r_time").toString();
                            String status = row.get("r_status").toString();
                            
                            String services = getServiceNamesByIds(serviceIds);
                            if(services.length() > 23) services = services.substring(0, 20) + "...";
                            
                            System.out.printf("%-5d %-25s %-15s %-12s %-10s%n", 
                                rid, services, date, time, status);
                        }
                    } catch (Exception e) {
                        System.out.println("Error viewing requests: " + e.getMessage());
                        break;
                    }
                    
                    System.out.print("\nEnter Request ID to Edit (0 to go back): ");
                    int editRequestId = sc.nextInt();
                    sc.nextLine();
                    
                    if(editRequestId == 0) {
                        System.out.println("Cancelled.");
                        break;
                    }
                    
                    // Verify the request belongs to this user and is pending
                    String verifyEditQuery = "SELECT * FROM tbl_request WHERE r_id = ? AND u_id = ? AND r_status = 'Pending'";
                    List<Map<String, Object>> verifyEditResult = conf.fetchRecords(verifyEditQuery, editRequestId, loggedInUserId);
                    
                    if(verifyEditResult.isEmpty()) {
                        System.out.println("✗ Invalid Request ID or you cannot edit this request.");
                        break;
                    }
                    
                    // Get current request details
                    Map<String, Object> currentRequest = verifyEditResult.get(0);
                    String currentDetails = currentRequest.get("r_details") != null ? currentRequest.get("r_details").toString() : "";
                    String currentDate = currentRequest.get("r_date").toString();
                    String currentTime = currentRequest.get("r_time").toString();
                    String currentLocation = currentRequest.get("r_location").toString();
                    String currentServiceIdsStr = currentRequest.get("s_id") != null ? currentRequest.get("s_id").toString() : "";
                    
                    // Parse current service IDs
                    List<Integer> currentServiceIds = new ArrayList<>();
                    if(!currentServiceIdsStr.isEmpty()) {
                        String[] ids = currentServiceIdsStr.split(",");
                        for(String id : ids) {
                            try {
                                currentServiceIds.add(Integer.parseInt(id.trim()));
                            } catch(NumberFormatException e) {
                                // Skip invalid IDs
                            }
                        }
                    }
                    
                    System.out.println("\n" + new String(new char[72]).replace('\0', '='));
                    System.out.println("CURRENT REQUEST DETAILS");
                    System.out.println(new String(new char[72]).replace('\0', '='));
                    displaySelectedServices(currentServiceIds);
                    System.out.println("Details  : " + currentDetails);
                    System.out.println("Date     : " + currentDate);
                    System.out.println("Time     : " + currentTime);
                    System.out.println("Location : " + currentLocation);
                    
                    System.out.println("\n" + new String(new char[72]).replace('\0', '='));
                    System.out.println("WHAT TO EDIT?");
                    System.out.println(new String(new char[72]).replace('\0', '='));
                    System.out.println("1. Services");
                    System.out.println("2. Details");
                    System.out.println("3. Date");
                    System.out.println("4. Time");
                    System.out.println("5. Location");
                    System.out.println("6. Edit All");
                    System.out.println("7. Cancel Edit");
                    System.out.println(new String(new char[72]).replace('\0', '='));
                    System.out.print("Enter Choice: ");
                    int editChoice = sc.nextInt();
                    sc.nextLine();
                    
                    String newDetails = currentDetails;
                    String newDate = currentDate;
                    String newTime = currentTime;
                    String newLocation = currentLocation;
                    List<Integer> newServiceIds = new ArrayList<>(currentServiceIds);
                    
                    switch(editChoice) {
                        case 1:
                        case 6:
                            viewServices();
                            newServiceIds.clear();
                            
                            // Ask how many services to add
                            int editNumServices = 0;
                            while(editNumServices < 1) {
                                System.out.print("\nHow many services would you like to add? ");
                                editNumServices = sc.nextInt();
                                sc.nextLine();
                                
                                if(editNumServices < 1) {
                                    System.out.println("✗ You must add at least 1 service!");
                                }
                            }
                            
                            // Select services with attempt limit
                            int editInvalidAttempts = 0;
                            int editMaxAttempts = 3;
                            
                            for(int i = 1; i <= editNumServices; i++) {
                                boolean validEdit = false;
                                while(!validEdit) {
                                    System.out.print("\nEnter Service ID for Service #" + i + ": ");
                                    int sid = sc.nextInt();
                                    sc.nextLine();
                                    
                                    if(!isValidServiceId(sid)) {
                                        editInvalidAttempts++;
                                        System.out.println("✗ Invalid Service ID. Please try again. (" + editInvalidAttempts + "/" + editMaxAttempts + ")");
                                        
                                        if(editInvalidAttempts >= editMaxAttempts) {
                                            System.out.println("\n⚠ Maximum invalid attempts reached. Edit cancelled.");
                                            break;
                                        }
                                    } else if(newServiceIds.contains(sid)) {
                                        editInvalidAttempts++;
                                        System.out.println("⚠ Service already added! Choose a different service. (" + editInvalidAttempts + "/" + editMaxAttempts + ")");
                                        
                                        if(editInvalidAttempts >= editMaxAttempts) {
                                            System.out.println("\n⚠ Maximum invalid attempts reached. Edit cancelled.");
                                            break;
                                        }
                                    } else {
                                        newServiceIds.add(sid);
                                        Map<String, Object> service = getServiceInfo(sid);
                                        System.out.println("✓ Added: " + service.get("s_services"));
                                        validEdit = true;
                                        editInvalidAttempts = 0; // Reset on success
                                        
                                        if(i < editNumServices) {
                                            displaySelectedServices(newServiceIds);
                                        }
                                    }
                                }
                                
                                // Break if max attempts reached
                                if(editInvalidAttempts >= editMaxAttempts) {
                                    break;
                                }
                            }
                            
                            // Cancel edit if attempts exhausted
                            if(editInvalidAttempts >= editMaxAttempts) {
                                editChoice = 7; // Set to cancel
                                break;
                            }
                            
                            displaySelectedServices(newServiceIds);
                            if(editChoice == 1) break;
                            
                        case 2:
                            if(editChoice == 2 || editChoice == 6) {
                                System.out.print("Enter New Details: ");
                                newDetails = sc.nextLine();
                            }
                            if(editChoice == 2) break;
                            
                        case 3:
                            if(editChoice == 3 || editChoice == 6) {
                                System.out.print("Enter New Date (YYYY-MM-DD): ");
                                newDate = sc.nextLine();
                            }
                            if(editChoice == 3) break;
                            
                        case 4:
                            if(editChoice == 4 || editChoice == 6) {
                                System.out.print("Enter New Time (HH:MM AM/PM): ");
                                newTime = sc.nextLine();
                            }
                            if(editChoice == 4) break;
                            
                        case 5:
                            if(editChoice == 5 || editChoice == 6) {
                                System.out.print("Enter New Location: ");
                                newLocation = sc.nextLine();
                            }
                            break;
                            
                        case 7:
                            System.out.println("Edit cancelled.");
                            break;
                            
                        default:
                            System.out.println("Invalid choice. Edit cancelled.");
                            break;
                    }
                    
                    if(editChoice >= 1 && editChoice <= 6) {
                        // Convert service IDs to comma-separated string
                        StringBuilder newServiceIdsStr = new StringBuilder();
                        for(int i = 0; i < newServiceIds.size(); i++) {
                            if(i > 0) newServiceIdsStr.append(",");
                            newServiceIdsStr.append(newServiceIds.get(i));
                        }
                        
                        // Calculate new total price
                        double newTotalPrice = calculateTotalPrice(newServiceIdsStr.toString());
                        
                        System.out.println("\n" + new String(new char[72]).replace('\0', '='));
                        System.out.println("CONFIRM CHANGES");
                        System.out.println(new String(new char[72]).replace('\0', '='));
                        displaySelectedServices(newServiceIds);
                        System.out.println("Details  : " + newDetails);
                        System.out.println("Date     : " + newDate);
                        System.out.println("Time     : " + newTime);
                        System.out.println("Location : " + newLocation);
                        
                        System.out.print("\nSave Changes? (Y/N): ");
                        String saveConfirm = sc.nextLine();
                        
                        if(saveConfirm.equalsIgnoreCase("Y")) {
                            String updateQuery = "UPDATE tbl_request SET s_id = ?, r_details = ?, r_date = ?, r_time = ?, r_location = ?, r_total_price = ? WHERE r_id = ? AND u_id = ?";
                            conf.updateRequest(updateQuery, newServiceIdsStr.toString(), newDetails, newDate, newTime, newLocation, newTotalPrice, editRequestId, loggedInUserId);
                            System.out.println("✓ Request updated successfully!");
                        } else {
                            System.out.println("Changes discarded.");
                        }
                    }
                    break;
                    
                case 4:
                    System.out.println("\n" + new String(new char[72]).replace('\0', '='));
                    System.out.println("CANCEL ERRANDS");
                    System.out.println(new String(new char[72]).replace('\0', '='));
                    
                    // Show pending requests
                    String cancelViewQuery = "SELECT * FROM tbl_request WHERE u_id = " + loggedInUserId + " AND r_status = 'Pending'";
                    try {
                        List<Map<String, Object>> pendingRequests = conf.fetchRecords(cancelViewQuery);
                        
                        if(pendingRequests.isEmpty()) {
                            System.out.println("No pending requests to cancel.");
                            break;
                        }
                        
                        System.out.printf("%-5s %-25s %-15s %-12s %-10s%n", 
                            "ID", "Services", "Date", "Time", "Status");
                        System.out.println(new String(new char[72]).replace('\0', '-'));
                        
                        for(Map<String, Object> row : pendingRequests) {
                            int rid = Integer.parseInt(row.get("r_id").toString());
                            String serviceIds = row.get("s_id") != null ? row.get("s_id").toString() : "";
                            String date = row.get("r_date").toString();
                            String time = row.get("r_time").toString();
                            String status = row.get("r_status").toString();
                            
                            String services = getServiceNamesByIds(serviceIds);
                            if(services.length() > 23) services = services.substring(0, 20) + "...";
                            
                            System.out.printf("%-5d %-25s %-15s %-12s %-10s%n", 
                                rid, services, date, time, status);
                        }
                    } catch (Exception e) {
                        System.out.println("Error viewing requests: " + e.getMessage());
                        break;
                    }
                    
                    System.out.print("\nEnter Request ID to Cancel (0 to go back): ");
                    int requestId = sc.nextInt();
                    sc.nextLine();
                    
                    if(requestId == 0) {
                        System.out.println("Cancelled.");
                        break;
                    }
                    
                    String verifyQuery = "SELECT * FROM tbl_request WHERE r_id = ? AND u_id = ? AND r_status = 'Pending'";
                    List<Map<String, Object>> verifyResult = conf.fetchRecords(verifyQuery, requestId, loggedInUserId);
                    
                    if(verifyResult.isEmpty()) {
                        System.out.println("✗ Invalid Request ID or you don't have permission to cancel this request.");
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