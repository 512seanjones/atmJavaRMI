import java.rmi.Naming;
import java.rmi.RemoteException;

public class AtmClient {
    public static void main(String arg[]){
        try {
            Atm obj = (Atm) Naming.lookup("//" + "10.0.0.201" + "/AtmServer");
            System.out.println(obj.accessAccount("0002", "5555"));
        }
        catch (Exception e) {
            System.out.println("AtmClient Exeception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
