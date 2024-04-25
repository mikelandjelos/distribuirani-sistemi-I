import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import interfaces.EStudSluzba;
import implementations.EStudSluzbaImpl;

public class PrijavaIspitaServer {

    public PrijavaIspitaServer() {
        try {
            LocateRegistry.createRegistry(1099);
            EStudSluzba studSluzba = new EStudSluzbaImpl();

            Naming.rebind("rmi://localhost:1099/EStudSluzba", studSluzba);

            System.out.println("RMI registar kreiran. Objekat EStudSluzba dodat.");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PrijavaIspitaServer();
        System.out.println("Server pokrenut.");

        try {
            System.in.read();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}