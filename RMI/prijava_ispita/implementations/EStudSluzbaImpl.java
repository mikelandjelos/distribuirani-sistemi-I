package implementations;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import interfaces.EStudSluzba;
import interfaces.Student;

public class EStudSluzbaImpl extends UnicastRemoteObject implements EStudSluzba {
    private HashMap<String, Student> studenti = new HashMap<String, Student>() {
        {
            put("18430", new StudentImpl("18430"));
            put("18431", new StudentImpl("18431"));
            put("18432", new StudentImpl("18432"));
            put("18433", new StudentImpl("18433"));
            put("18434", new StudentImpl("18434"));
            put("18435", new StudentImpl("18435"));
            put("18436", new StudentImpl("18436"));
            put("18437", new StudentImpl("18437"));
            put("18438", new StudentImpl("18438"));
            put("18439", new StudentImpl("18439"));
        }
    };

    public EStudSluzbaImpl() throws RemoteException {
    }

    @Override
    public Student vratiStudenta(String brIndeksa) throws RemoteException {
        return studenti.get(brIndeksa);
    }

}
