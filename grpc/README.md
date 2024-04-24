# gRPC

Postavka:

Implementirati i testirati servis koji u sebi sadrzi prost celobrojni akumulator, nad kojim se moze vrsiti 3 operacije:

- INC - inkrementira vrednost akumulatora i vraca vrednost nakon inkrementiranja;
- DEC - dekrementira vrednost akumulatora i vraca vrednost nakon dekrementiranja;
- ADD op - dodaje operand `op` akumulatoru i vraca vrednost nakon dekrementiranja;
- NOP - dodatak, ne radi nista, vec samo vraca vrednost akumulatora (sluzi za naznacavanje kraja stream-ova);

> Napomena: na optimalnost lock-ova se nije mnogo obracalo paznje, oni su tu samo reprezentativno, da bi moglo vise klijenata istovremeno da pristupa servisu bez konflikata. Nisu bitni za sam koncept gRPC-a, vec su vise dobra praksa, zato sto je thread-safety nezaobilazna komponenta servera u realnim situacijama.

Sta je bitno da se zna za gRPC:

- kako se pise protobuf specifikacija, poruke (`message`), servisi (`service`) odnosno deklaracije poziva udaljenih metoda (`rpc`);
- sta je unary RPC, client streaming RPC, bidirectional streaming RPC (i pozeljno je da se razume server streaming);
- kako se implementira servis u C#-u po datoj protobuf specifikaciji;
  - izvodi se iz `ImeDeklarisanogPaketa.ImeDeklarisanogServisaBase`;
  - `override`-uju se sve RPC metode deklarisane u protobuf specifikaciji;
  - stream RPC-evi:
    - `IAsyncStreamReader` - kada klijent stream-uje;
    - `IServerStreamWriter` - kada server stream-uje;
    - `MoveNext` i `Current` metode za citanje sledeceg podatka u stream-u;

[Povratak na pocetnu stranicu](../README.md)