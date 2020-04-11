import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*; // For reading from csv file

public class AtmImpl extends UnicastRemoteObject implements Atm {
    // If they the user enters a proper account number and pin then they
    // will be granted access to a row in the spreadsheet
    private boolean allowAccess = false; // start with no access
    private int accountRow = 0; // row 0 will contain headers
    private double currentBalance = -9999.9; // this value will be filled in
    // Setup data structures to hold csv values
    private String[] rowData = {" "," "," "," "};
    public AtmImpl() throws RemoteException {}
    public String accessAccount(String accountNum, String accountPin) {
        // Close any open accounts
        exitAccount();

        int result = 0;
        try {
            result = findRow(accountNum, accountPin);
        }
        catch (Exception e) {
            System.out.println("Find row exception: " + e.getMessage());
            e.printStackTrace();
        }

        if (result == 1) {
            // Row was found, but is being accessed by someone else
            return "AccessAccountError: This account is currently being accessed by someone else";
        }

        if (allowAccess) {
            return "AccessAccountSuccess: Access granted";
        }
        else {
            return "AccessAccountError: Could not find a matching account and pin value";
        }
    }
    public String getBalance() {
        // Check if user is logged in
        if (allowAccess) {
            return "GetBalanceSuccess: User has a current balance of " + Double.toString(currentBalance);
        }
        else {
            return "GetBalanceError: User needs to access an account";
        }
    }
    public String deposit(double amount) {
        if (amount < 0) {
            return "Unable to deposit a negative amount";
        }
        // Check if user is logged in
        if (allowAccess) {
            // Update current balance
            currentBalance += amount;
            // Update balance in atmData
            rowData[2] = Double.toString(currentBalance);
            return "DepositSuccess: User now has a balance of " + rowData[2];
        }
        else {
            return "DepositError: User needs to access an account";
        }
    }
    public String withdraw(double amount) {
        if (amount < 0) {
            return "Unable to withdraw a negative amount";
        }
        // Check if user is logged in
        if (allowAccess) {
            // Make sure user will not bring balance below zero
            if (currentBalance - amount < 0) {
                return "Unable to withdraw. Amount will bring balance below zero";
            }
            else {
                currentBalance -= amount;
                // Update balance in atmData
                rowData[2] = Double.toString(currentBalance);
                return "WithdrawSuccess: User now has a balance of " + rowData[2];
            }
        }
        else {
            return "WithdrawError: User needs to access an account";
        }
    }
    public String exitAccount() {
        // Close any open accounts
        if (allowAccess) {
            try {
                writeCSV();
            }
            catch (Exception e) {
                System.out.println("Write CSV Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Set back to default values if they were logged into another accound
        allowAccess = false;
        accountRow = 0;
        currentBalance = -9999.9;

        return "ExitAccountSuccess: Successfully updated ATM and closed access to current account";
    }
    private int findRow(String accountNum, String accountPin) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("atm.csv"));
        FileWriter fw = new FileWriter("temp-atm.csv");
        int i = 0;
        String row;
        while ((row = br.readLine()) != null) {
            String[] line = row.split(",");
            if (!allowAccess && line[0].equals(accountNum) && line[1].equals(accountPin)) {
                if (line[3] == "1") {
                    // Don't write anything and return
                    br.close();
                    fw.close();
                    return 1;
                }
                allowAccess = true;
                accountRow = i;
                currentBalance = Double.valueOf(line[2]);
                rowData = line;
                rowData[3] = "1"; // Set that the line is being accessed
                fw.append(String.join(",", rowData));
            }
            else {
                fw.append(row);
            }
            fw.append("\n");
            i++;
        }
        // Close file being read
        br.close();
        // Flush and close file being written
        fw.flush();
        fw.close();

        // Replace csv file with temp one so it contains updated access field
        File f1 = new File("temp-atm.csv");
        File f2 = new File("atm.csv");
        f2.delete();
        f1.renameTo(f2);

        return 0;
    }
    private void writeCSV() throws FileNotFoundException, IOException {
        rowData[3] = "0"; // Set that the file is no longer being accessed
        BufferedReader br = new BufferedReader(new FileReader("atm.csv"));
        FileWriter fw = new FileWriter("temp-atm.csv");
        int i = 0;
        String row;
        while ((row = br.readLine()) != null) {
            if (i == accountRow) {
                // Write over this line
                fw.append(String.join(",", rowData));
            }
            else {
                fw.append(row);
            }
            fw.append("\n");
            i++;
        }
        // Close file being read
        br.close();
        // Flush and close file being written
        fw.flush();
        fw.close();

        // Replace csv file with temp one so it contains updated fields
        File f1 = new File("temp-atm.csv");
        File f2 = new File("atm.csv");
        f2.delete();
        f1.renameTo(f2);
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
