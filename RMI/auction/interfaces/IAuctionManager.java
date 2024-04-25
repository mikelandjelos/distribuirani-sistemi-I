package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuctionManager extends Remote {
    String getName() throws RemoteException;
    String listAuctionItems() throws RemoteException;
    void unregisterCustomer(String customerName) throws RemoteException;
    IAuctionItem getAuctionItem(int index) throws RemoteException;
}