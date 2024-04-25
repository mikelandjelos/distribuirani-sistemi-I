package implementations;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import interfaces.IAuctionManager;
import interfaces.IAuctionItem;

public class AuctionManager extends UnicastRemoteObject implements IAuctionManager {
    private String auctionName;
    private List<IAuctionItem> items;

    public AuctionManager(IAuctionItem[] _items, String _auctionName) throws RemoteException {
        auctionName = _auctionName;
        items = new ArrayList<>();

        for (var item : _items)
            items.add(item);
    }

    @Override
    public String getName() throws RemoteException {
        return auctionName;
    }

    @Override
    public void unregisterCustomer(String customerName) throws RemoteException {
        try {
            for (var item : items) {
                if (item.isCustomerRegistered(customerName))
                    item.unregister(customerName);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public IAuctionItem getAuctionItem(int index) throws RemoteException {
        return items.get(index);
    }

    @Override
    public String listAuctionItems() throws RemoteException {
        StringBuilder sb = new StringBuilder();

        try {
            int i = 1;
            for (var item : items) {
                if (item.getBidsLeft() != 0)
                    sb.append(i + ") Name: " + item.getName() + ", Current price: " +
                            item.getCurrentPrice() + ", Bids left: " + item.getBidsLeft() + "\n");
                i++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return sb.toString();
    }
}
