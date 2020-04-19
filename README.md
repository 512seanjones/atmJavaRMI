# ATM using JavaRMI

## To run
__Must start server first for proper operation__
### Windows
* Server:
__Terminal 1__
    * cd mysql/bin
    * mysqld --console
__Terminal 2__
    * javac Atm.java AtmImpl.java
    * start rmiregistry 
    * java AtmImpl
    * Make sure to have atm.csv in directory that this is being run from
* Client:
    * Edit AtmClient.java to have IP address match that of the server
    * javac Atm.java AtmClient.java
    * java AtmClient

## Design
This ATM is designed to allow users to access their accounts from a sql database when providing a matching account and pin number. From there, the user can withdraw or deposit funds. The user can also access other accounts if they wish, otherwise they will need to exit the account to have their changes reflected in the ATM database.

### Atm methods
* Boolean lookupAccount(String accountNum)
    * Return true or false
* Boolean accessAccount(String accountNum, String accountPin)
    * Return true of false
* String getBalance()
    * Print success with balance or failure
* String deposit(double amount)
    * Print success with new balance or failure
* String withdraw(double amount)
    * Print success with new balance or failure
* String exitAccount()
    * Print success or failure

### Notes
* Server handles all implementation of the ATM interface, the client should only create objects and call its methods
* The database contains all accounts, pins and balances. It also contains a column that will tell if an account is currently being accessed.
    * Once a user access an account, the database will contain a 1 in the 'Accessed' column.
    * When the account is exited, the updated values will be written to it along with a 0 in the 'Accessed' column.
* Included error checking:
    * Cannot withdraw or deposit negative amounts
    * Cannot withdraw amounts that will bring balance below zero
    * Cannot access account that is already being accessed by another user
    * Cannot withdraw or deposit money unless an account has first been accessed
* There is no destructor in Java to handle writing to the file when a user exit, so that is why the exitAccount() method must be called.
