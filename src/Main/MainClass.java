package Main;

import Config.config;
import java.util.Scanner;
import java.util.InputMismatchException; 

public class MainClass {

    public static void viewUsers() {
        String Query = "SELECT * FROM tbl_users";

        String[] userHeaders = {"ID", "Name", "Email", "Type", "Status"};
        String[] userColumns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        config conf = new config();
        conf.viewUsers(Query, userHeaders, userColumns);
    }

    public static void main(String[] args) {
        config conf = new config();
        conf.connectDB();

       
        final String DEVELOPER_EMAIL = "developer";
        final String DEVELOPER_PASSWORD = "1234";

        int choice = 0;
        char cont = 'Y'; 
        
        
        Scanner sc = new Scanner(System.in); 

        do {
            try { 
                System.out.println("==== MAIN MENU =====");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");
                System.out.println("======================");
                System.out.print("Enter Choice: ");
                choice = sc.nextInt();
                sc.nextLine(); 

                switch (choice) {

                    case 1:
                        System.out.print("Enter email: ");
                        String email = sc.nextLine(); 
                        System.out.print("Enter Password: ");
                        String pass = sc.nextLine();
                        
                        
                        if (email.equals(DEVELOPER_EMAIL) && pass.equals(DEVELOPER_PASSWORD)) {
                           
                            SuperAdmin superadmin = new SuperAdmin();
                            superadmin.SuperAdmin();
                            
                        
                        } else { 
                            String hashpass = conf.hashPassword(pass); 
                            
                            String qry = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";
                            java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(qry, email, hashpass);

                            if (result.isEmpty()) {
                                System.out.println("INVALID CREDENTIALS, TRY AGAIN!!!");
                            } else {
                                java.util.Map<String, Object> user = result.get(0);
                                String stat = user.get("u_status").toString();
                                String type = user.get("u_type").toString();
                                
                                if (stat.equals("Pending")) {
                                    System.out.println("Account is Pending, Contact the Admin!");
                                } else {
                                    System.out.println("LOGIN SUCCESS!");

                                    if (type.equals("Admin")) {
                                        Admin admin = new Admin();
                                        admin.Admin(); 
                                    } else if (type.equals("Service Provider")) {
                                        ServiceProvider service = new ServiceProvider();
                                        service.ServiceProvider(); 
                                    } else if (type.equals("Customer")) {
                                        Customer customer = new Customer();
                                        customer.Customer(); 
                                    }
                                }
                            }
                        }
                        break;

                    case 2:
                        
                        System.out.print("Enter user name: ");
                        String name = sc.nextLine(); 
                        
                        System.out.print("Enter user email: ");
                        String em = sc.next();
                        sc.nextLine();
                        
                       
                        int emailAttempts = 0;
                        boolean validEmail = false;

                        while (emailAttempts < 3) {
                            String qry = "SELECT * FROM tbl_users WHERE u_email = ?";
                            java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(qry, em);

                            if (result.isEmpty()) {
                                validEmail = true;
                                break;
                            } else {
                                emailAttempts++;
                                if (emailAttempts < 3) {
                                    System.out.print("Email already exists, Enter other Email (" + (3 - emailAttempts) + " attempts left): ");
                                    em = sc.next();
                                    sc.nextLine(); 
                                }
                            }
                        }

                        if (!validEmail) {
                            System.out.println("Maximum attempts reached. Returning to main menu...");
                            break;
                        }

                        System.out.print("Enter user Type (1 - Service Provider/2 - Customer): ");
                        
                        int typeChoice = sc.nextInt();
                        sc.nextLine(); 

                        while (typeChoice > 2 || typeChoice < 1) {
                            System.out.print("Invalid, choose between 1 & 2 only: ");
                            typeChoice = sc.nextInt();
                            sc.nextLine(); 
                        }

                        String tp;
                        String status = "Pending"; 

                        if (typeChoice == 1) {
                            tp = "Service Provider";
                        } else {
                            tp = "Customer";
                        }

                        System.out.print("Enter Address: ");
                        String add = sc.nextLine(); 
                        
                        System.out.print("Enter Phone no.: "); 
                        String ph = sc.nextLine(); 
                        
                        System.out.print("Enter Password: ");
                        String ps = sc.nextLine();

                        String hashedPass = conf.hashPassword(ps);

                        String sql = "INSERT INTO tbl_users(u_name, u_email, u_type, u_status, u_pass, u_phone, u_address) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        conf.addRecord(sql, name, em, tp, status, hashedPass, ph, add); 

                        System.out.println("Registration successful!");
                        System.out.println(tp + " account created. Please wait for admin approval.");
                        break;

                    case 3:
                        System.out.println("Thank you! Program ended.");
                        sc.close(); 
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid Choice. Please enter 1, 2, or 3.");
                }

            } catch (InputMismatchException e) {
                
                System.out.println("Invalid input. Please enter a number (1, 2, or 3).");
                sc.nextLine();
                choice = 0; 
            } catch (Exception e) {
                
                System.out.println("An unexpected error occurred: " + e.getMessage());
                
            }


            if (choice != 3) {
                System.out.print("\nDo you want to continue? (Y/N): ");
                
                String continueInput = sc.next();
                cont = continueInput.toUpperCase().charAt(0); 
            } else {
                cont = 'N'; 
            }

        } while (cont == 'Y');
        
        
        System.out.println("Thank you! Program ended.");
        sc.close();
    }
}