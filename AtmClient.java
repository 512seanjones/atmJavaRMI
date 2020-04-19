import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

public class AtmClient {
    public static void main(String arg[]){
        try {
            String ipAddress = "192.168.0.119";
            Atm obj = (Atm) Naming.lookup("//" + ipAddress + "/AtmServer");

            Scanner in = new Scanner(System.in);
            while (true) {
                System.out.println("Please enter an account number:");
                String accountNum = in.nextLine();
                if (!obj.lookupAccount(accountNum)) {
                    System.out.println("Sorry that account does not exist");
                    continue;
                }
                System.out.println("Please enter a pin number:");
                String pin = in.nextLine();
                if (obj.accessAccount(accountNum, pin)) {
                    boolean inAccount = true;
                    while (inAccount) {
                        System.out.println("\n");
                        System.out.println("Current account balance is: " + obj.getBalance());
                        System.out.println("\n");
                        System.out.println("Please select an atm option:");
                        System.out.println("\t1. Withdraw from account");
                        System.out.println("\t2. Deposit to account");
                        System.out.println("\t3. Exit account");
                        String choice = in.nextLine();
                        switch (choice) {
                            case "1": {
                                System.out.println("Enter deposit amount:");
                                float amount = in.nextFloat();
                                System.out.println(obj.withdraw(amount));
                            }
                            case "2": {
                                System.out.println("Enter withdraw amount");
                                float amount = in.nextFloat();
                                System.out.println(obj.deposit(amount));
                                break;
                            }
                            case "3": {
                                System.out.println(obj.exitAccount());
                                // Stop looping
                                inAccount = false;
                                break;
                            }
                            default: {
                                System.out.println("Please select an option from the menu");
                            }
                        }
                    }
                }
                else {
                    System.out.println("Invalid pin number");
                    continue;
                }
                System.out.println("Would you like to access another account (y or n)?");
                String exitChoice = in.nextLine();
                if (exitChoice != "y") {
                    System.out.println("Goodbye");
                    break;
                }
            }

            System.out.println(obj.accessAccount("0002", "5555"));
            System.out.println(obj.deposit(100.00));
            System.out.println(obj.withdraw(25));
            System.out.println(obj.getBalance());
            System.out.println(obj.exitAccount());
        }
        catch (Exception e) {
            System.out.println("AtmClient Exeception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
