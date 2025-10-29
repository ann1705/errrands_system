
package Main;

import Config.config;
import java.util.Scanner;


public class SuperAdmin {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    
    
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
    
    
    
  
   
   
    
     public void SuperAdmin(){
    
    boolean developerRunning = true;
                        while(developerRunning){
                            System.out.println("\n===== SUPER ADMIN DASHBOARD (DEVELOPER) =====");
                            System.out.println("===============================================");
                            System.out.println("1. Add Admin Account");
                            System.out.println("2. Approve Account");
                            System.out.println("3. Add Services");
                            System.out.println("4. Update Services");
                            System.out.println("5. View Services");
                            System.out.println("6. Delete Services");
                            System.out.println("7. View User");
                            System.out.println("8.Delete User");
                            System.out.println("9. View All Request");
                            System.out.println("10. Logout");
                            System.out.print("Enter Choice: ");
                            int devChoice = sc.nextInt();
                            
                            switch(devChoice){
                                case 1:
                                    System.out.print("Enter Admin First Name: ");
                                    sc.nextLine(); // consume newline
                                    String adminFname = sc.next();
                                    
                                    System.out.print("Enter Admin Last Name: ");
                                    
                                    String adminLname = sc.next();
   
                                    System.out.print("Enter Admin email: ");
                                    String adminEmail = sc.next();
                                    
                                    // Check if email exists
                                    String checkQry = "SELECT * FROM tbl_users WHERE u_email = ?";
                                    java.util.List<java.util.Map<String, Object>> checkResult = conf.fetchRecords(checkQry, adminEmail);
                                    
                                    if(checkResult.isEmpty()){
                                        System.out.print("Enter Admin Password: ");
                                        String adminPass = sc.next();
                                        String hashpass = conf.hashPassword(adminPass);
                                        
                                        String adminSQL = "INSERT INTO tbl_users(u_first_name,u_last_name, u_email, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?,?)";
                                        conf.addRecord(adminSQL, adminFname, adminLname, adminEmail, "Admin", "Approved", hashpass);
                                        System.out.println("Admin account created successfully!");
                                    } else {
                                        System.out.println("Email already exists!");
                                    }
                                    break;
                                    
                                case 2:
                                    viewUsers();
                                    System.out.print("Enter ID to Approve: ");
                                    int approveId = sc.nextInt();
                                    
                                    String approveSql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
                                    conf.updateUsers(approveSql, "Approved", approveId);
                                    System.out.println("User approved successfully!");
                                    break;
                                    
                                case 3:
                                    System.out.print("Enter Services:");
                                    String ser = sc.nextLine();
                                    sc.nextLine();
                                    System.out.print("Enter Service Descriptions:");
                                    String desc = sc.nextLine();
                                    sc.nextLine();
                                    System.out.print("Enter Price:");
                                    String price = sc.nextLine();
                                    
                                    String sql = "INSERT INTO tbl_services(s_services, s_description, s_price) VALUES (?, ?, ?)";
                                    conf.addService(sql, ser, desc, price);
                                    break;
                                    
                                case 4:
                                    viewServices();
                                    
                                    System.out.print("Enter Service ID  to update:");
                                    int id = sc.nextInt();
                                    System.out.print("Enter new Service Name: ");
                                    String nsname = sc.next();
                                    System.out.print("Enter new Service Description: ");
                                    String ndesc = sc.next();
                                    System.out.print("Enter new Price: ");
                                    String nprice = sc.next();
                                   
                                    String qry = "UPDATE tbl_services SET s_services = ?, s_description = ?, s_price = ?  WHERE s_id = ?";
                                    
                                    conf.updateService(qry, nsname, ndesc, nprice, id);
                                    viewServices();
                                    break;
                                    
                                case 5:
                                   viewServices();
                                    break;
                                    
                                case 6:
                                    viewServices();
                                    System.out.print("Enter the ID to Delete: ");
                                    id = sc.nextInt();

                                    String deleteqry = "DELETE FROM tbl_services WHERE s_id = ?";
                                    conf.deleteServices(deleteqry, id);
                                    viewServices();
                                    break;
                                    
                                case 7:
                                    viewUsers();
                                    break;
                                    
                                case 8: 
                                    viewUsers();
                                    System.out.print("Enter User ID to Delete: ");
                                    int uId = sc.nextInt();
                                    
                                    String deleteSql = "DELETE FROM tbl_users WHERE u_id = ?";
                                    
                                    conf.deleteUsers(deleteSql, uId);
                                    System.out.println("User deleted successfully!");
                                    break;
                                    
                                case 9: 
                                    System.out.println("view all request....");
                                    
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
