
package Main;
import Config.config;
import java.util.Scanner;


public class Customer {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    
    public static void viewServices() {
    String Query = "SELECT * FROM tbl_services";

    String[] userHeaders = {"ID", "Service", "Description", "Price"};
    String[] userColumns = {"s_id", "s_services", "s_description", "s_price"};
    config conf = new config();
    conf.viewServices(Query, userHeaders, userColumns);
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
                    System.out.println("Request Errands");
                    viewServices();
                    System.out.print("Enter Service ID to Choose");
                    int servid = sc.nextInt();
                    
                    
                    
                    
                   
                    break;

                case 2:
                    System.out.println("View My Errands - To be implemented");
                    break;

                case 3:
                    System.out.println("Cancel Errands - To be implemented");
                    break;

                case 4:
                    System.out.println("Logging out...");
                    customerRunning = false;
                    break;

                default:
                    System.out.println("Invalid Choice.");
            }    break;
        }
    }
       
 }

