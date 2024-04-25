package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Student extends Remote {
    Prijava vratiPrijavu() throws RemoteException;

    void prijaviIspit(String ispit) throws RemoteException;

    void zapocniPracenjePrijava(String klijentId, StudentPrijavioIspitCallback callback) throws RemoteException;

    void zavrsiPracenjePrijava(String klijentId) throws RemoteException;
}
