
# CV02

[zadani-sem-prace-2.pdf](zadani-sem-prace-2.pdf)

Síť bank, ve které autonomně a náhodně probíhají bankovní operace.
Každý uzel jednou za čas provede operaci se svým sousedem.
A my do toho můžeme zvenčí spustit snapshot, ze kterého je vidět aktuální stav a že v tom systému nevznikají ani nemizí žádné peníze.

## Technologie
* Java (Jetty, ZeroMQ)
* Maven
* Vagrant

## Ovládání
1) Suštění serveru
    ```
    java -jar target/server-1.0.0-SNAPSHOT.jar
            <port REST serveru (pro spouštění snapshotů, vypínání...)>
            <seznam *:PORT oddělené čárkou k vytvoření (bind)>
            <seznam ADRESA:PORT oddělené čárkou k připojení (connect)>
    ```
    Adresy v seznamech tvoří páry.
    Tj. n-tý z prvního seznamu a n-tný z druhého seznamu tvoří obousměrné spojení mezi dvěma bankami.
    Spojení mezi bankami musí být vždy obousměrné.

2) Spuštění snapshotu na daném uzlu
    
    Je možné s náhodným id (vrátí se v odpovědi) nebo se zvoleným:
    ```
    curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://<ADRESA>:<PORT>/snapshot
    curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://<ADRESA>:<PORT>/snapshot/<ID>
    ```

3) Získání stavu snapshotu
    
    Musí být použito id snapshotu:
    ```
    curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://<ADRESA>:<PORT>/snapshot/<ID>/status
    ```

4) Ukončení serveru
    ```
    curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://<ADRESA>:<PORT>/shutdown
    ```

Komplexní příklady viz ukázky.

## Spuštění ukázek – localhost
1) `mvn package`
2) `./run-local_2.sh` – jednoduchý příklad jen se dvěma uzly.
3) `./run-local_4.sh` – složitější příklad se čtyřmi uzly.
    ```
    1---------2
    |     __/ |
    |  __/    |
    | /       |
    4---------3
    ```
    
    Příklad předpokládaného výstupu:
    ```
    Starting...
    
    Check status...
    1: {"msg":"Balance: 4964586"}
    2: {"msg":"Balance: 4933276"}
    3: {"msg":"Balance: 5101719"}
    4: {"msg":"Balance: 4947540"}
    
    Create snapshot...
    {"msg":"Snapshot scheduled with id=42"}
    
    Waiting for snapshot to complete...
    
    Print snapshot...
    1: {"msg":"Snapshot{marker=42, status=4978838, operations=[credit 28468, credit 12311, debit 37703, credit 21734], operationsSum=62513, finished=true}"}
    2: {"msg":"Snapshot{marker=42, status=4881662, operations=[credit 26664, credit 24075], operationsSum=50739, finished=true}"}
    3: {"msg":"Snapshot{marker=42, status=5130667, operations=[], operationsSum=0, finished=true}"}
    4: {"msg":"Snapshot{marker=42, status=4871822, operations=[credit 23759], operationsSum=23759, finished=true}"}
    
    Shutting down nodes...
    1: {"msg":"Ok"}
    2: {"msg":"Ok"}
    3: {"msg":"Ok"}
    4: {"msg":"Ok"}
    ```

Logy lze pak najít v tomto adresáři.
Nastavte delší prodlevy pokud se snapshot nestačí provést (pokud finished=false).
Součet parametrů `status` a `operationsSum` dá celkovou hodnotu peněz v oběhu.

## Spuštění ukázky – více strojů (vagrant)
Stejná konfigurace jako `run-local_4.sh`.
Rozdíl je v tom, že uzly jsou spuštěny každý na vlastním virtuálním stroji s vlastní adresou.
Loguje se do stejných souborů jako výše.

1) `mvn package`
2) `vagrant up`
3) `./vagrant_run-snapshot.sh` – spustí checkout z prvního uzlu. Možno spouštět opakovaně.

    Příklad předpokládaného výstupu:
    ```
    Create snapshot with id=12575...
    {"msg":"Snapshot scheduled with id=12575"}
    
    Waiting for snapshot to complete...
    
    Print snapshot...
    1: {"msg":"Snapshot{marker=12575, status=4811664, operations=[credit 47365, credit 35932, credit 49098], operationsSum=132395, finished=true}"}
    2: {"msg":"Snapshot{marker=12575, status=4967860, operations=[], operationsSum=0, finished=true}"}
    3: {"msg":"Snapshot{marker=12575, status=4987580, operations=[], operationsSum=0, finished=true}"}
    4: {"msg":"Snapshot{marker=12575, status=5047773, operations=[credit 12414, debit 21312, credit 10393, credit 29921], operationsSum=52728, finished=true}"}
    ```

4) `vagrant destroy -f`
