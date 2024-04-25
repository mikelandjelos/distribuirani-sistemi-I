package implementations;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

import interfaces.Prijava;

public class PrijavaImpl extends UnicastRemoteObject implements Prijava {
    Set<String> prijavljeniIspiti;

    public PrijavaImpl(Set<String> _prijavljeniIspiti) throws RemoteException {
        prijavljeniIspiti = _prijavljeniIspiti;
    }

    @Override
    public String vratiIspite() throws RemoteException {
        return String.join(", ", prijavljeniIspiti);
    }
}
