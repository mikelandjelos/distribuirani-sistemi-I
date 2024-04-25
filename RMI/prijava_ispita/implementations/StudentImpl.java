package implementations;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import interfaces.Prijava;
import interfaces.Student;
import interfaces.StudentPrijavioIspitCallback;

public class StudentImpl extends UnicastRemoteObject implements Student {
    private String brIndeksa;
    private Set<String> prijavljeniIspiti;
    private Map<String, StudentPrijavioIspitCallback> pratioci;

    public StudentImpl(String _brIndeksa) throws RemoteException {
        super();
        brIndeksa = _brIndeksa;
        prijavljeniIspiti = new HashSet<>();
        pratioci = new HashMap<>();
    }

    @Override
    public void prijaviIspit(String ispit) throws RemoteException {
        prijavljeniIspiti.add(ispit);
        for (var pratilac : pratioci.entrySet())
            pratilac.getValue().obavestiONovojPrijaviStudenta(ispit, brIndeksa);
    }

    @Override
    public Prijava vratiPrijavu() throws RemoteException {
        return new PrijavaImpl(prijavljeniIspiti);
    }

    public String getBrIndeksa() {
        return brIndeksa;
    }

    @Override
    public void zapocniPracenjePrijava(String klijentId, StudentPrijavioIspitCallback callback) throws RemoteException {
        pratioci.put(klijentId, callback);
    }

    @Override
    public void zavrsiPracenjePrijava(String klijentId) throws RemoteException {
        if (pratioci.containsKey(klijentId))
            pratioci.remove(klijentId);
    }
}
