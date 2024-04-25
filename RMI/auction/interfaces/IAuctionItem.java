package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuctionItem extends Remote {
    int getCurrentPrice() throws RemoteException;
    String getName() throws RemoteException;
    int getBidsLeft() throws RemoteException;
    void bid(String customerName, int newPrice) throws RemoteException;
    boolean isCustomerRegistered(String customerName) throws RemoteException;
    void register(String customerName, ICustomerCallback callback) throws RemoteException;
    void unregister(String customerName) throws RemoteException;
}