
# CV01

[zadani-sem-prace-1.pdf](zadani-sem-prace-1.pdf)

Pro zjednodušení vývoje byl místo tří serverů vytvořen jeden, který při spuštění bere parametr,
který určí jaký servlet se má použít.
Pro generování bankovních operací byla vytvořena klientská aplikace.
Správnost implementace lze ověřit z logů.

## Technologie
* Java
* Maven
* Vagrant

## Příklad konfigurace (localhost)
Sequencer vyžadje port a adresu shuffleru:
```
java -jar multi-server-1.0.0.jar sequencer 8080 http://localhost:8081
```

Shuffler vyžadje port a adresy serverů (1..n):
```
java -jar multi-server-1.0.0.jar shuffler 8081 http://localhost:8082 http://localhost:8083
```

Server vyžadje port:
```
java -jar multi-server-1.0.0.jar server 8082
java -jar multi-server-1.0.0.jar server 8083
```

Klient generující bankovní operace vyžaduje adresu sequenceru a počet:
```
java -jar client-1.0.0.jar http://localhost:8080 50
```

## Spuštění
1) `build.cmd`
2) `wsl`
3) `vagrant up`
4) `vagrant ssh sequencer`
    (pass: vagrant)
5) `cd /vagrant`
6) `java -jar client-1.0.0.jar http://10.0.3.60:8080 20`
7) `exit`
8) `vagrant destroy -f`

Logy lze pak najít v tomto adresáři.
