package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Prijava extends Remote {
    String vratiIspite() throws RemoteException;
}
