import java.rmi.Naming;
import java.rmi.RemoteException;

public class AtmClient {
    public static void main(String arg[]){
        try {
            String ipAddress = "10.0.0.201";
            Atm obj = (Atm) Naming.lookup("//" + ipAddress + "/AtmServer");
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
