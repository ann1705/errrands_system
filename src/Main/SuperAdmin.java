package Main;

import Config.config;
import java.util.Scanner;

public class SuperAdmin {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    private int loggedInUserId;
    
    // Constructor to set the user ID when they log in
    public SuperAdmin(int userId) {
        this.loggedInUserId = userId;
    }
    
    public static void viewUsers() {
        String Query = "SELECT * FROM tbl_users";
        String[] userHeaders = {"ID", "First Name","Last Name", "Email", "Type", "Status"};
        String[] userColumns = {"u_id", "u_first_name","u_last_name", "u_email", "u_type", "u_status"};
        config conf = new config();
        conf.viewUsers(Query, userHeaders, userColumns);
    }
    
    public static void viewServices() {
        String Query = "SELECT * FROM tbl_services";
        String[] userHeaders = {"ID", "Service", "Description", "Price"};
        String[] userColumns = {"s_id", "s_services", "s_description", "s_price"};
        config conf = new config();
        conf.viewServices(Query, userHeaders, userColumns);
    }
    
    public void viewRequest() {
        System.out.println("\n=== ALL REQUESTS (SUPER ADMIN VIEW) ===");
        // Super Admin views ALL requests from ALL users
        String viewQuery = "SELECT r.r_id, u.u_first_name, u.u_last_name, s.s_services, r.r_details, r.r_date, r.r_time, r.r_location, s.s_price, r.r_status " +
                          "FROM tbl_request r " +
                          "INNER JOIN tbl_services s ON r.s_id = s.s_id " +
                          "INNER JOIN tbl_users u ON r.u_id = u.u_id " +
                          "ORDER BY r.r_id DESC";
        String[] requestHeaders = {"Request ID", "Customer First", "Customer Last", "Service", "Details", "Date", "Time", "Location", "Price", "Status"};
        String[] requestColumns = {"r_id", "u_first_name", "u_last_name", "s_services", "r_details", "r_date", "r_time", "r_location", "s_price", "r_status"};
        conf.viewRequest(viewQuery, requestHeaders, requestColumns);
    }
    
    public void showDashboard() {
        boolean developerRunning = true;
        while(developerRunning) {
            System.out.println("\n===== SUPER ADMIN DASHBOARD (DEVELOPER) =====");
            System.out.println("===============================================");
            System.out.println("1. Add Admin Account");
            System.out.println("2. Approve Account");
            System.out.println("3. Add Services");
            System.out.println("4. Update Services");
            System.out.println("5. View Services");
            System.out.println("6. Delete Services");
            System.out.println("7. View User");
            System.out.println("8. Delete User");
            System.out.println("9. View All Request");
            System.out.println("10. Logout");
            System.out.print("Enter Choice: ");
            int devChoice = sc.nextInt();
            
            switch(devChoice) {
                case 1:
                    System.out.print("Enter Admin First Name: ");
                    sc.nextLine(); // consume newline
                    String adminFname = sc.nextLine();
                    
                    System.out.print("Enter Admin Last Name: ");
                    String adminLname = sc.nextLine();
   
                    System.out.print("Enter Admin Email: ");
                    String adminEmail = sc.nextLine();
                    
                    String checkQry = "SELECT * FROM tbl_users WHERE u_email = ?";
                    java.util.List<java.util.Map<String, Object>> checkResult = conf.fetchRecords(checkQry, adminEmail);
                    
                    System.out.print("Enter Admin Phone Number: ");
                    String adminPhone = sc.nextLine();
                    
                    System.out.print("Enter Admin Address: ");
                    String adminAddress = sc.nextLine();
                    
                    // Check if email exists
                   // String checkQry = "SELECT * FROM tbl_users WHERE u_email = ?";
                   // java.util.List<java.util.Map<String, Object>> checkResult = conf.fetchRecords(checkQry, adminEmail);
                    
                    if(checkResult.isEmpty()) {
                        System.out.print("Enter Admin Password: ");
                        String adminPass = sc.nextLine();
                        String hashpass = conf.hashPassword(adminPass);
                        
                        String adminSQL = "INSERT INTO tbl_users(u_first_name, u_last_name, u_email, u_phone, u_address, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        conf.addRecord(adminSQL, adminFname, adminLname, adminEmail, adminPhone, adminAddress, "Admin", "Approved", hashpass);
                        System.out.println("Admin account created successfully!");
                    } else {
                        System.out.println("Email already exists!");
                    }
                    break;
                    
                case 2:
                    int attempts = 0;
                    int maxAttempts = 3;
                    boolean validId = false;
                    
                    while (attempts < maxAttempts && !validId) {
                        viewUsers();
                        System.out.print("Enter ID to Approve (Attempt " + (attempts + 1) + " of " + maxAttempts + "): ");
                        int approveId = sc.nextInt();
                        
                        // Check if the ID exists
                        String checkIdQry = "SELECT * FROM tbl_users WHERE u_id = ?";
                        java.util.List<java.util.Map<String, Object>> idResult = conf.fetchRecords(checkIdQry, approveId);
                        
                        if (!idResult.isEmpty()) {
                            // ID exists, proceed with approval
                            String approveSql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
                            conf.updateUsers(approveSql, "Approved", approveId);
                            System.out.println("User approved successfully!");
                            validId = true;
                        } else {
                            // ID doesn't exist
                            attempts++;
                            if (attempts < maxAttempts) {
                                System.out.println("Error: User ID does not exist. Please try again.");
                            } else {
                                System.out.println("Error: Maximum attempts reached. Returning to dashboard...");
                            }
                        }
                    }
                    break;
                    
                case 3:
                    System.out.print("Enter Services: ");
                    sc.nextLine();
                    String ser = sc.nextLine();
                    
                    System.out.print("Enter Service Descriptions: ");
                    String desc = sc.nextLine();
                    
                    System.out.print("Enter Price: ");
                    String price = sc.nextLine();
                    
                    String sql = "INSERT INTO tbl_services(s_services, s_description, s_price) VALUES (?, ?, ?)";
                    conf.addService(sql, ser, desc, price);
                    break;
                    
                case 4:
                    int serviceAttempts = 0;
                    int maxServiceAttempts = 3;
                    boolean validServiceId = false;
                    
                    while (serviceAttempts < maxServiceAttempts && !validServiceId) {
                        viewServices();
                        
                        System.out.print("Enter Service ID to update (Attempt " + (serviceAttempts + 1) + " of " + maxServiceAttempts + "): ");
                        int id = sc.nextInt();
                        sc.nextLine(); // consume newline
                        
                        // Check if the Service ID exists
                        String checkServiceQry = "SELECT * FROM tbl_services WHERE s_id = ?";
                        java.util.List<java.util.Map<String, Object>> serviceResult = conf.fetchRecords(checkServiceQry, id);
                        
                        if (!serviceResult.isEmpty()) {
                            // Service ID exists, proceed with update
                            System.out.print("Enter new Service Name: ");
                            String nsname = sc.nextLine();
                            
                            System.out.print("Enter new Service Description: ");
                            String ndesc = sc.nextLine();
                            
                            System.out.print("Enter new Price: ");
                            String nprice = sc.nextLine();
                           
                            String qry = "UPDATE tbl_services SET s_services = ?, s_description = ?, s_price = ?  WHERE s_id = ?";
                            conf.updateService(qry, nsname, ndesc, nprice, id);
                            viewServices();
                            System.out.println("Service updated successfully!");
                            validServiceId = true;
                        } else {
                            // Service ID doesn't exist
                            serviceAttempts++;
                            if (serviceAttempts < maxServiceAttempts) {
                                System.out.println("Error: Service ID does not exist. Please try again.");
                            } else {
                                System.out.println("Error: Maximum attempts reached. Returning to dashboard...");
                            }
                        }
                    }
                    break;
                    
                case 5:
                    viewServices();
                    break;
                    
                case 6:
                    int deleteServiceAttempts = 0;
                    int maxDeleteServiceAttempts = 3;
                    boolean validDeleteServiceId = false;
                    
                    while (deleteServiceAttempts < maxDeleteServiceAttempts && !validDeleteServiceId) {
                        viewServices();
                        System.out.print("Enter the ID to Delete (Attempt " + (deleteServiceAttempts + 1) + " of " + maxDeleteServiceAttempts + "): ");
                        int id = sc.nextInt();
                        
                        // Check if the Service ID exists
                        String checkDeleteServiceQry = "SELECT * FROM tbl_services WHERE s_id = ?";
                        java.util.List<java.util.Map<String, Object>> deleteServiceResult = conf.fetchRecords(checkDeleteServiceQry, id);
                        
                        if (!deleteServiceResult.isEmpty()) {
                            // Service ID exists, proceed with deletion
                            String deleteqry = "DELETE FROM tbl_services WHERE s_id = ?";
                            conf.deleteServices(deleteqry, id);
                            viewServices();
                            System.out.println("Service deleted successfully!");
                            validDeleteServiceId = true;
                        } else {
                            // Service ID doesn't exist
                            deleteServiceAttempts++;
                            if (deleteServiceAttempts < maxDeleteServiceAttempts) {
                                System.out.println("Error: Service ID does not exist. Please try again.");
                            } else {
                                System.out.println("Error: Maximum attempts reached. Returning to dashboard...");
                            }
                        }
                    }
                    break;
                    
                case 7:
                    viewUsers();
                    break;
                    
                case 8: 
                    int deleteUserAttempts = 0;
                    int maxDeleteUserAttempts = 3;
                    boolean validDeleteUserId = false;
                    
                    while (deleteUserAttempts < maxDeleteUserAttempts && !validDeleteUserId) {
                        viewUsers();
                        System.out.print("Enter User ID to Delete (Attempt " + (deleteUserAttempts + 1) + " of " + maxDeleteUserAttempts + "): ");
                        int uId = sc.nextInt();
                        
                        // Check if the User ID exists
                        String checkDeleteUserQry = "SELECT * FROM tbl_users WHERE u_id = ?";
                        java.util.List<java.util.Map<String, Object>> deleteUserResult = conf.fetchRecords(checkDeleteUserQry, uId);
                        
                        if (!deleteUserResult.isEmpty()) {
                            // User ID exists, proceed with deletion
                            String deleteSql = "DELETE FROM tbl_users WHERE u_id = ?";
                            conf.deleteUsers(deleteSql, uId);
                            System.out.println("User deleted successfully!");
                            validDeleteUserId = true;
                        } else {
                            // User ID doesn't exist
                            deleteUserAttempts++;
                            if (deleteUserAttempts < maxDeleteUserAttempts) {
                                System.out.println("Error: User ID does not exist. Please try again.");
                            } else {
                                System.out.println("Error: Maximum attempts reached. Returning to dashboard...");
                            }
                        }
                    }
                    break;
                    
                case 9: 
                    viewRequest();
                    break;
                    
                case 10:
                    System.out.println("Logging out from Super Admin...");
                    developerRunning = false;
                    break;
                    
                default:
                    System.out.println("Invalid Choice.");
                    break;
            }
        }
    }
}