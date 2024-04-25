import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import javax.swing.text.Style;

import implementations.AuctionManager;
import interfaces.IAuctionItem;
import interfaces.IAuctionManager;
import interfaces.ICustomerCallback;

public class CustomerClient {
    private String name;
    private ICustomerCallback callback;

    public CustomerClient(String _name) {
        try {
            name = _name;
            callback = new CustomerCallback();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class CustomerCallback extends UnicastRemoteObject implements ICustomerCallback {
        public CustomerCallback() throws RemoteException {
        }

        @Override
        public void onItemBought(String itemName, int price) throws RemoteException {
            System.out.println("You bought " + itemName + " for " + price);
        }

        @Override
        public void onItemSold(String itemName, int price, String customer) throws RemoteException {
            System.out.println("Item  " + itemName + " was sold for " + price + " to " + customer);
        }

        @Override
        public void onPriceUpdate(String itemName, int newPrice, String cusotmer) throws RemoteException {
            System.err.println(cusotmer + " has made a bid of " + newPrice + " for item " + itemName);
        }
    }

    public static void main(String[] args) {
        try {
            IAuctionManager auctionManager = (IAuctionManager) Naming.lookup("OldVintageAntique");
            System.out.println("Dobro dosli na aukciju: " + auctionManager.getName());
            
            Scanner userInput = new Scanner(System.in);

            System.out.print("Unesite svoje ime: ");

            var customer = new CustomerClient(userInput.nextLine());

            biddingCycle: while (true) {
                System.out.println("Izaberite opciju:");
                System.out.println("\t1) Izlistaj dostupne predmete");
                System.out.println("\t2) Prijavi se za licitaciju predmeta");
                System.out.println("\t3) Napravi ponudu za predmet");
                System.out.println("\t4) Zavrsi licitiranje");

                var chosenOption = userInput.nextLine();
                int chosenItemIndex;
                int newPrice;
                IAuctionItem auctionItem;

                switch (chosenOption) {
                    case "1":
                        System.out.println("Izabrali ste opciju 1:");
                        System.out.println(auctionManager.listAuctionItems());
                        break;
                    case "2":
                        System.out.print("Izabrali ste opciju 1.\nIzaberite redni broj predmeta: ");

                        chosenItemIndex = Integer.parseInt(userInput.nextLine());
                        auctionItem = auctionManager.getAuctionItem(chosenItemIndex);
                        auctionItem.register(customer.name, customer.callback);

                        System.out.println("Uspesna registracija za aukciju predmeta " + auctionItem.getName());
                        break;
                    case "3":
                        System.out.println("Izabrali ste opciju 3.");

                        System.out.print("Unesite redni broj predmeta za koji zelite da izvrsite ponudu: ");
                        chosenItemIndex = Integer.parseInt(userInput.nextLine());
                        auctionItem = auctionManager.getAuctionItem(chosenItemIndex);

                        System.out.print("Izabrali ste predmet " + auctionItem.getName() +
                                ". Unesite ponudu (mora biti veca od " + auctionItem.getCurrentPrice() + "): ");

                        newPrice = Integer.parseInt(userInput.nextLine());
                        auctionItem.bid(customer.name, newPrice);

                        System.out.println("Uspesno izvrsena ponuda za predmet " + auctionItem.getName());
                        break;
                    case "4":
                        System.out.println("Izabrali ste opciju 4. Odjavljivanje sa aukcije uspesno.");
                        auctionManager.unregisterCustomer(customer.name);
                        break biddingCycle;
                    default:
                        System.out.println("Opcija " + chosenOption + " nije validna. Validne opcije su 1, 2, 3, 4.");
                        break;
                }
            }

            userInput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
