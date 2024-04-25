package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EStudSluzba extends Remote {
    Student vratiStudenta(String brIndeksa) throws RemoteException;
}
