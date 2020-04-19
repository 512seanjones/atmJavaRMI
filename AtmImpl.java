import java.sql.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AtmImpl extends UnicastRemoteObject implements Atm {
    private boolean allowAccess = false;
    Connection conn; //used for establishing database connection
    Statement stmt; //Allocate a 'Statement' object in the Connection
    String account_number = new String();
    public AtmImpl() throws RemoteException, SQLException{
      conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/atm_database?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
            "myuser", "xxxx");
      stmt = conn.createStatement();
    }

    public boolean lookupAccount(String accountNum){
      boolean account_exists = false;
      try{
          //executes above query to search for a row in datbase that matches AccountNum
          String accountNum_query = "SELECT * FROM atm_database WHERE AccountNum = " + accountNum + " LIMIT 1";
          ResultSet rset = stmt.executeQuery(accountNum_query);
          //will return 0 if the strings for PinNum and account pin arenthe same;
          account_exists = rset.next();

        }
      catch(Exception e) {
          System.out.println("Account lookup exception: " + e.getMessage());
          e.printStackTrace();
      }
      return account_exists;
    }

    public boolean accessAccount(String accountNum, String accountPin){
      int result = 1;
      int rows_affected = 0;
      boolean correct_pin = false;
      boolean lookupAccount = false;
      try{
          //executes above query to search for a row in datbase that matches AccountNum
          String accountNum_query = "SELECT * FROM atm_database WHERE AccountNum = " + accountNum + " LIMIT 1";
          ResultSet rset = stmt.executeQuery(accountNum_query);
          //will return 0 if the strings for PinNum and account pin arenthe same;
          lookupAccount = rset.next();
          if(lookupAccount){
              lookupAccount = true;
              if(rset.getInt("Accessed") == 1) result = 1;
              else result = 0;

              if(((rset.getString("PinNum")).compareTo(accountPin)) == 0) correct_pin = true;
              else correct_pin = false;

              if(correct_pin == true && result == 0){
                  String update_query = "UPDATE atm_database SET Accessed = 1 WHERE AccountNum = " + accountNum;
                  rows_affected = stmt.executeUpdate(update_query);
              }
            }

        }
      catch(Exception e) {
          System.out.println("Validate Access exception: " + e.getMessage());
          e.printStackTrace();
      }
      if(!lookupAccount){
        //coundnt locate account
        System.out.println("\nCouldnt locate account to grant access\n");
        return false;
      }
      if (result == 1) {
          // Row was found, but is being accessed by someone else
          return false;
      }
      if(correct_pin){
          if(rows_affected != 0){
            allowAccess = true;
            account_number = accountNum;
            return true;
          }
          else{
            allowAccess = false;
            return false;
          }
      }
      else {
          allowAccess = false;
          return false;
      }
    }




    public String getBalance() {
        String balance = new String();
        if(allowAccess){
          try{
              String accountNum_query = "SELECT * FROM atm_database WHERE AccountNum = " + account_number + " LIMIT 1";

              ResultSet rset = stmt.executeQuery(accountNum_query);
              rset.next();
              balance = rset.getString("Balance");
          }
          catch(Exception e){
            System.out.println("Get Balance exception: " + e.getMessage());
            e.printStackTrace();
          }
        }
        else{
          return "GetBalanceError: User needs to access an account";

        }
        return "$" + balance;
    }
    public String deposit(double amount){
        Double balance;
        int success = 0;
        Double new_balance = 0.0;
        if (amount < 0) {
            return "Unable to deposit a negative amount";
        }
        if(allowAccess){
            try{
                String accountNum_query = "SELECT * FROM atm_database WHERE AccountNum = " + account_number + " LIMIT 1";
                //executes above query to search for a row in datbase that matches AccountNum
                ResultSet rset = stmt.executeQuery(accountNum_query);
                rset.next();
                balance = rset.getDouble("Balance");
                new_balance = balance + amount;
                String update_balance = "UPDATE atm_database SET Balance = " + Double.toString(new_balance) + " WHERE AccountNum = " + account_number;
                success = stmt.executeUpdate(update_balance);
          }
          catch(Exception e){
            System.out.println("Make Deposit exception: " + e.getMessage());
            e.printStackTrace();
          }
          if(success == 0){
            return "Unable to deposit amount. atm database error";
          }
          else{
            return "DepositSuccess: User now has a balance of " + Double.toString(new_balance);
          }

      }
      else{
        return "DepositError: User needs to access an account";
      }
    }

    public String withdraw(double amount) {
        Double balance;
        int success = 0;
        Double new_balance = 0.0;
        boolean sufficient_funds = true;
        if (amount < 0) {
            return "Unable to withdraw a negative amount";
        }
        // Check if user is logged in
        if (allowAccess) {
          try{
              String accountNum_query = "SELECT * FROM atm_database WHERE AccountNum = " + account_number + " LIMIT 1";
              //executes above query to search for a row in datbase that matches AccountNum
              ResultSet rset = stmt.executeQuery(accountNum_query);
              rset.next();
              balance = rset.getDouble("Balance");
              new_balance = balance - amount;
              if (new_balance >= 0.0) {
                String update_balance = "UPDATE atm_database SET Balance = " + Double.toString(new_balance) + " WHERE AccountNum = " + account_number;
                success = stmt.executeUpdate(update_balance);
              }
              else{
                sufficient_funds = false;
              }
            }
            catch(Exception e){
              System.out.println("Make Witchdrawl exception: " + e.getMessage());
              e.printStackTrace();
            }

            if (!sufficient_funds) {
                return "Unable to withdraw. Amount will bring balance below zero";
            }
            else {
                if(success == 0){
                  return "Unable to withdraw. Database access error";
                }
                else{
                  return "Withdraw Success: User now has a balance of " + Double.toString(new_balance);
                }
            }
        }
        else {
            return "WithdrawError: User needs to access an account";
        }
    }

    public String exitAccount() {
      if (allowAccess) {
          try{
              String accountNum_query = "SELECT * FROM atm_database WHERE AccountNum = " + account_number + " LIMIT 1";
              //executes above query to search for a row in datbase that matches AccountNum
              ResultSet rset = stmt.executeQuery(accountNum_query);
              rset.next();
              String update_query = "UPDATE atm_database SET Accessed = 0 WHERE AccountNum = " + account_number;
              int rows_affected = stmt.executeUpdate(update_query);
            }
          catch (Exception e) {
              System.out.println("Write CSV Exception: " + e.getMessage());
              e.printStackTrace();
          }
      }
      allowAccess = false;
      account_number = null;
      return "ExitAccountSuccess: Successfully updated ATM and closed access to current account";
    }

  public static void main(String arg[]){
      try {
          AtmImpl obj = new AtmImpl();
          Naming.rebind("AtmServer", obj);
      }
      catch (Exception e) {
          System.out.println("AtmImpl Exception: " + e.getMessage());
          e.printStackTrace();
      }
  }
}
