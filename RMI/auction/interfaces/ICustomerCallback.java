package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICustomerCallback extends Remote {
    void onPriceUpdate(String itemName, int newPrice, String customer) throws RemoteException;
    void onItemSold(String itemName, int price, String customer) throws RemoteException;
    void onItemBought(String itemName, int price) throws RemoteException;
}
