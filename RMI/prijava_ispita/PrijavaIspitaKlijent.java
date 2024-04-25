import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import interfaces.EStudSluzba;
import interfaces.Student;
import interfaces.StudentPrijavioIspitCallback;

public class PrijavaIspitaKlijent {
    private String id;
    private StudentPrijavioIspitCallback callback;

    public PrijavaIspitaKlijent(String _id) {
        id = _id;

        try {
            callback = new StudentPrijavioIspitCallbackImpl();
        }
        catch (RemoteException rex) {
            rex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Ocekivan je unos 1 parametra programa (ID-a klijenta), uneseno: " + (args.length - 1));

        var klijent = new PrijavaIspitaKlijent(args[0]);

        Scanner input = new Scanner(System.in);

        EStudSluzba studSluzba;

        try {
            studSluzba = (EStudSluzba) Naming.lookup("rmi://localhost:1099/EStudSluzba");

            while (true) {
                System.out.println("Dobrodosli u korisnicki servis studentske sluzbe. Za nastavak izaberite opciju:");
                System.out.println("\ta) Prijava ispita");
                System.out.println("\tb) Provera prijavljenih ispita");
                System.out.println("\tc) Zapocni pracenje studenta");
                System.out.println("\td) Prekini pracenje studenta");
                System.out.println("\te) Kraj");

                String opcija = input.nextLine();

                String brIndeksa = null;
                Student student = null;
                String ispit = null;

                switch (opcija) {
                    case "a":
                        System.out.println("Izabrali ste opciju prijave ispita.");

                        System.out.print("Unesite broj indeksa: ");
                        brIndeksa = input.nextLine();

                        student = studSluzba.vratiStudenta(brIndeksa);
                        if (student == null) {
                            System.out.println(
                                    "Student sa brojem indeksa " + brIndeksa + " ne postoji! Prijava nije uspela.");
                            break;
                        }

                        System.out.print("Unesite ime ispita koji zelite da prijavite: ");
                        ispit = input.nextLine();

                        student.prijaviIspit(ispit);

                        System.out.println("Prijava poslata!");
                        System.out.println("==========================\n");

                        break;

                    case "b":
                        System.out.println("Izabrali ste opciju provere prijavljenih ispita.");

                        System.out.print("Unesite broj indeksa: ");
                        brIndeksa = input.nextLine();

                        student = studSluzba.vratiStudenta(brIndeksa);
                        if (student == null) {
                            System.out.println(
                                    "Student sa brojem indeksa " + brIndeksa + " ne postoji! Provera nije uspela.");
                            break;
                        }

                        System.out.println("Prijavljeni ispiti: " + student.vratiPrijavu().vratiIspite());
                        System.out.println("==========================\n");

                        break;

                    case "c":
                        System.out.println("Izabrali ste opciju za pocetak pracenja prijava studenta.");

                        System.out.print("Unesite broj indeksa: ");
                        brIndeksa = input.nextLine();

                        student = studSluzba.vratiStudenta(brIndeksa);
                        if (student == null) {
                            System.out.println(
                                    "Student sa brojem indeksa " + brIndeksa + " ne postoji! Pracenje nije uspelo.");
                            break;
                        }

                        student.zapocniPracenjePrijava(klijent.id, klijent.callback);
                        System.err.println("Uspesno pracenje sutdenta sa indeksom " + brIndeksa);

                        break;

                    case "d":

                        System.out.println("Izabrali ste opciju za zavrsetak pracenja prijava studenta.");

                        System.out.print("Unesite broj indeksa: ");
                        brIndeksa = input.nextLine();

                        student = studSluzba.vratiStudenta(brIndeksa);
                        if (student == null) {
                            System.out.println(
                                    "Student sa brojem indeksa " + brIndeksa + " ne postoji! Pracenje nije uspelo.");
                            break;
                        }

                        student.zavrsiPracenjePrijava(klijent.id);
                        System.err.println("Uspesan zavrsetak pracenja sutdenta sa indeksom " + brIndeksa);

                        break;

                    case "e":
                        System.out.println("Izabrali ste opciju za kraj.");
                        System.exit(0);

                        break;

                    default:
                        System.out.println("Opcija \"" + opcija + "\" nije validna! Pokusajte ponovo.");
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            input.close();
        }
    }

    public class StudentPrijavioIspitCallbackImpl extends UnicastRemoteObject implements StudentPrijavioIspitCallback {
        public StudentPrijavioIspitCallbackImpl() throws RemoteException {
        }

        @Override
        public void obavestiONovojPrijaviStudenta(String nazivIspita, String indeksStudenta) throws RemoteException {
            System.out.println("Student " + indeksStudenta + " je prijavio ispit " + nazivIspita);
        }
    }
}
