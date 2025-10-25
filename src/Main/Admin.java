
package Main;

import Config.config;
import java.util.Scanner;




public class Admin {
    config conf = new config();
    Scanner sc = new Scanner(System.in);
    
    public static void viewUsers() {
        String Query = "SELECT * FROM tbl_users";
        
        String[] userHeaders = {"ID", "Name", "Email", "Type", "Status"};
        String[] userColumns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
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
    

  public void Admin() {
      
        boolean adminRunning = true;
        while(adminRunning){
        System.out.println("\n===== ADMIN DASHBOARD =====");
        System.out.println("==============================");
        System.out.println("1. View Users");
        System.out.println("2. Approve Customer/Service Provider");
        System.out.println("3. View Services");
        System.out.println("4. View All Request");
        System.out.println("5. Logout");
        System.out.print("Enter Choice: ");
        int respo = sc.nextInt();

        switch(respo){
            case 1:
                viewUsers();
                break;

            case 2:
                String pendingQuery = "SELECT * FROM tbl_users WHERE u_type IN ('Customer', 'Service Provider') AND u_status = 'Pending'";
                String[] pendingHeaders = {"ID", "Name", "Email", "Type", "Status"};
                String[] pendingColumns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
                conf.viewUsers(pendingQuery, pendingHeaders, pendingColumns);

                System.out.print("Enter ID to Approve: ");
                int ids = sc.nextInt();

                String checkQry2 = "SELECT u_type FROM tbl_users WHERE u_id = ?";
                java.util.List<java.util.Map<String, Object>> checkResult2 = conf.fetchRecords(checkQry2, ids);

                if(!checkResult2.isEmpty()){
                    String accountType = checkResult2.get(0).get("u_type").toString();
                    if(!accountType.equals("Admin")){
                        String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
                        conf.updateUsers(sql, "Approved", ids);
                        System.out.println("User approved successfully!");
                    } else {
                        System.out.println("Error: You cannot approve Admin accounts!");
                    }
                } else {
                    System.out.println("User ID not found!");
                }
                viewServices();
                break;

            case 3:
                viewServices();
                break;
                
            case 4:
                System.out.println("View all Request");
                

            case 5:
                System.out.println("Logging out...");
                adminRunning = false;
                break;

            default:
                System.out.println("Invalid Choice.");
        }
    }


}   
    
}
