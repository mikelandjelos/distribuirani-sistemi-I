import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import implementations.AuctionItem;
import implementations.AuctionManager;
import interfaces.IAuctionItem;
import interfaces.IAuctionManager;

public class AuctionServer {
    public AuctionServer(String auctionName) {
        try {
            IAuctionItem[] items = {
                new AuctionItem("Old luxury watch", 1500, 5),
                new AuctionItem("Vintage necklace", 15000, 10),
                new AuctionItem("Antique painting", 5000, 7)
            };

            IAuctionManager auctionManager = new AuctionManager(items, auctionName);
        
            LocateRegistry.createRegistry(1099); // Kreiramo registry servis na portu 1099
            Naming.rebind(auctionManager.getName(), auctionManager);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AuctionServer("OldVintageAntique");
        System.out.println("Auction server running on localhost:1099");
    }
}