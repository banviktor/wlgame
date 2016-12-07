Angol nyelvű szótanuló játék
============================

## Fordítás és futtatás

### Követelmények

- Java SE Development Kit (JDK) 8
- 8080-as portot más alkalmazás ne foglalja
- Tetszőleges web böngésző
- Internet kapcsolat

### Útmutató

- Windows rendszer használata esetén nyisson meg egy Command Prompt-ot
  - Navigáljon ebbe a könyvtárba
  - Írja be a következő parancsot: `gradlew.bat bootRun`
- UNIX rendszer használata esetén nyisson egy terminálablakot
  - Navigáljon ebbe a könyvtárba
  - Írja be a következő parancsot: `./gradlew bootRun`
- Nyomja meg az Enter billentyűt, aminek hatására a függőségek letöltődnek,
  az alkalmazás lefordul, és a szerver elindul a 8080-as porton hallgatózva.
  Ne zárja be a terminálablakot!
- A web böngészőben navigáljon a http://127.0.0.1:8080/ címre.

---

English word learning game
==========================

## Compile and run

### Requirements

- Java SE Development Kit (JDK) 8
- Port 8080 must not be used by other applications
- Any web browser
- Internet connection

### Guide

- Windows users shall open a Command Prompt
  - Navigate into this directory
  - Type in the following command: `gradlew.bat bootRun`
- UNIX-based OS users shall open a terminal window
  - Navigate into this directory
  - Type in the following command: `./gradlew bootRun`
- Press the Enter key, that will cause the dependencies to be downloaded,
  the application gets compiled and starts listening on port 8080.
  Do not close the terminal window!
- In the web browser navigate to http://127.0.0.1:8080/
