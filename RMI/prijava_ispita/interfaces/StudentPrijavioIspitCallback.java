package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StudentPrijavioIspitCallback extends Remote {
    void obavestiONovojPrijaviStudenta(String nazivIspita, String indeksStudenta) throws RemoteException;
}
