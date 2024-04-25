package implementations;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import interfaces.IAuctionItem;
import interfaces.ICustomerCallback;

public class AuctionItem extends UnicastRemoteObject implements IAuctionItem {
    private String name;
    private Integer bidsLeft;

    private Integer price;
    private String highestBidder;

    private Map<String, ICustomerCallback> registeredBidders;

    public AuctionItem(String itemName, int startingPrice, int bidsToEnd) throws RemoteException {
        super();
        name = itemName;
        price = startingPrice;
        registeredBidders = new HashMap<>();
        bidsLeft = bidsToEnd;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCurrentPrice() throws RemoteException {
        return price;
    }

    @Override
    public int getBidsLeft() throws RemoteException {
        return bidsLeft;
    }

    @Override
    public synchronized void bid(String customerName, int newPrice)
            throws RemoteException, IllegalArgumentException, IllegalStateException {
        if (bidsLeft == 0)
            throw new IllegalStateException("Item has been sold, cannot bid on it anymore!");

        if (newPrice <= price)
            throw new IllegalArgumentException(
                    "You can't bid with " + newPrice + ". New price must be higher than " + price + ".");

        if (!isCustomerRegistered(customerName))
            throw new IllegalArgumentException("Customer " + customerName +
                    " not registered for item " + name);

        price = newPrice;
        highestBidder = customerName;
        --bidsLeft;

        if (bidsLeft == 0) {
            ICustomerCallback callback = registeredBidders.get(highestBidder);
            callback.onItemBought(name, price);

            for (Entry<String, ICustomerCallback> entry : registeredBidders.entrySet()) {
                if (entry.getKey() == highestBidder)
                    continue;
                entry.getValue().onItemSold(name, price, highestBidder);
            }

            return;
        }

        for (Entry<String, ICustomerCallback> entry : registeredBidders.entrySet()) {
            entry.getValue().onPriceUpdate(name, price, customerName);
        }
    }

    @Override
    public synchronized void register(String customerName, ICustomerCallback customerCallback) throws RemoteException {
        registeredBidders.put(customerName, customerCallback);
    }

    @Override
    public synchronized void unregister(String customerName)
            throws RemoteException {
        registeredBidders.remove(customerName);
    }

    @Override
    public boolean isCustomerRegistered(String customerName) throws RemoteException {
        return registeredBidders.containsKey(customerName);
    }
}
