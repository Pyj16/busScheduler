## Translations/Prevodi
- [English](#bus-scheduler)
- [Slovenščina](#avstobusni-urnik)

# Bus Scheduler

A simple application that allows the user to see upcoming bus lines on a given bus stop
two hours from the time of running in either a relative or absolute time.

## Features
- Reading from a mock database contained in a folder with text
files for stops, stoptimes, routes and trips.
- Ability to choose stop ID, max number of buses and whether the output is
relative or absolute.
- Accounting for time overflow if application is run after 22:00:00.

## Preconditions
All data must be in a text file (.txt) in the "data" folder. The names of the files must be:
- routes.txt
- stop_times.txt
- stops.txt
- trips.txt

## Execution
In the command prompt:
```
..busScheduler/src>java Main [stopID] [maxBuses] [mode]
```
- stopId: Integer. ID of the bus stop
- maxBuses: Integer. Maximum number of buses outputted
- mode: String
    - "relative" or "r": Relative representation in minutes
    - "absolute" or "a": Absolute representation in HH:mm:ss




# Avstobusni Urnik

Preprost program ki prikazuje uporabniku naslednje avtobusne linije največ 2 ur
od čas zahteve v absolutni ali relativni meri.

## Funkcionalnosti
- Branje iz simulirane baze podatkov, ki je v tekstovni obliki in vsebuje  datoteke
za postaje, čase prihoda ter linije.
- Izbiranje ID postaje, maximalno število prikazanih avtobusov in relativen ali
absoluten prikaz.
- Preverjanje in pravilna pretvorba izračuna časa če program je zaženen kasneje od 22:00:00.

## Predpogoje
Podatki morajo biti v besedilni obliki (.txt) v "data" mapi. Imena datotek morajo biti:
- routes.txt
- stop_times.txt
- stops.txt
- trips.txt

## Zagon
V terminalu:
```
..busScheduler/src>java Main [stopID] [maxBuses] [mode]
```
- stopId: Število. ID avtobusne postaje
- maxBuses: Število. Maximalno število prikazanih linij
- mode: Besedilo
  - "relative" ali "r": Relativni prikaz v minutah
  - "absolute" ali "a": Absolutni prikaz v obliki in HH:mm:ss


## Struktura projekta

- src
  - data (Vsebuje vse mokirane baze podatkov)
    - routes.txt
    - stop_times.txt
    - stops.txt
    - trips.txt
  - Main.java (Glavni program)
  - Route.java
  - Stop.java
  - StopTime.java
  - Trip.java

### Main.java
Podatki iz mape "/data" so shranjeni v HashMap in List strukturah. V primeru obstajanje dejanske SQL baze, bi bile vse te obijekte zamenjene z DAO obijekte.

Metoda "readData" bere podatke iz fiksne direktorije in ne vstavlja vse podatke ki
se najajajo na besedilni datoteki v HashMap in List obijekte.

Metoda "showStops" kot argumenti vzame id postaje, maximalno število avtobusov, maximalen čas od zagona v urah (za sedaj je fiksno na 2 uri), in 
boolean za relativni ali absolutni prikaz (true = relativen, false = absoluten).
Tudi ima override če bi želeli samo vstavili uro (je bilo uporabljeni pri testiranju).
Metoda je le prihod do metode "stopsOutput".

Metoda "stopsOutput" vzame enake parametre iz "showStops", obdeluje podatke in izpiše relevantne linije.  

### Stop.java
Vsebuje redundante atribute ki se nikoli ne uporabljajo.
Uporabljeni so le:
- int id
- String code
- String name

### Trip.class
ID tega razreda je v String obliki.

### StopTime
ID tega razreda je združitev trip_id in sequence.