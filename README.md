# ATM using JavaRMI

## To run
### Windows
* Server:
    * javac -d . Atm.java AtmImpl.java
    * start rmiregistry
    * java AtmImpl
* Client:
    * Edit AtmClient.java to have IP address match that of the server
    * javac -d . Atm.java AtmClient.java
    * java AtmClient
