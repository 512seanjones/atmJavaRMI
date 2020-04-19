import java.rmi.*;

public interface Atm extends java.rmi.Remote {
    // Returns true or false
    boolean lookupAccount(String accountNum) throws RemoteException;
    // Returns true or false
    boolean accessAccount(String accountNum, String accountPin) throws RemoteException;
    // Print success with balance or failure
    String getBalance() throws RemoteException;
    // Print success with new balance or failure
    String deposit(double amount) throws RemoteException;
    // Print success with new balance or failure
    String withdraw(double amount) throws RemoteException;
    // Print success or failure
    String exitAccount() throws RemoteException;
}
