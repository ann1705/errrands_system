
package Main;

import Config.config;
import java.util.Scanner;


public class ServiceProvider {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    
    
    
    public void ServiceProvider() {
         boolean providerRunning = true;
         while(providerRunning){
            System.out.println("\n===== SERVICE PROVIDER DASHBOARD =====");
            System.out.println("1. View My Services");
            System.out.println("2. Add Service");
            System.out.println("3. Update Service");
            System.out.println("4. Delete Service");
            System.out.println("5. View Service Requests");
            System.out.println("6. Logout");
            System.out.print("Enter Choice: ");
            int provResp = sc.nextInt();

            switch(provResp){
                case 1:
                    System.out.println("View My Services - To be implemented");
                    break;

                case 2:
                    System.out.println("Add Service - To be implemented");
                    break;

                case 3:
                    System.out.println("Update Service - To be implemented");
                    break;

                case 4:
                    System.out.println("Delete Service - To be implemented");
                    break;

                case 5:
                    System.out.println("View Service Requests - To be implemented");
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
